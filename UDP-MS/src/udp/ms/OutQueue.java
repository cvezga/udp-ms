package udp.ms;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import udp.MulticastPublisher;

public class OutQueue implements Runnable {

	private final Queue<Message> queue = new ConcurrentLinkedQueue<>();

	private boolean running = false;

	@Override
	public void run() {
		running = true;
		int count = 0;
		while (running) {
			Message message = this.queue.poll();
			if (message != null) {
				try {

					message.incrementSendCount();
					MulticastPublisher.multicast(message.toString());

				} catch (Exception e) {
					e.printStackTrace();
					if (message.getSendCount() < 3) {
						this.queue.add(message);
					} else {
						System.err.println("Max sen retry for message: " + message);
					}
				}
			} else {
				count++;
				if (count > 100) {
					count = 0;
					sleep(100);
				}
			}
		}

	}

	private void sleep(int i) {
		try {
			Thread.sleep(i);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

	}

	public void add(Message message) {
		message.resetTimeout();
		queue.add(message);
	}

}
