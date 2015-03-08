package de.jpaw.bonaparte.demorqrs;

import java.util.UUID;
import de.jpaw.bonaparte.pojos.rqrs.Request;

public class CreateRq {

    public static Request createRequest() {
        UUID myUuid = UUID.randomUUID();
        Request myRequest = new Request();
        myRequest.setDuration(0);
        myRequest.setMessage("Hello, World");
        myRequest.setSerialNo(0);
        myRequest.setUniqueId(myUuid);
        return myRequest;
    }

}
