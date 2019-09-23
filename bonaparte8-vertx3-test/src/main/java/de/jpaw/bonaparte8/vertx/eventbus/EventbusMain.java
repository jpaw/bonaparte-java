package de.jpaw.bonaparte8.vertx.eventbus;

import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.eventbus.Message;

import java.util.Date;


public class EventbusMain {
    static int cnt = 0;
    static public boolean publish = true;       // false = send.   7 seconds for send, 6 seconds for publish (publish is slightly faster)

    public static void main(String[] args) {
        Vertx vertx = Vertx.vertx();
//        Vertx vertx = Vertx.vertx();
        final EventBus eb = vertx.eventBus();
        final Date start = new Date();

        Handler<Message<String>> echoHandler = new Handler<Message<String>>() {
            @Override
            public void handle(Message<String> message) {
                String body = message.body();
                //message.reply("Meh");
                if (publish)
                    eb.publish("service.pingpong", "Meh");
                else
                    eb.send("service.pingpong", "Meh");
            }
        };
        Handler<Message<String>> pingPongHandler = new Handler<Message<String>>() {
            @Override
            public void handle(Message<String> message) {
                String body = message.body();
                //message.reply("Meh");
                if (++cnt == 1000000) {
                    Date end = new Date();
                    long millis = end.getTime() - start.getTime();
                    System.out.println("Time taken: " + millis / 1000 + " seconds for 1 mio roundtrips");
                    System.exit(1);
                } else {
                    if (publish)
                        eb.publish("service.echo", "Meh");
                    else
                        eb.send("service.echo", "Meh");
                }
            }
        };
        eb.consumer("service.echo", echoHandler);
        eb.consumer("service.pingpong", pingPongHandler);

        // kick it off
        if (publish)
            eb.publish("service.echo", "hello world");
        else
            eb.send("service.echo", "hello world");
        try {
            Thread.sleep(180000);
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        System.out.println("Timeout");

    }

}
