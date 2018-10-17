package udp.ms;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;

import udp.Constans;
import udp.ms.Message.MessageType;

public class MSConsoleIN implements Runnable {

	private MulticastSocket socket = null;

	private InetAddress group = null;

	private boolean isRunning = false;

	private final String consoleId;

	public MSConsoleIN(String consoleId) {
		this.consoleId = consoleId;
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
				System.out.println("MSConsole InQueue initiated");
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

				if (message.getType() == MessageType.RESPONSE && consoleId.equals(message.getInstanceName())) {
					System.out.println("Response:");
					System.out.println(message.getMessageText());
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
		
		System.out.println("MSConsole InQueue ended.");
	}

}
