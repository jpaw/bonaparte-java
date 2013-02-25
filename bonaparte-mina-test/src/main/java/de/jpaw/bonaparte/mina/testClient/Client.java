
package de.jpaw.bonaparte.mina.testClient;

import java.net.InetSocketAddress;

import org.apache.mina.core.RuntimeIoException;
import org.apache.mina.core.future.ConnectFuture;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.filter.logging.LoggingFilter;
import org.apache.mina.transport.socket.nio.NioSocketConnector;

import de.jpaw.bonaparte.mina.codec.SumUpProtocolCodecFactory;




public class Client {
    private static final String HOSTNAME = "localhost";

    private static final int PORT = 8078;


    private static final long CONNECT_TIMEOUT = 30*1000L; // 30 seconds


    public static void main(String[] args) throws Throwable {



        NioSocketConnector connector = new NioSocketConnector();

        // Configure the service.
        connector.setConnectTimeoutMillis(CONNECT_TIMEOUT);

            connector.getFilterChain().addLast(
                    "codec",
                    new ProtocolCodecFilter(
                            new SumUpProtocolCodecFactory(false)));

        connector.getFilterChain().addLast("logger", new LoggingFilter());

        connector.setHandler(new ClientSessionHandler(/*values*/));

        IoSession session;
        for (;;) {
            try {
                ConnectFuture future = connector.connect(new InetSocketAddress(
                        HOSTNAME, PORT));
                future.awaitUninterruptibly();
                session = future.getSession();
                break;
            } catch (RuntimeIoException e) {
                System.err.println("Failed to connect.");
                e.printStackTrace();
                Thread.sleep(5000);
            }
        }

        // wait until the summation is done
        session.getCloseFuture().awaitUninterruptibly();
        
        connector.dispose();
    }
}
