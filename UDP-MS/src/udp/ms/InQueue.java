package udp.ms;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

import udp.Constans;
import udp.MulticastPublisher;
import udp.ms.Message.MessageType;

public class InQueue implements Runnable {

	private MulticastSocket socket = null;

	private InetAddress group = null;

	private boolean isRunning = false;

	private final ConcurrentHashMap<String, Message> messageMap;

	private final Map<String, List<Function<Message,String>>> methodRefMap;

	public InQueue(final ConcurrentHashMap<String, Message> messageMap,
			final Map<String, List<Function<Message,String>>> methodRefMap) {
		this.messageMap = messageMap;
		this.methodRefMap = methodRefMap;
	}

	private boolean initSocket() {

		try {
			socket = new MulticastSocket(4446);
			group = InetAddress.getByName("230.0.0.0");
			socket.joinGroup(group);
			return true;
		} catch (IOException e) {
			e.printStackTrace();
		}

		return false;
	}

	@Override
	public void run() {
		try {

			isRunning = initSocket();
			if (isRunning) {
				System.out.println("InQueue initiated");
			}
			while (isRunning) {
				
				
				byte[] buf = new byte[4096];

				DatagramPacket packet = new DatagramPacket(buf, buf.length);

				socket.receive(packet);

				String received = new String(packet.getData(), 0, packet.getLength());
				
				int idx1 = received.indexOf(Constans.S_STX);
				int idx2 = received.indexOf(Constans.S_ETX);
				
				if(idx1 == -1 || idx2 == -1){
					System.err.println("Invalid massage. Did not match STX or ETX");
					continue;
				}

				String mesaggeBody = received.substring(idx1+1,idx2);
				 
								
				Message message = new Message(mesaggeBody);

				switch (message.getType()) {
				case GET:
					
					List<Function<Message,String>> functionList = this.methodRefMap.get(message.getTarget());
					if (functionList != null) {
						
						Message ack = new Message(message);
						ack.setType(MessageType.GET_ACK);
						MulticastPublisher.multicast(ack.toString());
						
						for (Function<Message,String> func : functionList) {
							
							String out = func.apply(message);
							
							if(out!=null) {
								Message response = new Message(message);
								response.setType(MessageType.RESPONSE);
								response.setMessageText(out);
								
								MulticastPublisher.multicast(response.toString());
							}
						}
					}
					break;
				case SEND:
					
					List<Function<Message,String>> sendFunctionList = this.methodRefMap.get(message.getTarget());
					if (sendFunctionList != null) {
						
						Message ack = new Message(message);
						ack.setType(MessageType.SEND_ACK);
						MulticastPublisher.multicast(ack.toString());
						
						for (Function<Message,String> func : sendFunctionList) {
							
							func.apply(message);
							
						}
					}
					break;
				case RESPONSE:
					Message rm = this.messageMap.get(message.getKey());
					if(rm!=null) {
						
						Message resAck = new Message(rm);
						resAck.setType(MessageType.RESPONSE_ACK);
						MulticastPublisher.multicast(resAck.toString());
						
						Function<Message,String> f = rm.getCallbackFunction();
					    if(f!=null) {
					       f.apply(message);	
					    }
					}
					break;
				case MULTICAST:
					if (this.methodRefMap.keySet().contains(message.getTarget())) {
						List<Function<Message, String>> funcs = this.methodRefMap.get(message.getTarget());
						if (funcs != null) {
							for(Function<Message, String> f : funcs) {
								f.apply(message);
							}
						}
					}
					break;
				case GET_ACK:
				case SEND_ACK:
				case RESPONSE_ACK:
					Message am = this.messageMap.get(message.getKey());
					if (am != null) {
						am.setMessageAckTime(System.currentTimeMillis());
					}
					break;
				default:
					System.err.println(message.getType().name() + " not supported.");
				}

			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {

			try {
				socket.leaveGroup(group);
			} catch (IOException e) {
				e.printStackTrace();
			}

			socket.close();

		}
	}

}
