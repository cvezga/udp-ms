package udp.ms;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class LoggerMS extends AbstractMicroService {
	
	private static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.sss");
	
	private BufferedWriter bw; 
	
	@Override
	public void init() {
		 
		send("MonitorMS_REG","LoggerMS");
		subscribe("LoggerMS", this::log );
		try {
			bw = new BufferedWriter(  new FileWriter("LoggerMS-"+System.currentTimeMillis()+".log"));
			write("timestamp");
			write(",");
			write("type");
			write(",");
			write("node name");
			write(",");
			write("message");
			write("\n");
			flush();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	
	public String log(Message message) {
		
			write(sdf.format(new Date()));
			write(",");
			write(message.getType().name());
			write(",");
			write(message.getNodeName());
			write(",");
			write(message.getMessageText());
			write("\n");
			
			flush();
			
			
		
		return null;
	}


	private void flush() {
		try {
			bw.flush();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}


	private void write(String text) {
		try {
			bw.write(text);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
