package udp.ms;

import java.util.function.Function;

public class Message {

	private static final int ACK_TIMEOUT = 250;

	public enum MessageType {
		GET, GET_ACK, RESPONSE, RESPONSE_ACK, SEND, SEND_ACK, MULTICAST
	};

	private String nodeName;
	private String instanceName;
	private String target;
	private int messageId;
	private MessageType type;
	private String messageText;
	
	private long messageTime;
	private long messageAckTime;
	private long timeout;

	private Function<Message, String> callbackFunction;

	private int sendCount;
	private boolean discard;

	public Message(String nodeName, String instanceName, String target, int messageId, MessageType type,
			String message) {
		this.nodeName = nodeName;
		this.instanceName = instanceName;
		this.target = target;
		this.messageId = messageId;
		this.type = type;
		this.messageText = message;
		this.messageTime = System.currentTimeMillis();
	}

	public Message(Message message) {
		this.nodeName = message.getNodeName();
		this.instanceName = message.getInstanceName();
		this.target = message.getTarget();
		this.messageId = message.getMessageId();
		this.type = message.getType();
		this.messageText = message.getMessageText();
		this.messageTime = System.currentTimeMillis();
	}

	public Message(String textMassage) {
		String[] data = textMassage.split("\\|",-1);
		this.nodeName = data[0];
		this.instanceName = data[1];
		this.target = data[2];
		this.messageId = Integer.parseInt(data[3]);
		this.type = MessageType.values()[Integer.parseInt(data[4])];
		this.messageText = data[5];
		this.messageTime = System.currentTimeMillis();
	}

	public void resetTimeout() {
		this.timeout = System.currentTimeMillis() + ACK_TIMEOUT;
	}

	public String getKey() {
		return StringUtil.join("|", this.nodeName, this.messageId);
	}

	@Override
	public String toString() {
		return StringUtil.join("|", nodeName, instanceName, target, messageId, type.ordinal(), messageText);
	}

	public int getMessageId() {
		return messageId;
	}

	public void setMessageId(int messageId) {
		this.messageId = messageId;
	}

	public String getMessageText() {
		return messageText;
	}

	public void setMessageText(String messageText) {
		this.messageText = messageText;
	}

	public String getTarget() {
		return target;
	}

	public void setTarget(String target) {
		this.target = target;
	}

	public Function<Message, String> getCallbackFunction() {
		return callbackFunction;
	}

	public void setCallbackFunction(Function<Message, String> callbackFunction) {
		this.callbackFunction = callbackFunction;
	}

	public long getMessageTime() {
		return messageTime;
	}

	public void setMessageTime(long messageTime) {
		this.messageTime = messageTime;
	}

	public long getTimeout() {
		return timeout;
	}

	public void setTimeout(long timeout) {
		this.timeout = timeout;
	}

	public void incrementSendCount() {
		this.sendCount++;

	}

	public int getSendCount() {
		return sendCount;
	}

	public void discard() {
		this.discard = true;

	}

	public boolean isDiscard() {
		return discard;
	}

	public MessageType getType() {
		return type;
	}

	public void setType(MessageType type) {
		this.type = type;
	}

	public void setSendCount(int sendCount) {
		this.sendCount = sendCount;
	}

	public void setDiscard(boolean discard) {
		this.discard = discard;
	}

	public String getNodeName() {
		return nodeName;
	}

	public void setNodeName(String nodeName) {
		this.nodeName = nodeName;
	}

	public long getMessageAckTime() {
		return messageAckTime;
	}

	public void setMessageAckTime(long messageAckTime) {
		this.messageAckTime = messageAckTime;
	}

	public String getInstanceName() {
		return instanceName;
	}

	public void setInstanceName(String instanceName) {
		this.instanceName = instanceName;
	}

}
