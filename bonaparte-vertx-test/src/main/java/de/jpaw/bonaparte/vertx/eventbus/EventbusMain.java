package de.jpaw.bonaparte.vertx.eventbus;

import java.util.Date;

import org.vertx.java.core.eventbus.EventBus;
import org.vertx.java.core.eventbus.Message;
import org.vertx.java.core.Handler;
import org.vertx.java.core.Vertx;
import org.vertx.java.core.VertxFactory;

public class EventbusMain {
    static int cnt = 0;

    public static void main(String[] args) {
        Vertx vertx = VertxFactory.newVertx();
        final EventBus eb = vertx.eventBus();
        final Date start = new Date();

        Handler<Message<String>> echoHandler = new Handler<Message<String>>() {
            public void handle(Message<String> message) {
                String body = message.body();
                //message.reply("Meh");
                eb.send("service.pingpong", "Meh");
            }
        };
        Handler<Message<String>> pingPongHandler = new Handler<Message<String>>() {
            public void handle(Message<String> message) {
                String body = message.body();
                //message.reply("Meh");
                if (++cnt == 1000000) {
                    Date end = new Date();
                    long millis = end.getTime() - start.getTime();
                    System.out.println("Time taken: " + millis / 1000 + " seconds for 1 mio roundtrips");
                    System.exit(1);
                } else {
                    eb.send("service.echo", "Meh");
                }
            }
        };
        eb.registerHandler("service.echo", echoHandler);
        eb.registerHandler("service.pingpong", pingPongHandler);

        // kick it off
        eb.send("service.echo", "Hello world");
        try {
            Thread.sleep(180000);
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        System.out.println("Timeout");

    }

}
