package udp.ms;

public class StringUtil {

	public static String concat(Object... text) {
		StringBuilder sb = new StringBuilder();
		for(Object t : text) {
			sb.append(t);
		}
		return sb.toString();
	}
	
	public static String join(String delimiter, Object... text) {
		StringBuilder sb = new StringBuilder();
		for(Object t : text) {
			if(sb.length()>0) sb.append(delimiter);
			sb.append(t);
			
		}
		return sb.toString();
	}
}
