package de.jpaw.bonaparte.aws.test

import java.io.BufferedReader
import java.io.DataOutputStream
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import org.testng.annotations.Test

@Test
class CurrencyTest {

    def public void testHttp() {

        val String body1 = '''
<?xml version="1.0" encoding="utf-8"?>
<soap:Envelope xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
xmlns:xsd="http://www.w3.org/2001/XMLSchema"
xmlns:soap="http://schemas.xmlsoap.org/soap/envelope/">
  <soap:Body>
    <ConversionRate xmlns="http://www.webserviceX.NET/">
      <FromCurrency>EUR</FromCurrency>
      <ToCurrency>USD</ToCurrency>
    </ConversionRate>
  </soap:Body>
</soap:Envelope>
    '''

        val payload = body1.bytes   // create a serialized object

        // 2.) create a connection to the target. This does not use any of the above SSL context.
        val url = new URL("http://www.webservicex.net/CurrencyConvertor.asmx");

        val connection = url.openConnection() as HttpURLConnection => [
            doOutput                = true
            doInput                 = true
            instanceFollowRedirects = false
            requestMethod           = "POST"
            // contentType             = "application/soap+xml"
            setRequestProperty("Content-Type",   "text/xml")
            setRequestProperty("charset",        "utf-8")
            setRequestProperty("Content-Length", '''«payload.length»''')
            useCaches               = false
        ]

        val wr = new DataOutputStream(connection.outputStream) => [
            write(payload)
            flush
            close
        ]

        // 4.) retrieve the response as required
        val inputstream = connection.inputStream
        val inputstreamreader = new InputStreamReader(inputstream)
        val bufferedreader = new BufferedReader(inputstreamreader)

        var String string = null;
        while ((string = bufferedreader.readLine()) !== null) {
            System.out.println("Received " + string);
        }
        connection.disconnect();
    }
}
