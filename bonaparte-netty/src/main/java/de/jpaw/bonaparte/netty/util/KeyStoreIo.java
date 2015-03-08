package de.jpaw.bonaparte.netty.util;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.Security;
import java.security.UnrecoverableKeyException;

import javax.net.ssl.KeyManagerFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class KeyStoreIo {
    private static final Logger logger = LoggerFactory.getLogger(KeyStoreIo.class);

    static public KeyStore keyStoreFromFile(String filename) {
        KeyStore ks;
        try {
            ks = KeyStore.getInstance("JKS");
        } catch (KeyStoreException e) {
            logger.error("Cannot instantiate keystore JKS: {}", e);
            return null;
        }

        // read the password from a file in the user's HOME
        String pwFilename = filename + "storePW";
        String keyStoreFilename = filename + "store";
        logger.info("Reading keystore from file {} with PW in {}", pwFilename, keyStoreFilename);

        try (BufferedReader rpw = new BufferedReader(new FileReader(pwFilename))) {
            String line = rpw.readLine();
            rpw.close();
            // get user password
            char[] password = line.toCharArray();

            try (FileInputStream kis = new java.io.FileInputStream(keyStoreFilename)) {
                ks.load(kis, password);
                kis.close();
            } catch (Exception e) {
                logger.error("Cannot read from keystore file: {}", e);
                return null;
            }
        } catch (IOException e) {
            logger.error("Cannot read from pw file: {}", e);
            return null;
        }

        return ks;
    }

    static public KeyManagerFactory getKeyManagerFactory(String filename) {
        KeyStore ks = keyStoreFromFile(filename);
        if (ks == null) {
            return null;
        }

        String algorithm = Security.getProperty("ssl.KeyManagerFactory.algorithm");
        if (algorithm == null) {
            algorithm = "SunX509";
        }
        KeyManagerFactory kmf;
        try {
            kmf = KeyManagerFactory.getInstance(algorithm);
        } catch (NoSuchAlgorithmException e2) {
            logger.error("Cannot instantiate key manager factory: {}", e2);
            return null;
        }

        String keyPwFilename = filename + "PW";
        logger.info("Reading key password from file {}", keyPwFilename);
        try (BufferedReader rpw = new BufferedReader(new FileReader(keyPwFilename))) {
            String line = rpw.readLine();
            rpw.close();
            // get user password
            char[] keyPassword = line.toCharArray();
            kmf.init(ks, keyPassword);
        } catch (Exception e) {
            logger.error("Cannot read from key pw file: {}", e);
            return null;
        }

        return kmf;
    }



    static public KeyManagerFactory keyStoreFromFile(String keyFilename, String pwFilename, String type) {
        char[] keyPassword;
        String keyPwFilename = pwFilename;
        logger.info("Reading key password from file {}", keyPwFilename);
        try (BufferedReader rpw = new BufferedReader(new FileReader(keyPwFilename))) {
            String line = rpw.readLine();
            rpw.close();
            // get user password
            keyPassword = line.toCharArray();
        } catch (Exception e) {
            logger.error("Cannot read from key pw file: {}", e);
            return null;
        }

        KeyStore ks;
        try {
            ks = KeyStore.getInstance(type);
        } catch (KeyStoreException e) {
            logger.error("Cannot instantiate keystore {}: {}", type, e);
            return null;
        }

        try (FileInputStream kis = new java.io.FileInputStream(keyFilename)) {
            ks.load(kis, keyPassword);
            kis.close();
        } catch (Exception e) {
            logger.error("Cannot read from keystore file: {}", e);
            return null;
        }

        String algorithm = Security.getProperty("ssl.KeyManagerFactory.algorithm");
        if (algorithm == null) {
            algorithm = "SunX509";
        }
        KeyManagerFactory kmf;
        try {
            kmf = KeyManagerFactory.getInstance(algorithm);
        } catch (NoSuchAlgorithmException e2) {
            logger.error("Cannot instantiate key manager factory: {}", e2);
            return null;
        }

        try {
            kmf.init(ks, keyPassword);
        } catch (UnrecoverableKeyException | KeyStoreException | NoSuchAlgorithmException e) {
            logger.error("Cannot init kmf", e);
            return null;
        }

        return kmf;
    }

}
