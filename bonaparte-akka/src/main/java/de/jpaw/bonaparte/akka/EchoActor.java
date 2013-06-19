package de.jpaw.bonaparte.akka;

import akka.actor.UntypedActor;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import java.util.concurrent.atomic.AtomicInteger;

import org.joda.time.LocalDateTime;

import de.jpaw.bonaparte.pojos.rqrs.Request;
import de.jpaw.bonaparte.pojos.rqrs.Response;

public class EchoActor extends UntypedActor {
	LoggingAdapter log = Logging.getLogger(getContext().system(), this);
	static AtomicInteger threadSerial = new AtomicInteger(0);
	private AtomicInteger counterInThread = new AtomicInteger(0);
	private final int thisThreadId = 42;

	public void onReceive(Object message) throws Exception {
		if (message instanceof Request) {
			log.info("Received an object of type " + message.getClass().getCanonicalName());
			Request myRequest = (Request) message;
			Response myResponse = new Response();

			myResponse.setSerialNo(myRequest.getSerialNo());
			myResponse.setUniqueId(myRequest.getUniqueId());
			myResponse.setThreadNo(thisThreadId);
			myResponse.setSerialInThread(counterInThread.incrementAndGet());
			myResponse.setWhenReceiced(new LocalDateTime());

			if (myRequest.getDuration() > 0)
				Thread.sleep(myRequest.getDuration());
			getSender().tell(myResponse, getSelf());
		} else {
			unhandled(message);
		}
	}
}
