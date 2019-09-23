package de.jpaw.bonaparte8.vertx.jwt.tests

import java.io.BufferedReader
import java.io.InputStream
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import org.junit.Test

class JwtTestRemote {
    static private final String BASE_URL = "http://localhost:8080"
    static private final int NUM_REQUESTS = 10000

    def private static String get1stLine(InputStream is) {
        val it = new BufferedReader(new InputStreamReader(is))
        val token = readLine
        close
        return token
    }

    @Test
    def public void runJWTBench() {
        main
    }

    def public static void main(String ... args) {

        val url         = new URL(BASE_URL + "/login");
        val it          = (url.openConnection as HttpURLConnection)
        requestMethod   = "GET"
        setRequestProperty("User-Agent", "xtend sample client 1.0")

        val token = get1stLine(inputStream)
        println('''Result: token = «token»''')
        disconnect

        val auth = "Bearer " + token
        val t0 = System.currentTimeMillis
        for (var int i = 0; i < NUM_REQUESTS; i += 2) {
            val url2         = new URL(BASE_URL + "/api/bench");
            val it          = (url2.openConnection as HttpURLConnection)
            requestMethod   = "GET"
            setRequestProperty("Authorization", auth)
            val respCode2    = responseCode
            disconnect
            if (respCode2 != 200)
                throw new Exception("Bad response: got code " + respCode2)
        }
        val t1 = System.currentTimeMillis
        println('''took «t1 - t0» ms for «NUM_REQUESTS» requests''')
    }
}
