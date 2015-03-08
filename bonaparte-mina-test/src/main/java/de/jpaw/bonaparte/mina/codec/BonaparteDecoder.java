package de.jpaw.bonaparte.mina.codec;

import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolDecoderOutput;
import org.apache.mina.filter.codec.demux.MessageDecoder;
import org.apache.mina.filter.codec.demux.MessageDecoderResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import de.jpaw.bonaparte.core.BonaPortable;
import de.jpaw.bonaparte.core.ByteArrayParser;



public class BonaparteDecoder  implements MessageDecoder {
    private static final Logger logger = LoggerFactory.getLogger(BonaparteDecoder.class);

    public BonaparteDecoder() {
    }


    public MessageDecoderResult decodable(IoSession session, IoBuffer in) {
        if (in.remaining() < Constants.HEADER_LEN) {
            return MessageDecoderResult.NEED_DATA;
        }
        return MessageDecoderResult.OK;
    }

    public MessageDecoderResult decode(IoSession session, IoBuffer in,
            ProtocolDecoderOutput out) throws Exception {

        byte[] array;
        if (in.hasArray()) {
            if (in.arrayOffset() == 0 && in.remaining() == in.capacity()) {
                array = in.array();
            } else {


                array = new byte[in.remaining()];

                in.get(array, 0, array.length);
            }
        } else {
            array = new byte[in.remaining()];
            in.get(array, 0, array.length);
        }
        ByteArrayParser p = new ByteArrayParser(array, 0, -1);
        BonaPortable o = p.readRecord();
        logger.trace("Receiving data of class {}", o.getClass());



        out.write(o);
        return MessageDecoderResult.OK;

    }




    public void finishDecode(IoSession session, ProtocolDecoderOutput out)
            throws Exception {
    }
}
