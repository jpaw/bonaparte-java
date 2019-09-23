package de.jpaw.bonaparte8.vertx.jwt

import de.jpaw.bonaparte.core.JsonComposer
import de.jpaw.bonaparte.pojos.testobjects.StringList
import io.vertx.core.AbstractVerticle
import io.vertx.core.Vertx
import io.vertx.core.http.HttpHeaders
import io.vertx.core.json.JsonObject
import io.vertx.ext.auth.jwt.JWTAuth
import io.vertx.ext.web.Router
import io.vertx.ext.web.handler.JWTAuthHandler
import java.util.Currency
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import io.vertx.ext.jwt.JWTOptions

// 1) copy mykeystore.jceks to /tmp (or run mkstore.sh)
// 2) run this main class
// 3) run the test to benchmark authentication
public class SecuredService extends AbstractVerticle {
    private static final Logger LOGGER = LoggerFactory.getLogger(SecuredService)
    private static int port = 8080


    // doc on key store:  http://vertx.io/docs/vertx-auth-jwt/js/
    override void start() {
        super.start

        // Create a JWT Auth Provider
        val jwt = JWTAuth.create(vertx, new JsonObject()
            .put("keyStore", new JsonObject()
                .put("type",     "jceks")
                .put("path",     "/tmp/mykeystore.jceks")
                .put("password", "xyzzy5")
            )
        );

        val router = Router.router(vertx) => [
            // protect the API
            route("/api/*").handler(JWTAuthHandler.create(jwt, null));  // no exclude path because login will be separate

            // define protected services
            get("/api/bench").handler [ response.end ]
            get("/api/currencies").handler [
                LOGGER.info("currencies requested and request successfully authenticated")
                response
                    .putHeader("content-type", "application/json")
                    .end(JsonComposer.toJsonString(new StringList(Currency.availableCurrencies.map[currencyCode].toList)))
            ]

            // define a custom login service
            get("/login").handler [                                             // create a new JWT token
                response.putHeader(HttpHeaders.CONTENT_TYPE, "text/plain");
                response.end(jwt.generateToken(new JsonObject => [
                    put("tenantId", "ACME")
                    put("userId",   "john")
                ], new JWTOptions => [
                    expiresInMinutes = 10
                ]));
            ]
        ]
        vertx.createHttpServer => [
            requestHandler [ router.accept(it) ]
            listen(port)
        ]
    }


    def static void main(String[] args) throws Exception {
        LOGGER.info('''Secured server starting on port «port», use /login to authenticate, /api/currencies to test...''')

        Vertx.vertx.deployVerticle(new SecuredService)

        new Thread([Thread.sleep(60000000)]).start // wait in some other thread
    }
}
