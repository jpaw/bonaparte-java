package de.jpaw.bonaparte.hornetq.testSender;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.jms.BytesMessage;
import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.MessageProducer;
import javax.jms.ObjectMessage;
import javax.jms.Queue;
import javax.jms.Session;

import org.hornetq.api.core.TransportConfiguration;
import org.hornetq.api.jms.HornetQJMSClient;
import org.hornetq.api.jms.JMSFactoryType;
import org.hornetq.core.remoting.impl.netty.NettyConnectorFactory;

import de.jpaw.bonaparte.core.ByteArrayComposer;
import de.jpaw.bonaparte.demorqrs.CreateRq;
import de.jpaw.bonaparte.pojos.rqrs.Request;

public class OneThread implements Runnable {
    private final int threadIndex;
    private final boolean doObj;
    private final int callsPerThread;
    protected Queue queue = null;
    protected ConnectionFactory cf = null;
    private int portNumber = 5445; // standard netty port

    private Date start;
    private Date stop;

    OneThread(boolean doObj, int callsPerThread, int threadIndex, String queue) throws IOException {
        this.doObj = doObj;
        this.callsPerThread = callsPerThread;
        this.threadIndex = threadIndex;
        this.queue = HornetQJMSClient.createQueue(queue);

        Map<String, Object> connectionParams = new HashMap<String, Object>();
        connectionParams.put(org.hornetq.core.remoting.impl.netty.TransportConstants.PORT_PROP_NAME, portNumber);

        // we use AIO instead of NIO
        // connectionParams.put(org.hornetq.core.remoting.impl.netty.TransportConstants.USE_NIO_PROP_NAME, true);

        TransportConfiguration transportConfiguration = new TransportConfiguration(NettyConnectorFactory.class.getName(),
                connectionParams);

        // instantiate the JMS ConnectionFactory object using that TransportConfiguration
        cf = (ConnectionFactory) HornetQJMSClient.createConnectionFactoryWithoutHA(JMSFactoryType.CF,
                transportConfiguration);
    }

    @Override
    public void run() {
        Request myRequest = CreateRq.createRequest();
        myRequest.setDuration(22);
        myRequest.setSerialNo(threadIndex * 100000000);
        Connection connection = null;

        start = new Date();
        try {
            // Create a JMS Connection
            connection = cf.createConnection();

            // Create a JMS Session
            boolean useTransactional = true;
            Session session = connection.createSession(useTransactional, useTransactional
                    ? Session.SESSION_TRANSACTED
                            : Session.AUTO_ACKNOWLEDGE);

            // Create a JMS Message Producer
            MessageProducer producer = session.createProducer(queue);

            // loop through all the messages
            for (int i = 0; i < callsPerThread; ++i) {
                myRequest.setSerialNo((threadIndex * 100000000) + i);
                if (doObj) {
                    ObjectMessage message = session.createObjectMessage(myRequest);
                    producer.send(message);
                } else {
                    ByteArrayComposer bac = new ByteArrayComposer();
                    bac.writeRecord(myRequest);
                    BytesMessage message = session.createBytesMessage();
                    message.writeBytes(bac.getBuffer(), 0, bac.getLength());
                    producer.send(message);
                }
                if (useTransactional) {
                    session.commit();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Exception: " + threadIndex + " did not finish");
            return;
        }
        stop = new Date();
        long millis = stop.getTime() - start.getTime();
        double callsPerSecond = (callsPerThread * 1000) / millis;
        System.out.println("Thread result: " + (int) callsPerSecond + " calls / second");
    }
}
