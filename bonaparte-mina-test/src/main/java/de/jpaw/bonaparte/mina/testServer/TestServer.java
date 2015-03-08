package de.jpaw.bonaparte.mina.testServer;

import java.net.InetSocketAddress;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.filter.logging.LoggingFilter;
import org.apache.mina.transport.socket.nio.NioSocketAcceptor;

import de.jpaw.bonaparte.mina.codec.SumUpProtocolCodecFactory;




public class TestServer {

    private final int port;

    public TestServer(int port) {
        this.port = port;
    }


  public void run() throws Exception {
      NioSocketAcceptor acceptor = new NioSocketAcceptor();

      // Prepare the service configuration.

          acceptor.getFilterChain()
                  .addLast(
                          "codec",
                          new ProtocolCodecFilter(
                                  new SumUpProtocolCodecFactory(true)));
      LoggingFilter loggingFilter = new LoggingFilter();
      acceptor.getFilterChain().addLast("logger", loggingFilter);

      acceptor.setHandler(new TestServerHandler());
      acceptor.bind(new InetSocketAddress(port));

      System.out.println("Listening on port " + port);
}

    public static void main(String[] args) throws Exception {
        int port = 8078;
        if (args.length > 0) {
            port = Integer.parseInt(args[0]);
        }
        System.out.println("Starting a test server on port " + port);
        new TestServer(port).run();
    }
}
