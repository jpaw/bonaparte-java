package de.jpaw.bonaparte.akka.testClient;

import java.util.Date;
import java.util.concurrent.TimeUnit;

import scala.concurrent.duration.Duration;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.actor.UntypedActor;
import akka.actor.UntypedActorFactory;
import akka.routing.RoundRobinRouter;
import de.jpaw.bonaparte.akka.EchoActor;
import de.jpaw.bonaparte.demorqrs.CreateRq;
import de.jpaw.bonaparte.pojos.rqrs.Request;
import de.jpaw.bonaparte.pojos.rqrs.Response;

public class AkkaBenchmark {
	private static Date start;

	static class StartBenchmarkTrigger {
	}

	static class StopBenchmarkTrigger {
	}

	public static void main(String[] args) throws Exception {
		int numberOfThreads = 4;
		int callsPerThread = 100;
		int delay = 0;

		if (args.length > 0) {
			delay = Integer.valueOf(args[0]);
		} else {
			System.out.println("Usage: AkkaBenchmark (delay in ms) [(threads) [(calls / thread)]]");
			System.exit(1);
		}
		if (args.length > 1) {
			numberOfThreads = Integer.valueOf(args[1]);
		}
		if (args.length > 2) {
			callsPerThread = Integer.valueOf(args[2]);
		}

		System.out.println("Starting benchmark with delay " + delay + " ms with " + numberOfThreads + " threads and " + callsPerThread + " calls per thread");

		start = new Date();

		DummyClassJustToMake2VariablesFinal dummy = new DummyClassJustToMake2VariablesFinal(numberOfThreads, callsPerThread);
		dummy.runTest();
	}

	public static class DummyClassJustToMake2VariablesFinal {
		private final int numberOfThreads;
		private final int callsPerThread;

		DummyClassJustToMake2VariablesFinal(int numberOfThreads, int callsPerThread) {
			this.numberOfThreads = numberOfThreads;
			this.callsPerThread = callsPerThread;
		}

		void runTest() {
			// Create an Akka system
			ActorSystem system = ActorSystem.create("MySystem");

			// create the result listener, which will print the result and
			// shutdown the system
			final ActorRef doneListener = system.actorOf(new Props(DoneListener.class), "doneListener");

			// create the master
			ActorRef dispatcher = system.actorOf(new Props(new UntypedActorFactory() {
				public UntypedActor create() {
					return new Dispatcher(numberOfThreads, callsPerThread, doneListener);
				}
			}), "dispatcher");

			// start the calculation
			dispatcher.tell(new StartBenchmarkTrigger());
		}
	}

	// #result-listener
	public static class DoneListener extends UntypedActor {
		public void onReceive(Object message) {
			if (message instanceof StopBenchmarkTrigger) {
				Date stop = new Date();
				long millis = stop.getTime() - start.getTime();
				System.out.println("Overall result: " + millis / 1000 + " seconds");
				getContext().system().shutdown();
			} else {
				unhandled(message);
			}
		}
	}

	public static class Dispatcher extends UntypedActor {
		private final int nrOfMessages;
		private int nrOfResults;
		private final long start = System.currentTimeMillis();

		private final ActorRef listener;
		private final ActorRef echoServer;

		public Dispatcher(int nrOfWorkers, int nrOfMessages, ActorRef listener) {
			this.nrOfMessages = nrOfMessages;
			this.listener = listener;

			echoServer = this.getContext().actorOf(new Props(EchoActor.class).withRouter(new RoundRobinRouter(nrOfWorkers)), "echoServer");
		}

		public void onReceive(Object message) {
			if (message instanceof StartBenchmarkTrigger) {
				for (int start = 0; start < nrOfMessages; start++) {
					Request newRequest = CreateRq.createRequest();
					newRequest.setSerialNo(start);
					echoServer.tell(newRequest, getSelf());
				}
			} else if (message instanceof Response) {
				nrOfResults += 1;
				if (nrOfResults == nrOfMessages) {
					// Send the result to the listener
					Duration duration = Duration.create(System.currentTimeMillis() - start, TimeUnit.MILLISECONDS);
					listener.tell(new StopBenchmarkTrigger(), getSelf());
					// Stops this actor and all its supervised children
					getContext().stop(getSelf());
				}
			} else {
				unhandled(message);
			}
		}
	}
}
