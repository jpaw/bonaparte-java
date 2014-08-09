package de.jpaw.bonaparte.batch;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.zip.GZIPInputStream;
import java.util.zip.ZipInputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.martiansoftware.jsap.FlaggedOption;
import com.martiansoftware.jsap.JSAP;
import com.martiansoftware.jsap.JSAPResult;
import com.martiansoftware.jsap.Switch;

abstract public class BatchReaderFile implements Contributor {
    private static final Logger LOG = LoggerFactory.getLogger(BatchReaderFile.class);
    private static final int BUFFER_SIZE = 65536;       // GZIP Buffer size (tunable constant for performance)
    private boolean useGzip = false;
    private boolean useZip = false;
    private String filename = null;
    private InputStream rawStream = null;
    protected InputStream uncompressedStream = null;    // the effective input. Subclasses can add buffering and decoding
    protected boolean isBuffered = false;               // information if this stream is buffered already, to avoid duplicate buffers 
    protected int delayInMillis = 0;
    protected int skip = 0;
    protected int maxRecords = 0;
    
    
    @Override
    public void addCommandlineParameters(JSAP params) throws Exception {
        params.registerParameter(new FlaggedOption("in", JSAP.STRING_PARSER, null, JSAP.NOT_REQUIRED, 'i', "in", "input filename (extensions .gz and .zip are understood)"));
        params.registerParameter(new Switch("inzip", JSAP.NO_SHORTFLAG, "in-zip", "unzip input file on the fly"));
        params.registerParameter(new Switch("ingzip", JSAP.NO_SHORTFLAG, "in-gzip", "gunzip input file on the fly"));
        params.registerParameter(new FlaggedOption("indelay", JSAP.INTEGER_PARSER, "0", JSAP.NOT_REQUIRED, JSAP.NO_SHORTFLAG, "in-delay", "number of ms delay between repetitions"));
        params.registerParameter(new FlaggedOption("skip", JSAP.INTEGER_PARSER, "0", JSAP.NOT_REQUIRED, 's', "skip", "number of input records to skip"));
        params.registerParameter(new FlaggedOption("maxnum", JSAP.INTEGER_PARSER, "999999999", JSAP.NOT_REQUIRED, 'm', "num", "maximum number of records to process"));
    }
    
    
    @Override
    public void evalCommandlineParameters(JSAPResult params) throws Exception {
        skip = params.getInt("skip");
        maxRecords = params.getInt("maxnum");
        delayInMillis = params.getInt("indelay");
        
        useGzip = params.getBoolean("ingzip");
        useZip = params.getBoolean("inzip");
        filename = params.getString("in");
        if (filename != null) {
            if (filename.endsWith(".gz") || filename.endsWith(".GZ"))
                useGzip = true;
            if (filename.endsWith(".zip") || filename.endsWith(".ZIP"))
                useZip = true;
        }
        // plausi check: cannot do both gzip and zip
        if (useGzip && useZip) {
            LOG.error("Cannot use both gzip and zip compression for input at the same time");
            // fatal error, terminate
            System.exit(1);
        }
        
        // command line parsed, now open the input (and check for existence of file)
        // if the file does not exist, we terminate without doing anything
        if (filename == null) {
            filename = "(stdin)";   // have something readable
            rawStream = System.in;
        } else {
            try {
                rawStream = new FileInputStream(filename);
            } catch (FileNotFoundException e) {
                LOG.error("Cannot open file {} for input", filename);
                // fatal error, terminate
                System.exit(1);
            }
        }
        if (useGzip) {
            uncompressedStream = new GZIPInputStream(rawStream, BUFFER_SIZE);
            isBuffered = true;
        } else if (useZip) {
            ZipInputStream zipInput = new ZipInputStream(rawStream);
            zipInput.getNextEntry();
            uncompressedStream = zipInput;
        } else {
            uncompressedStream = rawStream;
        }
    }
    
    @Override
    public void close() throws Exception {
        uncompressedStream.close();
    }
}
