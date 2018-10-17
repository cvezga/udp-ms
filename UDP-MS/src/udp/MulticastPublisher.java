package udp;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.UnknownHostException;

//https://www.baeldung.com/java-broadcast-multicast

public class MulticastPublisher {

	private static InetAddress group;
	static {
		try {
			group = InetAddress.getByName("230.0.0.0");
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
	}
	
	public static void multicast(String messageText) throws IOException {
		
		String multicastMessage = new StringBuilder()
				.append( Constans.S_STX )
				.append( messageText )
				.append( Constans.S_ETX )
				.toString();
		
		byte[] buf = multicastMessage.getBytes();

		DatagramPacket packet = new DatagramPacket(buf, buf.length, group, 4446);
		
		DatagramSocket socket = new DatagramSocket();
		socket.send(packet);
		socket.close();
	}
}