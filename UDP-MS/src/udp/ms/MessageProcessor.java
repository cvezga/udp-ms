package udp.ms;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

import udp.MulticastPublisher;
import udp.ms.Message.MessageType;

public class MessageProcessor {

	private String nodeName;

	private final ConcurrentHashMap<String, Message> messageMap;
	private final InQueue in;
	private final OutQueue out;

	private int messageIdCounter = 0;

	public MessageProcessor(String nodeName) {
		this.nodeName = nodeName;

		this.messageMap = new ConcurrentHashMap<>();

		this.in = new InQueue(this.messageMap, this.methodRefMap);

		this.out = new OutQueue();

	}

	public void init() {

		new Thread(in).start();

		new Thread(out).start();

	}


	public void queue(String instanceName, String target, Function<Message, String> callback, String message) {
	
		Message m = createMessage(instanceName, target, callback, message);

		messageMap.put(m.getKey(), m);

		out.add(m);

	}

	private Message createMessage(String instanceName, String target, Function<Message, String> callback, String message) {
		messageIdCounter++;
		Message m;
		if (callback != null) {
			m = new Message(this.nodeName, instanceName, target, messageIdCounter, MessageType.GET, message);
			m.setCallbackFunction(callback);
		} else {
			m = new Message(this.nodeName, instanceName, target, messageIdCounter, MessageType.SEND, message);
		}

		return m;
	}

	public void doContextProcess() {
		checkMessageTimeouts();
		fireCrons();
	}

	private void checkMessageTimeouts() {
		long time = System.currentTimeMillis();
		for (Message m : this.messageMap.values()) {
			if (m.getMessageAckTime()==0 && time > m.getTimeout()) {
				System.out.println("Timeout for: " + m + " message send count " + m.getSendCount());
				if (m.getSendCount() < 5) {
					out.add(m);
				} else {
					System.out.println("Max send retry for message: " + m);
					m.discard();
				}
			}
		}

		Iterator<Message> it = this.messageMap.values().iterator();
		while (it.hasNext()) {
			Message m = it.next();
			if (m.isDiscard())  {
				Message mm = new Message(m);
				mm.setTarget("LoggerMS");
				mm.setMessageText(m.toString().replace("|", ":")+" -> discarded");
				try {
					MulticastPublisher.multicast(mm.toString());
				} catch (IOException e) {
					e.printStackTrace();
				}

				it.remove();
			}else if (m.getMessageAckTime()>0) {
				Message mm = new Message(m);
				mm.setTarget("LoggerMS");
				long ackTime = m.getMessageAckTime()-m.getMessageTime();
				mm.setMessageText(m.toString().replace("|", ":")+" -> acktime:"+ackTime);
				try {
					MulticastPublisher.multicast(mm.toString());
				} catch (IOException e) {
					e.printStackTrace();
				}
				it.remove();
			}
		}

	}

//	private final Map<String, List<MicroService<Message,String>>> listenerMap = new HashMap<>();
//
//	public void subscribe(String topic, MicroService<Message,String> microService) {
//		List<MicroService<Message,String>> list = listenerMap.get(topic);
//		if (list == null) {
//			list = new ArrayList<>();
//			listenerMap.put(topic, list);
//		}
//		list.add(microService);
//
//	}

	private final Map<String, List<Function<Message,String>>> methodRefMap = new HashMap<>();

	public void subscribe(String topic, Function<Message,String> methodRef) {
		List<Function<Message,String>> list = methodRefMap.get(topic);
		if (list == null) {
			list = new ArrayList<>();
			methodRefMap.put(topic, list);
		}
		list.add(methodRef);
	
	}

	private List<MicroService<Message,String>> cronList = new ArrayList<>();

	public void cron(MicroService<Message,String> microService) {
		cronList.add(microService);
	}

	private void fireCrons() {
		for (MicroService<?, ?> m : cronList) {
			if (m.getCronFireTime() > 0 && System.currentTimeMillis() > m.getCronFireTime() ) {
			    try {
				   m.onCron();
			    }catch(Exception e) {
			       e.printStackTrace();
			    }finally {
				   m.resetCronFireTime();
			    }
			}
		}
	}

}
