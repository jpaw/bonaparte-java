package de.jpaw.bonaparte.poi;

import java.io.IOException;
import java.io.OutputStream;

/** Common set of output methods all childs of BaseExcelComposer support. */
public interface ExcelWriter {
    public void write(OutputStream os) throws IOException;
    public void writeToFile(String filename) throws IOException;
    public byte [] getBytes() throws IOException;
}
