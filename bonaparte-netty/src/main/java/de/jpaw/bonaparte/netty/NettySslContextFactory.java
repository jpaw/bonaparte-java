package de.jpaw.bonaparte.netty;

import java.io.File;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.jpaw.bonaparte.netty.util.KeyStoreIo;


public class NettySslContextFactory {
    private static final Logger logger = LoggerFactory.getLogger(NettySslContextFactory.class);
    private static final String keyFilename = System.getProperty("user.home") + File.separator + ".key";

    private static final String PROTOCOL = "TLS";
    private static final SSLContext SERVER_CONTEXT;
    private static final SSLContext CLIENT_CONTEXT;

    static {

        SSLContext serverContext;
        SSLContext clientContext;
        KeyManagerFactory kmf = KeyStoreIo.getKeyManagerFactory(keyFilename);
        if (kmf == null) {
            throw new Error("Could not get key manager factory");
        }

        // Initialize the SSLContext to work with our key managers.
        try {
            serverContext = SSLContext.getInstance(PROTOCOL);
            serverContext.init(kmf.getKeyManagers(), null, null);
        } catch (Exception e) {
            throw new Error("Failed to initialize the server-side SSLContext", e);
        }
        try {
            clientContext = SSLContext.getInstance(PROTOCOL);
            clientContext.init(null, NettySslTrustManagerFactory.getTrustManagers(), null);
        } catch (Exception e) {
            throw new Error("Failed to initialize the client-side SSLContext", e);
        }
        logger.info("Assigning client and server SSL contexts");
        SERVER_CONTEXT = serverContext;
        CLIENT_CONTEXT = clientContext;
    }

    public static SSLContext getServerContext() {
        return SERVER_CONTEXT;
    }

    public static SSLContext getClientContext() {
        return CLIENT_CONTEXT;
    }

    private NettySslContextFactory() {
        // no instances, please
    }
}
