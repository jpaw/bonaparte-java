package de.jpaw.bonaparte.sock;

import de.jpaw.bonaparte.core.BonaPortable;

public class EchoDummy implements INetworkDialog {

    @Override
    public BonaPortable doIO(BonaPortable request) throws Exception {
        return request;
    }
}
