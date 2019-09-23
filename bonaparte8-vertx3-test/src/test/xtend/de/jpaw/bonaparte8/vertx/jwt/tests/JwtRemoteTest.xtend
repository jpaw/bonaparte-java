package de.jpaw.bonaparte8.vertx.jwt.tests

import de.jpaw.bonaparte8.vertx.jwt.SecuredService
import io.vertx.core.Vertx
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
        vertx.deployVerticle(SecuredService.name, context.asyncAssertSuccess)
    }

    @After
    def public void tearDown(TestContext context) {
        vertx.close(context.asyncAssertSuccess)
    }

    @Test
    def public void testSecuredService(TestContext context) {
        val async = context.async

        vertx.createHttpClient.getNow(8080, "localhost", "/login", [
            handler [
                println('''Result: token = «it»''')
                context.assertTrue(toString.startsWith("eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9."))  // alg is constant
                async.complete
            ]
        ])
    }
}
