package de.jpaw.bonaparte8.vertx.eventbus;

import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.eventbus.Message;

import java.util.Date;

import de.jpaw.bonaparte.pojos.testobjects.EventBusTest;
import de.jpaw.bonaparte8.vertx3.SpecificCodec;


public class EventBusCodecBench {
    static int cnt = 0;
    static public boolean publish = true;       // false = send.   6 seconds for publish
    static public EventBusTest ECHO2PING = new EventBusTest("X", 97, null);
    static public EventBusTest PING2ECHO = new EventBusTest("hello", 42, null);

    public static void main(String[] args) {
        Vertx vertx = Vertx.vertx();
//        Vertx vertx = Vertx.vertx();
        final EventBus eb = vertx.eventBus();
        eb.registerDefaultCodec(EventBusTest.class, new SpecificCodec<EventBusTest>(EventBusTest.class, "ebt"));
        final Date start = new Date();

        Handler<Message<EventBusTest>> echoHandler = new Handler<Message<EventBusTest>>() {
            @Override
            public void handle(Message<EventBusTest> message) {
                EventBusTest body = message.body();
                if (body.getZ() != 42)
                    throw new RuntimeException("Got " + body.getZ() + " instead of 42");
                //message.reply("Meh");
                if (publish)
                    eb.publish("service.pingpong", ECHO2PING);
                else
                    eb.send("service.pingpong", ECHO2PING);
            }
        };
        Handler<Message<EventBusTest>> pingPongHandler = new Handler<Message<EventBusTest>>() {
            @Override
            public void handle(Message<EventBusTest> message) {
                EventBusTest body = message.body();
                if (body.getZ() != 97)
                    throw new RuntimeException("Got " + body.getZ() + " instead of 97");
                //message.reply("Meh");
                if (++cnt == 1000000) {
                    Date end = new Date();
                    long millis = end.getTime() - start.getTime();
                    System.out.println("Time taken: " + millis / 1000 + " seconds for 1 mio roundtrips");
                    System.exit(1);
                } else {
                    if (publish)
                        eb.publish("service.echo", PING2ECHO);
                    else
                        eb.send("service.echo", PING2ECHO);
                }
            }
        };
        eb.consumer("service.echo", echoHandler);
        eb.consumer("service.pingpong", pingPongHandler);

        // kick it off
        if (publish)
            eb.publish("service.echo", PING2ECHO);
        else
            eb.send("service.echo", PING2ECHO);
        try {
            Thread.sleep(180000);
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        System.out.println("Timeout");

    }

}
