package udp.ms;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

public class ObjectFactory {

	private static Map<String, MicroService<String, String>> msMap = new HashMap<>();

	public static MicroService<String, String> create(String instanceName, String className,
			MessageProcessor messageProcessor) {

		if (msMap.containsKey(instanceName)) {
			throw new RuntimeException("Instace name already defined: " + instanceName);
		}

		MicroService<String, String> ms = null;

		try {
			ms = (MicroService<String, String>) Class.forName(className).newInstance();
			ms.setMessageProcessor(messageProcessor);
			msMap.put(instanceName, ms);

		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}

		return ms;

	}

	public static void setAttributeValue(String instanceName, String attributeName, String value) {
		MicroService<String, String> ms = msMap.get(instanceName);
		if (ms == null) {
			throw new RuntimeException("Instace name does not exist: " + instanceName);
		}

		try {
			Field f = ms.getClass().getDeclaredField(attributeName);
			if (f == null) {
				throw new RuntimeException("Error getting attribute: " + attributeName);
			}
			f.setAccessible(true);
			if (f.getType() == long.class) {
				f.set(ms, Long.parseLong(value));
			} else {
				f.set(ms, value);
			}

		} catch (NoSuchFieldException e) {
			e.printStackTrace();
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public static void callMethod(String instanceName, String method, String params) {
		MicroService<String, String> ms = msMap.get(instanceName);
		if (ms == null) {
			throw new RuntimeException("Instace name does not exist: " + instanceName);
		}

		try {
			Method m = ms.getClass().getDeclaredMethod(method, String.class);
			if (m == null) {
				throw new RuntimeException("Error getting method: " + method);
			}

			m.invoke(ms, params);

		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
