package de.jpaw.bonaparte.mina.codec;


import org.apache.mina.filter.codec.demux.DemuxingProtocolCodecFactory;

import de.jpaw.bonaparte.pojos.rqrs.Request;
import de.jpaw.bonaparte.pojos.rqrs.Response;







public class SumUpProtocolCodecFactory extends DemuxingProtocolCodecFactory {

    public SumUpProtocolCodecFactory(boolean server) {
        if (server) { 
            super.addMessageDecoder(BonaparteDecoder.class);
            super.addMessageEncoder(Response.class, BonaparteEncoder.class);
        } else // Client
        {
            super.addMessageEncoder(Request.class, BonaparteEncoder.class);
            super.addMessageDecoder(BonaparteDecoder.class);
        }
    }
}
