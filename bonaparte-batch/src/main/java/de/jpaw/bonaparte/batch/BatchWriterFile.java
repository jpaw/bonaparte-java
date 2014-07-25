package de.jpaw.bonaparte.batch;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.zip.GZIPOutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.martiansoftware.jsap.FlaggedOption;
import com.martiansoftware.jsap.JSAP;
import com.martiansoftware.jsap.JSAPResult;
import com.martiansoftware.jsap.Switch;

abstract public class BatchWriterFile implements Contributor {
    private static final Logger LOG = LoggerFactory.getLogger(BatchWriterFile.class);
    private boolean useGzip = false;
    private boolean useZip = false;
    private String filename = null;
    private OutputStream rawStream = null;
    protected OutputStream uncompressedStream = null;  // the effective input. Subclasses can add buffering and decoding
    protected int delayInMillis = 0;
    
    
    @Override
    public void addCommandlineParameters(JSAP params) throws Exception {
        params.registerParameter(new FlaggedOption("out", JSAP.STRING_PARSER, null, JSAP.NOT_REQUIRED, 'o', "out", "output filename (extensions .gz and .zip are understood)"));
        params.registerParameter(new Switch("outzip", JSAP.NO_SHORTFLAG, "out-zip", "unzip output file on the fly"));
        params.registerParameter(new Switch("outgzip", JSAP.NO_SHORTFLAG, "out-gzip", "gunzip output file on the fly"));
        params.registerParameter(new FlaggedOption("outdelay", JSAP.INTEGER_PARSER, "0", JSAP.NOT_REQUIRED, JSAP.NO_SHORTFLAG, "out-delay", "additional throttling of output (in ms per record)"));
    }
    
    
    @Override
    public void evalCommandlineParameters(JSAPResult params) throws Exception {
        delayInMillis = params.getInt("outdelay");
        useGzip = params.getBoolean("outgzip");
        useZip = params.getBoolean("outzip");
        filename = params.getString("out");
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
        
        // command line parsed, now open the output (and check for writeability of file)
        // if the file does not exist, we terminate without doing anything
        if (filename == null) {
            filename = "(stdout)";   // have something readable
            rawStream = System.out;
        } else {
            try {
                rawStream = new FileOutputStream(filename);
            } catch (FileNotFoundException e) {
                LOG.error("Cannot open file {} for output", filename);
                // fatal error, terminate
                System.exit(1);
            }
        }
        if (useGzip) {
            uncompressedStream = new GZIPOutputStream(rawStream);
        } else if (useZip) {
            ZipOutputStream zipOutput = new ZipOutputStream(rawStream);
            zipOutput.putNextEntry(new ZipEntry(filename));
            uncompressedStream = zipOutput;
        } else {
            uncompressedStream = rawStream;
        }
    }
    
    @Override
    public void close() throws Exception {
        if (useZip)
            ((ZipOutputStream)uncompressedStream).closeEntry();
        uncompressedStream.close();
    }
}
