package udp.ms;

import java.util.Date;

public class ClockMS extends AbstractMicroService {
	
	@Override
	public void init() {
		subscribe("ClockMS", this::onRequest);
		log.info("ClockMS started.");
	}

	public String onRequest(Message in) {
		log.debug("ClockMS got request");  
		return new Date().toString();
	}

}
