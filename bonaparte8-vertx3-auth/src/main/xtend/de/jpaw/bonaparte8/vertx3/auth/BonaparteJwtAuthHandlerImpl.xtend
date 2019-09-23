package de.jpaw.bonaparte8.vertx3.auth

import de.jpaw.bonaparte.api.auth.JwtConverter
import de.jpaw.bonaparte.core.MapParser
import de.jpaw.bonaparte.pojos.api.auth.JwtInfo
import de.jpaw.bonaparte.pojos.api.auth.JwtPayload
import io.vertx.core.Handler
import io.vertx.core.json.JsonObject
import io.vertx.ext.jwt.JWT
import io.vertx.ext.web.RoutingContext
import java.io.File
import java.io.FileInputStream
import java.security.KeyStore
import org.slf4j.Logger
import org.slf4j.LoggerFactory

import static io.vertx.core.http.HttpHeaders.*
import de.jpaw.bonaparte8.vertx3.auth.BonaparteVertxUser
import java.io.InputStream
import java.io.FileNotFoundException
import de.jpaw.bonaparte.core.MimeTypes
import io.vertx.ext.jwt.JWTOptions

// BonaparteJwtAuthHandler does not implement an AuthHandler, because that one requires a SessionHandler!
class BonaparteJwtAuthHandlerImpl implements Handler<RoutingContext> {
    private static final String PAYLOAD_PQON = JwtPayload.BClass.INSTANCE.getPqon();
    private static final Logger LOGGER = LoggerFactory.getLogger(BonaparteJwtAuthHandlerImpl)
    private JWT jwt = null

    public new(File keyStore, String password) throws FileNotFoundException {
        this(new FileInputStream(keyStore), password)
    }

    public new(InputStream in, String password) {
        try {
            val ks = KeyStore.getInstance("jceks")
            ks.load(in, password.toCharArray())
            in.close

            jwt = new JWT(ks, password.toCharArray())
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    def public String sign(JwtInfo claims, Integer expiresInSeconds, String algorithm) {
        val options = new JWTOptions
        if (expiresInSeconds !== null)
            options.expiresInSeconds = expiresInSeconds
        if (algorithm !== null)
            options.algorithm = algorithm
        val jwtMap = JwtConverter.asMap(claims);
        return jwt.sign(new JsonObject(jwtMap), options)
    }

    // overridable auth methods. null means no supported format, true = authenticated, false = rejected
    def protected void authenticate(RoutingContext ctx, String authorizationHeader) {
        LOGGER.debug("unsupported authentication method");
        ctx.response.statusMessage = "unsupported authentication method"
        ctx.fail(403)
    }

    override handle(RoutingContext ctx) {
        if (ctx.user !== null) {
            // probably a session handler has been used
            LOGGER.debug("user exists; seems to be authenticated already")
            ctx.next
            return
        }
        val authorizationHeader = ctx.request.headers.get(AUTHORIZATION)
        if (authorizationHeader === null) {
            ctx.response.statusMessage = "No Authorization http Header"
            ctx.fail(401)
            return
        }
        LOGGER.debug("authenticating header field {}", authorizationHeader)
        var String reasonOfFailureMsg = null
        if (!authorizationHeader.startsWith("Bearer ")) {
            ctx.authenticate(authorizationHeader)
            return
        } else {
            try {
                val jwtToken = authorizationHeader.substring(7).trim
                val map = jwt.decode(jwtToken).map
                // if no object type has been specified, provide the default type to avoid a warning, unless fqon or pqon is specified
                val pqon1 = map.get(MimeTypes.JSON_FIELD_PQON);
                val pqon2 = map.get(MimeTypes.JSON_FIELD_FQON);
                if (!((pqon1 !== null && pqon1 instanceof String) || (pqon2 !== null && pqon2 instanceof String)))
                    map.put(MimeTypes.JSON_FIELD_PQON, PAYLOAD_PQON);

                val info = JwtConverter.parseJwtInfo(MapParser.asBonaPortable(map, JwtPayload.meta$$this) as JwtPayload)
                val now = System.currentTimeMillis
                if (info.issuedAt !== null && info.issuedAt.isAfter(now)) {
                    reasonOfFailureMsg = '''JWT token pretends to be issued in the future by «info.issuedAt.millis - now» ms'''
                } else if (info.notBefore !== null && info.notBefore.isAfter(now)) {
                    reasonOfFailureMsg = '''JWT token not yet valid'''
                } else if (info.expiresAt !== null && info.expiresAt.isBefore(now)) {
                    reasonOfFailureMsg = '''JWT token has expired by «now - info.expiresAt.millis» ms'''
                } else if (info.userId === null || info.userRef === null) {
                    reasonOfFailureMsg = '''no user ID / ref specified'''
                } else {
                    ctx.user = new BonaparteVertxUser(jwtToken, info)
                    ctx.next
                    return
                }
            } catch (Exception e) {
                LOGGER.debug("Exception during JWT decode: {}", e.message)
                reasonOfFailureMsg = "Failed to decode http header Authorization JWT"
            }
        }
        LOGGER.debug("authentication fails: {}", reasonOfFailureMsg)
        // ctx.response.statusCode = 403
        ctx.response.statusMessage = reasonOfFailureMsg
        ctx.fail(403)
    }
}
