package testcases;

import java.util.UUID;

import org.junit.jupiter.api.Test;

import de.jpaw.bonaparte.core.BonaPortable;
import de.jpaw.bonaparte.pojos.rqrs.Request;
import de.jpaw.bonaparte.pojos.rqrs.Response;
import de.jpaw.bonaparte.sock.SimpleTcpClient;

public class SingleRequest {
    static final int MY_SERIAL = 23487234;
    static int port = 8077;

    @Test
    public void testRqRs() throws Exception {
        UUID myUuid = UUID.randomUUID();
        Request myRequest = new Request();
        myRequest.setDuration(0);
        myRequest.setMessage("Hello, World");
        myRequest.setSerialNo(MY_SERIAL);
        myRequest.setUniqueId(myUuid);

        SimpleTcpClient myClient = new SimpleTcpClient("localhost", port, false);

        BonaPortable someResponse = myClient.doIO(myRequest);
        assert someResponse != null : "Received null response";
        System.out.println("Received a response of type " + someResponse.getClass().getCanonicalName());
        assert someResponse instanceof Response : "Response is of wrong type";
        Response myResponse = (Response)someResponse;
        assert myRequest.getUniqueId().equals(myResponse.getUniqueId());

    }
}
