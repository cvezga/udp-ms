package udp;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.UnknownHostException;

public class MulticastReceiver {
	

	public static void read() {
		String received = null;
		
		MulticastSocket socket = null;
		byte[] buf = new byte[256];
		
		try {
			socket = new MulticastSocket(4446);
			InetAddress group = InetAddress.getByName("230.0.0.0");
			socket.joinGroup(group);
			//while (true) {
				DatagramPacket packet = new DatagramPacket(buf, buf.length);
				socket.receive(packet);
				received = new String(packet.getData(), 0, packet.getLength());
//				if ("end".equals(received)) {
//					break;
//				}
//			}
			socket.leaveGroup(group);
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {

			socket.close();

		}
	}
}
