package udp.ms;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import udp.HtmlUtil;

public class MonitorMS extends AbstractMicroService {

	private String nodeName;

	private Map<String, Monitor> monitorMap = new HashMap<>();

	@Override
	public void init() {
		 
		send("MonitorMS_REG","MonitorMS");
		subscribe("MonitorMS_REG", this::registerNode);
		subscribe("HeartBeatMonitorMS", this::heartBeat);
		subscribe(context.getNodeName()+":CMD:report", this::reportHeartBeat);
		subscribe(context.getNodeName()+":CMD:reportHtml", this::reportHtml);
		cron(30000);
		log.info("MonitorMS started.");
	}

	private String reportHtml(Message m) {
		return reportHtml();
		
	}

	public String reportHtml() {
		
		String resp = reportHeartBeat(null);
		
		String html = HtmlUtil.formatAsTableResponse(Arrays.asList(resp.split("\n")));
		
		return html;
	}
	

	private String reportHeartBeat(Message m) {
		  StringBuilder sb = new StringBuilder();
		 
	      List<String> keys = new ArrayList<>(this.monitorMap.keySet());
	      
	      Collections.sort(keys);
	      
	      sb.append("node,node_start_time,node_alive_time,node_loop_count,node_loop_rate,start_ts,current_ts,elapsed_ts,last_update_ts,hbCount,hbMaxRate,hbMinRate,hbRate,hbTime,usedMemory,freeMemory,totalMemory,maxMemory\n");
	      
	      for(String key : keys) {
	    	  Monitor mntr = this.monitorMap.get(key);
	    	  
	    	  String row = join(","
        	          ,mntr.nodeName
        	          ,mntr.nodeStartTime
        	          ,formatTime( System.currentTimeMillis() - mntr.nodeStartTime )
        	          ,mntr.nodeLoopCount
        	          ,mntr.nodeLoopCount / ( ( System.currentTimeMillis() - mntr.nodeStartTime ) / 1000.00 ) 
                      ,mntr.start_ts
        	    	  ,mntr.current_ts
        	    	  ,mntr.current_ts-mntr.start_ts
        	    	  ,System.currentTimeMillis() - mntr.current_ts
        	    	  ,mntr.hbCount
        	    	  ,mntr.hbMaxRate
        	    	  ,mntr.hbMinRate
        	    	  ,mntr.hbRate
        	    	  ,mntr.hbTime
        	    	  ,mntr.usedMemory
        	    	  ,mntr.freeMemory
        	    	  ,mntr.totalMemory
        	    	  ,mntr.maxMemory
	    	  );
	    	  
	    	  sb.append( row ).append( "\n" );
	    	  
	      }
		
	      return sb.toString();
	}

	private long DAY = 1000 * 60 * 60 * 24;
	private long HH = 1000 * 60 * 60;
	private long MM = 1000 * 60;
	private String formatTime(long time) {
	    int dd = ( int ) ( time / DAY ); 
	    time -=dd;
	    int hh = ( int ) ( time / HH );
	    time -=hh;
	    int mm = ( int ) ( time / MM );
        
	    return hh+"d "+hh+"h "+mm+"m";
        
	}


	@Override
	public void onCron() {
		showReport();
	}

	private void showReport() {
		for (Monitor m : monitorMap.values())
			log.info("Monitor Report: " + m);

	}

	public String registerNode(Message message) {
		log.debug("MonitorMS: "+message);
		String nodeName = message.getNodeName();
		if(!this.monitorMap.containsKey(nodeName)) {
			this.monitorMap.put(nodeName, new Monitor(nodeName, System.currentTimeMillis()));
			System.out.println(nodeName+" registered");
		}
		return null;
	}

	public String heartBeat(Message message) {
		long time = System.currentTimeMillis();
		String nodeName = message.getNodeName();
		String text = message.getMessageText();
		String[] data = text.split(",");
		int x=0;
		long nodeStartTime = Long.parseLong(data[x++]);
		long nodeLoopCount = Long.parseLong(data[x++]);
		float rate = Float.parseFloat(data[x++]);
		long count = Integer.parseInt(data[x++]);
		long elaptime = Integer.parseInt(data[x++]);
		long usedMemory = Long.parseLong(data[x++]);
		long freeMemory = Long.parseLong(data[x++]);
		long totalMemory =Long.parseLong(data[x++]);
		long maxMemory =Long.parseLong(data[x++]);
		Monitor m = this.monitorMap.get(nodeName);
		if(m!=null) {
		    m.nodeStartTime = nodeStartTime;
		    m.nodeLoopCount = nodeLoopCount;
			m.hbCount = count;
			m.hbTime = elaptime;
			m.hbRate = rate;
			m.hbMinRate = Math.min(m.hbMinRate, m.hbRate);
			m.hbMaxRate = Math.max(m.hbMaxRate, m.hbRate);
			m.usedMemory = usedMemory;
			m.freeMemory = freeMemory;
			m.totalMemory = totalMemory;
			m.maxMemory = maxMemory;
			m.current_ts = time;
		}else {
			registerNode(message);
		}
		return null;
	}

	private class Monitor {
		String nodeName;
		long nodeStartTime;
		long nodeLoopCount;
		long hbCount;
		long hbTime;
		long start_ts;
		long current_ts;
        float hbRate;
        float hbMinRate = Float.MAX_VALUE;
        float hbMaxRate;
        long usedMemory;
		long freeMemory;
		long totalMemory;
		long maxMemory;
        
        public Monitor(String nodeName, long start_ts) {
        	this.nodeName = nodeName;
        	this.start_ts = start_ts;
        }
        @Override
        public String toString() {
        	return nodeStartTime+":"+nodeLoopCount+":"+nodeName+":"+hbCount+":"+hbTime+":"+hbRate+":"+hbMinRate+":"+hbMaxRate+":"+usedMemory+":"+freeMemory+":"+totalMemory+":"+maxMemory;
        }
	}
}
