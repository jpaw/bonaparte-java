package de.jpaw.bonaparte.netty.util;

import java.security.cert.X509Certificate;

import javax.net.ssl.SSLPeerUnverifiedException;
import javax.net.ssl.SSLSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SessionInfo {
    private static final Logger logger = LoggerFactory.getLogger(SessionInfo.class);

    /*
     * to extract the DN, we would need extra JARs from org.bouncycastle.asn1 public static String certToCn(X509Certificate cert) { X500Principal principal =
     * cert.getSubjectX500Principal();
     *
     * X500Name x500name = new X500Name( principal.getName() ); RDN cn = x500name.getRDNs(BCStyle.CN)[0]);
     *
     * return IETFUtils.valueToString(cn.getFirst().getValue()); }
     */

    public static void logSessionInfo(SSLSession session, String who) {
        // print the certificate chain (if any)
        try {
            logger.info(who + "'s principal name is {}", session.getPeerPrincipal().getName());
            java.security.cert.Certificate[] clientCerts = session.getPeerCertificates();
            if ((clientCerts != null) && (clientCerts.length > 0)) {
                // have at least one server certificate - index 0 is the peer itself, further entries are the chain entries, the last entry is the root.
                for (int i = 0; i < clientCerts.length; i++) {
                    logger.debug(who + "'s certificate chain[{}] = {}", i, clientCerts[i].toString());
                }
                if (clientCerts[0] instanceof X509Certificate) {
                    X509Certificate cert = (X509Certificate) clientCerts[0];
                    logger.info(who + "'s certificate DN is {}", cert.getSubjectX500Principal().getName()); // same output as the principal above!
                }
            }
        } catch (SSLPeerUnverifiedException e) {
            logger.info("Using an SSL connection, but the peer could not be verified");
        }

    }

}
