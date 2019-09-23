package de.jpaw.bonaparte8.vertx3.auth

import de.jpaw.bonaparte.pojos.api.auth.JwtInfo
import de.jpaw.bonaparte8.vertx3.auth.BonaparteJwtAuthHandlerImpl
import io.vertx.core.AbstractVerticle
import io.vertx.core.Vertx
import io.vertx.core.buffer.Buffer
import io.vertx.core.impl.VertxInternal
import io.vertx.ext.web.Router
import io.vertx.ext.web.RoutingContext
import io.vertx.ext.web.handler.BodyHandler
import org.slf4j.Logger
import org.slf4j.LoggerFactory

import static io.vertx.core.http.HttpHeaders.*

public class BonaparteVertxAuthTestServer extends AbstractVerticle {
    private static final Logger LOGGER = LoggerFactory.getLogger(BonaparteVertxAuthTestServer)

    def static void error(RoutingContext it, int errorCode) {
        response.statusCode = errorCode
        response.end
    }


    def void rpcHandler(RoutingContext it) {
        LOGGER.info('''POST /rpc received...''')

        val info = user?.principal?.map?.get("info")
        if (info === null || !(info instanceof JwtInfo)) {
            throw new RuntimeException("No user defined or of bad type: Missing auth handler?")
        }
        val info2 = info as JwtInfo
        val ct = request.headers.get(CONTENT_TYPE)
        if (ct === null) {
            response.statusMessage = '''Undefined Content-Type'''
            error = 415
            return
        }
        val resp = '''The request was «body»!'''
        LOGGER.info("Processed request {} for tenant {}", body.toString, info2.tenantId)
        response.end(Buffer.buffer(resp))
    }

    // doc on key store:  http://vertx.io/docs/vertx-auth-jwt/js/
    override void start() {
        super.start
        val authHandler = new BonaparteJwtAuthHandlerImpl((vertx as VertxInternal).resolveFile("/tmp/mykeystore.jceks"), "xyzzy5")
        LOGGER.info("Auth test server verticle started")
        val router = Router.router(vertx) => [
            // login path
            get("/login").handler [                                             // create a new JWT token
                LOGGER.info("Logging in for locale {}", preferredLocale)
                response.putHeader(CONTENT_TYPE, "text/plain");
                response.end(authHandler.sign(new JwtInfo => [
                        tenantId    = "ACME"
                        userId      =  "john"
                        userRef     = 4711L
                    ], 600, null));
            ]
            // protect the API
            route("/rpc").handler(authHandler);
            post("/rpc").handler(BodyHandler.create)
            post("/rpc").handler [rpcHandler]
        ]
        vertx.createHttpServer => [
            requestHandler [ router.accept(it) ]
            listen(8080)
        ]
    }


    def static void main(String[] args) throws Exception {
        Vertx.vertx.deployVerticle(new BonaparteVertxAuthTestServer)

        new Thread([Thread.sleep(100000)]).start // wait in some other thread
    }
}
