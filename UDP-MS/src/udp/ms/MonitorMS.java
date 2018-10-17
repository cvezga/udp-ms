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
	      
	      sb.append("node,start_ts,current_ts,elapsed_ts,last_update_ts,hbCount,hbMaxRate,hbMinRate,hbRate,hbTime,usedMemory,freeMemory,totalMemory,maxMemory\n");
	      
	      for(String key : keys) {
	    	  Monitor mntr = this.monitorMap.get(key);
	    	  sb.append(mntr.nodeName);
	    	  sb.append(",");
	    	  sb.append(mntr.start_ts);
	    	  sb.append(",");
	    	  sb.append(mntr.current_ts);
	    	  sb.append(",");
	    	  sb.append(mntr.current_ts-mntr.start_ts);
	    	  sb.append(",");
	    	  sb.append(System.currentTimeMillis() - mntr.current_ts);
	    	  sb.append(",");
	    	  sb.append(mntr.hbCount);
	    	  sb.append(","); 
	    	  sb.append(mntr.hbMaxRate);
	    	  sb.append(","); 
	    	  sb.append(mntr.hbMinRate);
	    	  sb.append(",");
	    	  sb.append(mntr.hbRate);
	    	  sb.append(",");
	    	  sb.append(mntr.hbTime);
	    	  sb.append(",");
	    	  sb.append(mntr.usedMemory);
	    	  sb.append(",");
	    	  sb.append(mntr.freeMemory);
	    	  sb.append(",");
	    	  sb.append(mntr.totalMemory);
	    	  sb.append(",");
	    	  sb.append(mntr.maxMemory);
	    	  sb.append("\n");
	      }
		
	      return sb.toString();
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
		float rate = Float.parseFloat(data[0]);
		long count = Integer.parseInt(data[1]);
		long elaptime = Integer.parseInt(data[2]);
		long usedMemory = Long.parseLong(data[3]);
		long freeMemory = Long.parseLong(data[4]);
		long totalMemory =Long.parseLong(data[5]);
		long maxMemory =Long.parseLong(data[6]);
		Monitor m = this.monitorMap.get(nodeName);
		if(m!=null) {
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
        	return nodeName+":"+hbCount+":"+hbTime+":"+hbRate+":"+hbMinRate+":"+hbMaxRate+":"+usedMemory+":"+freeMemory+":"+totalMemory+":"+maxMemory;
        }
	}
}
