package udp.ms;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import udp.MulticastPublisher;
import udp.ms.Message.MessageType;

public class MSConsole {

	private static int messageId = 0; 
	
	public static void main(String[] args) throws IOException {

		String msNodeName = ( args.length == 0 ? "dummy" : args[0] );
		
		final String consoleId = "MSConsole."+System.currentTimeMillis();

		MSConsoleIN in = new MSConsoleIN(consoleId);
		
		new Thread(in).start();
		
		sleep(500);
		
		System.out.println("MSConsoleId: " + consoleId);
		System.out.println("MSNode: " + msNodeName);
		System.out.println(msNodeName+">");

		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

		String line = null;
		while (!"exit".equals(line)) {
			if (System.in.available() > 0) {
				line = br.readLine();
				
				messageId++;
				
				String[] data = line.split(" ");
				String command = data[0];
				
				Message message = new Message("MSConsole", consoleId, msNodeName+":CMD:"+command, messageId, MessageType.GET, line);
				
				System.out.println(message);
				
				MulticastPublisher.multicast(message.toString());
				
				System.out.println(msNodeName+">");

			} else {
				sleep(100);
			}
		}

	}

	private static void sleep(int ms) {
		try {
			Thread.sleep(ms);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

	}

}
