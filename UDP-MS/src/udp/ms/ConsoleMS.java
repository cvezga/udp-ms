package udp.ms;

import udp.MemoryUtil;

public class ConsoleMS extends AbstractMicroService {
	
	@Override
	public void init() {
	 
		subscribe(context.getNodeName()+":CMD:test", this::test);
		subscribe(context.getNodeName()+":CMD:hello", this::hello);
		subscribe(context.getNodeName()+":CMD:mem", this::mem);
		subscribe(context.getNodeName()+":CMD:gc", this::gc);
		subscribe(context.getNodeName()+":CMD:ping", this::ping);
		subscribe(context.getNodeName()+":CMD:status", this::status);
		System.out.println("ConsoleMS subscribed to: "+context.getNodeName()+":CMD:test");
		log.info("ClockMS started.");
	}
	
	public String status(Message in) {
		return "Status:\n"
				+"Node Name: "+context.getNodeName()
				+"Time runing: "+context.getRunningTime();
	}

	public String ping(Message in) {
		return "pong";
	}

	public String test(Message in) {
		return "test command is working.";
	}

	public String hello(Message in) {
		return "Hello from " + context.getNodeName();
	}
	
	public String mem(Message in) {
		return MemoryUtil.getMemoryDataText();
	}
	
	public String gc(Message in) {
		System.gc();
		return "System.gc() was called.";
	}
	
}
