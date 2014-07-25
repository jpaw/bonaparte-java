package de.jpaw.bonaparte.batch;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

import com.martiansoftware.jsap.FlaggedOption;
import com.martiansoftware.jsap.JSAP;
import com.martiansoftware.jsap.JSAPResult;

/** Text file input class.
 * Provides the method String getNextLine();
 * 
 */
abstract public class BatchReaderTextFileAbstract extends BatchReaderFile {
    private Charset encoding = StandardCharsets.UTF_8;
    private BufferedReader bufferedReader = null;  // the buffered and decoded input.
    private int recordCounter = 0;                  // used to count so we can do max records at most
    
    @Override
    public void addCommandlineParameters(JSAP params) throws Exception {
        super.addCommandlineParameters(params);
        params.registerParameter(new FlaggedOption("incs", JSAP.STRING_PARSER, null, JSAP.NOT_REQUIRED, JSAP.NO_SHORTFLAG, "in-charset", "input encoding (default is UTF-8, help to get a list of available character sets)"));
    }
    
    
    @Override
    public void evalCommandlineParameters(JSAPResult params) throws Exception {
        encoding = Util.charsetFromStringWithHelp(params.getString("incs"));
        
        // encoding has been clarified. Now technically everything is fine, get the actual file. That will provide the stream uncompressedStream in the superclass
        super.evalCommandlineParameters(params);
        
        // provide the buffering and charset decoding on top...
        bufferedReader = new BufferedReader(new InputStreamReader(uncompressedStream, encoding));       
    }
    
    public String getNext() throws IOException, InterruptedException {
        while (skip > 0) {
            // ignore some records
            String line = bufferedReader.readLine();
            if (line == null)
                return null;  // EOF before we started...
            --skip;
        }
        if (maxRecords > 0 && recordCounter < maxRecords) {
            ++recordCounter;
            if (delayInMillis > 0)
                Thread.sleep(delayInMillis);
            return bufferedReader.readLine();
        } else {
            // max. number reached: do not return more records
            return null;
        }
    }
}
