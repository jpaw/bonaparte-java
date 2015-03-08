package de.jpaw.bonaparte.core;

/** Methods implemented by parsers which can tell at which position a problem has occured, in order to support diagnostics. */
public interface ParsePositionProvider {
    /** Returns the character offset (or byte offset, whichever is more suitable) of the current position,
     *  or -1 is no meaningful position can be determined. */
    int getParsePosition();

    /** Returns the name fo the class which contains the problem, or some static name for the outer class. */
    String getCurrentClassName();

    /** A default implementation, to be used as a fallback, in case no suitable data can be provided. */
    static ParsePositionProvider DEFAULT = new ParsePositionProvider() {

        @Override
        public int getParsePosition() {
            return -1;
        }

        @Override
        public String getCurrentClassName() {
            return "record";
        }
    };
}
