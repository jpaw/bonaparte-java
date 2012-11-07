package de.jpaw.bonaparte.netty.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.Security;

import javax.net.ssl.KeyManagerFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class KeyStoreIo {
    private static final Logger logger = LoggerFactory.getLogger(KeyStoreIo.class);

	static public KeyStore keyStoreFromFile() {
        KeyStore ks;
        try {
	        ks = KeyStore.getInstance("JKS");
        } catch (KeyStoreException e) {
        	logger.error("Cannot instantiate keystore JKS: {}", e.getStackTrace());
	        return null;
        }

        // read the password from a file in the user's HOME
        String pwFilename = System.getProperty("user.home") + File.separator + ".keystorePW";
        String keyStoreFilename = System.getProperty("user.home") + File.separator + ".keystore";
        logger.info("Reading keystore from file {} with PW in {}", pwFilename, keyStoreFilename);

        try (BufferedReader rpw = new BufferedReader(new FileReader(pwFilename))) {
        	String line = rpw.readLine();
        	rpw.close();
            // get user password
            char[] password = line.toCharArray();
            
            try (FileInputStream kis = new java.io.FileInputStream(keyStoreFilename)) {
                ks.load(kis, password);
                kis.close();
            } catch(Exception e) {
            	logger.error("Cannot read from keystore file: {}", e.getStackTrace());
    	        return null;
            }
        } catch(IOException e) {
        	logger.error("Cannot read from pw file: {}", e.getStackTrace());
	        return null;
        }
		
        return ks;
	}

	static public KeyManagerFactory getKeyManagerFactory() {
        KeyStore ks = keyStoreFromFile();
        if (ks == null)
        	return null;
		
        String algorithm = Security.getProperty("ssl.KeyManagerFactory.algorithm");
        if (algorithm == null) {
            algorithm = "SunX509";
        }
	    KeyManagerFactory kmf;
        try {
	        kmf = KeyManagerFactory.getInstance(algorithm);
        } catch (NoSuchAlgorithmException e2) {
        	logger.error("Cannot instantiate key manager factory: {}", e2.getStackTrace());
	        return null;
        }
	    
        String keyPwFilename = System.getProperty("user.home") + File.separator + ".keyPW";
        logger.info("Reading key password from file {}", keyPwFilename);
        try (BufferedReader rpw = new BufferedReader(new FileReader(keyPwFilename))) {
        	String line = rpw.readLine();
        	rpw.close();
            // get user password
            char[] keyPassword = line.toCharArray();
            kmf.init(ks, keyPassword);
        } catch(Exception e) {
        	logger.error("Cannot read from key pw file: {}", e.getStackTrace());
	        return null;
        }

        return kmf;
	}

}
