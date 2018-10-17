package udp.ms;

import java.util.function.Function;

public interface MicroService<IN,OUT> {
	
	void init();
	
	OUT onData(IN in);
	
	void setMessageProcessor(MessageProcessor messageProcessor);

	long getCronFireTime();

	void onCron();

	void resetCronFireTime();
	
	void call(String target, Function<Message, String> callback, String message);
	
	void send(String target, String message);
	
	void subscribe(String topic, Function<Message, String> callback);
	
	void cron(long cronTime);

	String onMulticast(Message message);
	
	String getInstanceName();
	
	void setContext(Context context);

	void setInstanceName(String instanceName);
}
