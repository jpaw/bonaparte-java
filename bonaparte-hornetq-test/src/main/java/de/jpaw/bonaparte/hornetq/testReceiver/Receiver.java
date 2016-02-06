package de.jpaw.bonaparte.hornetq.testReceiver;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import javax.jms.BytesMessage;
import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.ExceptionListener;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageListener;
import javax.jms.ObjectMessage;
import javax.jms.Queue;
import javax.jms.Session;

import org.hornetq.api.core.TransportConfiguration;
import org.hornetq.api.jms.HornetQJMSClient;
import org.hornetq.api.jms.JMSFactoryType;
import org.hornetq.core.remoting.impl.netty.NettyConnectorFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.jpaw.bonaparte.core.BonaPortable;
import de.jpaw.bonaparte.core.ByteArrayParser;
import de.jpaw.bonaparte.core.MessageParserException;

public class Receiver implements MessageListener, ExceptionListener {
    private static final Logger LOGGER = LoggerFactory.getLogger(Receiver.class);
    private final Queue queue;
    private final boolean doObjectMessages;
    private final ConnectionFactory cf;
    private final AtomicInteger numOk;
    private final AtomicInteger numExc;
    private int portNumber = 5445; // standard netty port

    Receiver(boolean doObjQueue) {
        queue = HornetQJMSClient.createQueue("exampleQueue");

        Map<String, Object> connectionParams = new HashMap<String, Object>();

        connectionParams.put(org.hornetq.core.remoting.impl.netty.TransportConstants.PORT_PROP_NAME, portNumber);

        // we use AIO instead of NIO
        // connectionParams.put(org.hornetq.core.remoting.impl.netty.TransportConstants.USE_NIO_PROP_NAME, true);

        TransportConfiguration transportConfiguration = new TransportConfiguration(NettyConnectorFactory.class.getName(), connectionParams);

        // instantiate the JMS ConnectionFactory object using that TransportConfiguration
        cf = (ConnectionFactory) HornetQJMSClient.createConnectionFactoryWithoutHA(JMSFactoryType.CF, transportConfiguration);
        numOk = new AtomicInteger(0);
        numExc = new AtomicInteger(0);
        doObjectMessages = doObjQueue;
    }

    private void run() {
        Connection connection = null;
        try {

            // create a queue connection
            connection = cf.createConnection();

            // // create a queue session
            // QueueSession queueSession = queueConn.createQueueSession(false,
            // Session.AUTO_ACKNOWLEDGE);
            //
            // Session session = connection.createQueueSession(false,
            // Session.AUTO_ACKNOWLEDGE);
            //
            // // create a queue receiver
            // QueueReceiver queueReceiver = queueSession.createReceiver(queue);

            Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);

            // create a queue receiver
            MessageConsumer consumer = session.createConsumer(queue);

            // set an asynchronous message listener
            consumer.setMessageListener(this);

            // set an asynchronous exception listener on the connection
            connection.setExceptionListener(this);

            // start the connection
            connection.start();

            for (;;) {
                int beforeOk = numOk.get();
                int beforeExc = numExc.get();
                Thread.sleep(1000);
                int afterOk = numOk.get();
                int afterExc = numExc.get();
                if (afterOk > beforeOk) {
                    LOGGER.info("Read {} messages OK, {} exceptions", afterOk - beforeOk, afterExc - beforeExc);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            LOGGER.error("an error occurred: " + e);
        }
    }

    @Override
    public void onException(JMSException exception) {
        LOGGER.error("an error occurred: " + exception);

    }

    @Override
    public void onMessage(Message message) {

        try {
            if (doObjectMessages) {
                ObjectMessage msg = (ObjectMessage) message;
                @SuppressWarnings("unused")
                BonaPortable obj = (BonaPortable)msg.getObject();
                numOk.incrementAndGet();
            } else {
                byte [] arr = new byte[1000];
                BytesMessage msg = (BytesMessage) message;
                int len = msg.readBytes(arr);
                if (len > 0) {
                    ByteArrayParser p = new ByteArrayParser(arr, 0, len);
                    @SuppressWarnings("unused")
                    BonaPortable obj = p.readRecord();
                    numOk.incrementAndGet();
                }
            }
        } catch (JMSException ex) {
            numExc.incrementAndGet();
        } catch (MessageParserException ex) {
            numExc.incrementAndGet();
        }

    }

    /**
     * @param args
     */
    public static void main(String[] args) {
        Receiver r = new Receiver((args.length > 0) && args[0].equals("obj"));
        r.run();
    }

}
