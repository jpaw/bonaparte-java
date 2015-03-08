package de.jpaw.bonaparte.core;

/** Represents the /dev/null sink, discarding all input. */
public class NullComposer extends NoOpComposer<RuntimeException> implements BufferedMessageComposer<RuntimeException> {
    static public final byte [] EMPTY_RESULT = new byte[0];

    public NullComposer() {
    }


    @Override
    public void reset() {
    }

    @Override
    public int getLength() {
        return 0;
    }

    /** returns an immutable result buffer */
    @Override
    public byte[] getBuffer() {
        return EMPTY_RESULT;
    }

    /** returns a mutable result buffer */
    @Override
    public byte[] getBytes() {
        return new byte[0];
    }

}
