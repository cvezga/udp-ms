package udp;

import java.text.DecimalFormat;

public class MemoryUtil {

	public static String getMemoryData() {
         int mb = 1024*1024;

		//Getting the runtime reference from system
		Runtime runtime = Runtime.getRuntime();

		//Print used memory
		long usedMemory =  (runtime.totalMemory() - runtime.freeMemory()) / mb;

		//Print free memory
		long freeMemory =  runtime.freeMemory() / mb;

		//Print total available memory
		long totalMemory = runtime.totalMemory() / mb;

		//Print Maximum available memory
		long maxMemory = runtime.maxMemory() / mb;
		
		return usedMemory+","+freeMemory+","+totalMemory+","+maxMemory;
	}
	
	private static DecimalFormat df = new DecimalFormat("#,###,##0");
	public static String getMemoryDataText() {
        int mb = 1024*1024;

        StringBuilder sb = new StringBuilder();
		//Getting the runtime reference from system
		Runtime runtime = Runtime.getRuntime();

		//Print used memory
		long usedMemory =  (runtime.totalMemory() - runtime.freeMemory()) / mb;
		sb.append("Used memory.: ").append(df.format(usedMemory)).append("MB\n");

		//Print free memory
		long freeMemory =  runtime.freeMemory() / mb;
		sb.append("Free memory.: ").append(df.format(freeMemory)).append("MB\n");

		//Print total available memory
		long totalMemory = runtime.totalMemory() / mb;
		sb.append("Total memory: ").append(df.format(totalMemory)).append("MB\n");

		//Print Maximum available memory
		long maxMemory = runtime.maxMemory() / mb;
		sb.append("Max memory..: ").append(df.format(maxMemory)).append("MB\n");
		
		return sb.toString();
	}

}