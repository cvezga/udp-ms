package udp.ms;

public class SayHelloMS extends AbstractMicroService {

	@Override
	public void init() {
		 
		send("MonitorMS_REG","SayHelloMS");
		cron(1000);
	}

	@Override
	public void onCron() {

		System.out.println("Hello from SayHelloMS.");
		
	}
 
}
