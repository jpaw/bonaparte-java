package de.jpaw.bonaparte8.vertx.jwt.tests

import java.util.Base64
import org.junit.Test

import static extension de.jpaw.bonaparte.api.auth.JwtConverter.*

class JwtTest {
    private static final String TOKEN = "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJ0ZW5hbnRJZCI6IkFDTUUiLCJ1c2VySWQiOiJqb2huIiwiaWF0IjoxNDQ2NzQ1NTk4LCJleHAiOjE0NDY3NDYxOTh9.x4V-9MYQIGYXn77axbc_BaE4Ry98F1osB_Ed4sNHv9M="

    @Test
    def public void testToken() {
        val bytes = TOKEN.split("\\.").toList.map[Base64.urlDecoder.decode(it)]
        val algStr = new String(bytes.get(0))
        val payloadStr = new String(bytes.get(1))

        println('''Alg portion is     «algStr»''')
        println('''Payload portion is «payloadStr»''')
        println('''Length of sig bytes is «bytes.get(2).length»''')

        // convert alg and payload into object
        val alg = algStr.parseAlg
        val payload = payloadStr.parsePayload

        println('''Alg is «alg»''')
        println('''Payload is «payload»''')

    }
}
