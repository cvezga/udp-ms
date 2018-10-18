package udp.ms;

import java.util.function.Function;

public abstract class AbstractMicroService implements MicroService<Message, String>{

	protected MessageProcessor messageProcessor;
	
	private long cronTime = 0;
	
	private  long  firetime = 0;
	
	protected String instanceName;
	
	protected Context context;
	
	 
	
	
	@Override
	public void subscribe(String topic, Function<Message,String> methodRef) {
		messageProcessor.subscribe(topic, methodRef);
		
	}
	
	@Override
	public void cron(long cronTime) {
		this.cronTime = cronTime;
		this.firetime = System.currentTimeMillis() + cronTime;
		messageProcessor.cron(this);
	}

	@Override
	public void call(String target, Function<Message, String> callback, String message) {
		messageProcessor.queue(getInstanceName(), target, callback, message);
	}

	@Override
	public void send(String target, String message) {
		messageProcessor.queue(getInstanceName(), target, null, message);
	}
	
	static enum LOG_LEVEL { debug, info, warn, error, fatal };
	
	protected Log log = new Log();
	
	protected  class Log { 
	
	
		private void log(LOG_LEVEL level, String message) {
			send("LoggerMS", level.name() + ":" + message);
		}

		public void debug(String message) {
			log(LOG_LEVEL.debug, message);
		}

		public void info(String message) {
			log(LOG_LEVEL.info, message);
		}

		public void warn(String message) {
			log(LOG_LEVEL.warn, message);
		}

		public void error(String message) {
			log(LOG_LEVEL.error, message);
		}

		public void fatal(String message) {
			log(LOG_LEVEL.fatal, message);
		}

	}
	
	protected String join(String delitimer, Object... values) {
	    StringBuilder sb = new StringBuilder();
	    for(Object obj : values) {
	        if(sb.length() > 0) sb.append( delitimer );
	        sb.append( obj );
	    }
	    return sb.toString();
	}

	@Override
	public void setMessageProcessor(MessageProcessor messageProcessor) {
		this.messageProcessor = messageProcessor;
	}

	@Override
	public long getCronFireTime() {
		return this.firetime;
	}

	@Override
	public void onCron() {
		throw new RuntimeException("onCron was not overwritten by implementation.");
	}

	@Override
	public String onData(Message in) {
		throw new RuntimeException("onData was not overwritten by implementation.");
	}

	@Override
	public void resetCronFireTime() {
		this.firetime = System.currentTimeMillis() + this.cronTime;
	}

	@Override
	public String onMulticast(Message message) {
		return null;
	}

	@Override
	public String getInstanceName() {
		return instanceName;
	}

	@Override
	public void setInstanceName(String instanceName) {
		this.instanceName = instanceName;
	}
	
	@Override
	public void setContext(Context context) {
		this.context = context;
		
	}
}
