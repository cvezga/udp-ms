package udp.ms;

import java.util.StringJoiner;

import udp.MemoryUtil;

public class HeartBeatMS extends AbstractMicroService {
	
	private String heartBeatMonitor;
	private long heartBeatTime; 
	
	private long lastReportedTime;
	
	private long count;
	
	@Override
	public void init() {
		 
	    lastReportedTime=System.currentTimeMillis();
	    send("MonitorMS_REG","HeartBeatMS");
		cron(1000);
	}

	@Override
	public void onCron() {
		long time =  System.currentTimeMillis();
		long elapsetTime = time - lastReportedTime;
		
		count++;
		
		
		if(elapsetTime>=heartBeatTime) {
		  float rate = 	count/(elapsetTime/1000f);
		  send(heartBeatMonitor, join(",", context.nodeStartTime, context.nodeLoopCount, rate, count, elapsetTime, MemoryUtil.getMemoryData()));
		  lastReportedTime=time;
		}
		
	}



	

}
