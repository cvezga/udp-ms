package udp.ms;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

public class MSNode {

	private static List<MicroService<String, String>> msList = new ArrayList<>();

	private static MessageProcessor messageProcessor;

	private static Context context = new Context();
	
	public static void main(String[] args)
			throws InstantiationException, IllegalAccessException, ClassNotFoundException {

		String configFileName = args[0];
		
		String nodeName = configFileName.replace(".config", "");
	
		context.set("nodeName", nodeName);
		
		messageProcessor = new MessageProcessor(nodeName);

		context.set("messageProcessor", messageProcessor);
		
		loadConfig("common.config");
		
		loadConfig(configFileName);
	
		messageProcessor.init();
		for (MicroService<String, String> ms : msList) {
			ms.init();
		}

		/////////////////////////////////////

		int cylceCount = 0;
		while (true) {
			try {
				messageProcessor.doContextProcess();

			} catch (Exception e) {
				e.printStackTrace();
			}
			cylceCount++;
			if (cylceCount % 100 == 0) {
				cylceCount = 0;
				sleep(100);
			}
		}

	}

	private static void loadConfig(String configFileName) {
		System.out.println("Loading "+configFileName);

		try (Stream<String> lines = Files.lines(Paths.get(configFileName))) {

			lines.forEach(line -> {
				if(line.startsWith("create ")) {
					String[] data = line.split(" ");
					String instanceName = data[1];
					String className = data[2];
					MicroService<String, String> ms = ObjectFactory.create(instanceName, className, messageProcessor);
					context.addInstance(instanceName, ms);
					ms.setContext(context);
					ms.setInstanceName(instanceName);
					msList.add(ms);
				} else 	if(line.startsWith("set ")) {
					String[] data = line.split(" ");
					String key = data[1];
					String value = data[2];
					context.set(key,value);
				} else 	if(line.startsWith("load ")) {
					String[] data = line.split(" ");
					String file = data[1];
					loadConfig(file);
				} else 	if(line.startsWith("call ")) {
					String[] data = line.split(" ");
					String instanceAttribute = data[1];
					int idx = instanceAttribute.lastIndexOf(".");
					String instanceName = instanceAttribute.substring(0, idx);
					String method = instanceAttribute.substring(idx+1);
					String params = data[2];
					ObjectFactory.callMethod(instanceName, method, params);
				} else if( line.startsWith("#") || line.trim().length() == 0 ) {
				} else {
					String[] data = line.split(" ");
					String instanceAttribute = data[0];
					int idx = instanceAttribute.lastIndexOf(".");
					String instanceName = instanceAttribute.substring(0, idx);
					String attributeName = instanceAttribute.substring(idx+1);
					String value = data[1];
					ObjectFactory.setAttributeValue(instanceName, attributeName, value);
				}
				
			});

		} catch (IOException e) {
			e.printStackTrace();
			System.exit(1);
		}

	
		System.out.println("Finish loading "+configFileName);
		
	}

	private static void sleep(int i) {
		try {
			Thread.sleep(i);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
