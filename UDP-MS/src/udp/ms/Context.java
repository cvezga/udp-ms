package udp.ms;

import java.util.HashMap;
import java.util.Map;

public class Context {
	
	private Map<String,Object> instanceMap = new HashMap<>();
	
	private Map<String,Object> prop = new HashMap<>();
	
	public long nodeLoopCount = 0;
	public long nodeStartTime = 0;
	
	public void set(String key, Object value) {
		prop.put(key, value);
	}

	public String getNodeName() {
		return (String) this.prop.get("nodeName");
	}

	public String getRunningTime() {
		// TODO Auto-generated method stub
		return null;
	}

	
	public void addInstance(String instanceName, Object obj) {
		instanceMap.put(instanceName, obj);
	}
	
	public Object getInstance(String instanceName) {
		return instanceMap.get(instanceName);
	}

}
