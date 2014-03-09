package de.jpaw.bonaparte.batch;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

import com.martiansoftware.jsap.FlaggedOption;
import com.martiansoftware.jsap.JSAP;
import com.martiansoftware.jsap.JSAPResult;

/** Text file output class.
 * Provides the method write(String);
 * 
 */
abstract public class BatchWriterTextFileAbstract extends BatchWriterFile {
    private Charset encoding = StandardCharsets.UTF_8;
	private BufferedWriter bufferedWriter = null;
	
	@Override
	public void addCommandlineParameters(JSAP params) throws Exception {
		super.addCommandlineParameters(params);
		params.registerParameter(new FlaggedOption("outcs", JSAP.STRING_PARSER, null, JSAP.NOT_REQUIRED, JSAP.NO_SHORTFLAG, "out-charset", "output encoding (default is UTF-8, LIST to get a list of available character sets)"));
	}
	
	
	@Override
	public void evalCommandlineParameters(JSAPResult params) throws Exception {
		encoding = Util.charsetFromStringWithHelp(params.getString("outcs"));
		
		// encoding has been clarified. Now technically everything is fine, get the actual file. That will provide the stream uncompressedStream in the superclass
		super.evalCommandlineParameters(params);
		
		// provide the buffering and charset decoding on top...
		bufferedWriter = new BufferedWriter(new OutputStreamWriter(uncompressedStream, encoding));		
	}
	
	public void write(String line) throws IOException, InterruptedException {
		bufferedWriter.write(line);
		if (delayInMillis > 0)
			Thread.sleep(delayInMillis);
	}
	
	@Override
	public void close() throws Exception {
		bufferedWriter.flush();
		bufferedWriter.close();
		super.close();  // redundant / duplicate close call?
	}
	
}
