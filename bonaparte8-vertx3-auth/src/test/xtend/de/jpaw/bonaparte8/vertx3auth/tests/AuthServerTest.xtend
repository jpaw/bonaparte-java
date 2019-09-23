package de.jpaw.bonaparte8.vertx3auth.tests

import de.jpaw.bonaparte8.vertx3.auth.BonaparteVertxAuthTestServer
import io.vertx.core.Vertx
import io.vertx.core.http.HttpHeaders
import io.vertx.ext.unit.TestContext
import io.vertx.ext.unit.junit.VertxUnitRunner
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(VertxUnitRunner)
public class JwtRemoteTest {


    private Vertx vertx;

    @Before
    def public void setUp(TestContext context) {
        vertx = Vertx.vertx()
        vertx.deployVerticle(BonaparteVertxAuthTestServer.name, context.asyncAssertSuccess)
    }

    @After
    def public void tearDown(TestContext context) {
        vertx.close(context.asyncAssertSuccess)
    }

    @Test
    def public void testAuthServer(TestContext context) {
        val async = context.async

        vertx.createHttpClient => [
            getNow(8080, "localhost", "/login", [
                handler [
                    println('''Result: token = «it»''')
                    async.complete
                ]
            ])
        ]
    }

    @Test
    def public void testAuthServerNoAuth(TestContext context) {
        val async = context.async

        vertx.createHttpClient => [
            post(8080, "localhost", "/rpc", [
                println('''returns «statusCode» with msg «statusMessage»''')
                if (statusCode == 200)
                    endHandler [
                        println('''Result: token = «it»''')
                        async.complete
                    ]
                else
                    async.complete
            ])
            .end
        ]
    }

    @Test
    def public void testAuthServerBadAuth(TestContext context) {
        val async = context.async

        vertx.createHttpClient => [
            post(8080, "localhost", "/rpc", [
                println('''returns «statusCode» with msg «statusMessage»''')
                if (statusCode == 200)
                    endHandler [
                        println('''Result: token = «it»''')
                        async.complete
                    ]
                else
                    async.complete
            ])
            .putHeader(HttpHeaders.AUTHORIZATION, "ghghghgh.ggg.ggg")
            .end
        ]
    }

    @Test
    def public void testAuthServerBadAuth2(TestContext context) {
        val async = context.async

        vertx.createHttpClient => [
            post(8080, "localhost", "/rpc", [
                println('''returns «statusCode» with msg «statusMessage»''')
                if (statusCode == 200)
                    endHandler [
                        println('''Result: token = «it»''')
                        async.complete
                    ]
                else
                    async.complete
            ])
            .putHeader(HttpHeaders.AUTHORIZATION, "Bearer ghghghgh.ggg.ggg")
            .end
        ]
    }

    @Test
    def public void testAuthServerGoodAuth(TestContext context) {
        val async = context.async

        val clt = vertx.createHttpClient
        clt.getNow(8080, "localhost", "/login", [
            handler [
                val jwt = toString
                clt.post(8080, "localhost", "/rpc", [
                    println('''returns «statusCode» with msg «statusMessage»''')
                    if (statusCode == 200)
                        bodyHandler [
                            println('''Result: token = «it»''')
                            async.complete
                        ]
                    else
                        async.complete
                ])
                .putHeader(HttpHeaders.AUTHORIZATION, "Bearer " + jwt)
                .putHeader(HttpHeaders.CONTENT_TYPE, "application/json")
                .end('''{ "foo": "bar" }''')
            ]
        ])
    }
}
