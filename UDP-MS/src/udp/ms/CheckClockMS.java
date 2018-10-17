package udp.ms;

public class CheckClockMS extends AbstractMicroService {

	@Override
	public void init() {
		 
		send("MonitorMS_REG","CheckClockMS");
		cron(5000);
		log.info("CheckClockMS started.");
	}

	@Override
	public void onCron() {

		log.debug("Calling ClockMS");

		call("ClockMS", this::clockMsResponse, "dummy");

		log.debug("ClockMS was queued");
		
	}

	public String clockMsResponse(Message s) {
		log.info("******************** Response from ClockMS is: " + s.getMessageText());
		return null;
	}

	@Override
	public String onData(Message in) {
		// TODO Auto-generated method stub
		return null;
	}

}
