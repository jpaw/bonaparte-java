package de.jpaw.bonaparte.mina.codec;

import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolEncoderOutput;
import org.apache.mina.filter.codec.demux.MessageEncoder;

import de.jpaw.bonaparte.core.BonaPortable;
import de.jpaw.bonaparte.core.ByteArrayComposer;


public class BonaparteEncoder<T extends BonaPortable> implements MessageEncoder<T> {


    public BonaparteEncoder() {
    }



    @Override
    public void encode(IoSession session, T message, ProtocolEncoderOutput out) throws Exception {


        IoBuffer buf = IoBuffer.allocate(16);
        buf.setAutoExpand(true); // Enable auto-expand for easier encoding



        ByteArrayComposer w = new ByteArrayComposer();
        w.writeRecord(message);
        buf.put(w.getBuffer(), 0, w.getLength());


        buf.flip();
        out.write(buf);
        //out.flush();



    }

}
