package udp.ms;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.HashMap;
import java.util.Map;

public class Web2MS extends AbstractMicroService {
	
	private String port;

	private ServerSocketChannel serverSocketChannel;
	
	private Map<String, EndPoint> endPointMap = new HashMap<>(); 

	@Override
	public void init() {
	 
		try {
			serverSocketChannel = ServerSocketChannel.open();
			serverSocketChannel.socket().bind(new InetSocketAddress(Integer.parseInt(port)));
			serverSocketChannel.configureBlocking(false);

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		cron(250);

	}

	@Override
	public void onCron() {
		SocketChannel socketChannel = null;
		try {
			socketChannel = serverSocketChannel.accept();
			if (socketChannel != null) {
				
				long startTime = System.currentTimeMillis();
				
				StringBuilder sb = new StringBuilder();
								
				while(sb.indexOf("\r\n\r\n")==-1) {
					ByteBuffer buf = ByteBuffer.allocate(4096);
					int bytesRead = socketChannel.read(buf);
					if(bytesRead>0) {
						buf.flip();
						String chunk = new String(buf.array(), 0, bytesRead);
						sb.append(chunk);
					}
					if(System.currentTimeMillis() - startTime > 10000 ) {
						socketChannel.finishConnect();
						socketChannel.close();
						throw new Exception("Web2MS request read timeout");
					}
				}

				
				String input = sb.toString();
				
				String[] data = input.split(" ");
				String urlEndpoint = data[1].substring(1);
				
				
				String resp="null";
				EndPoint ep = endPointMap.get(urlEndpoint);
				if(ep!=null) {
					try {
						resp = (String) ep.method.invoke(ep.objRef);
					} catch (IllegalAccessException e) {
						e.printStackTrace();
					} catch (IllegalArgumentException e) {
						e.printStackTrace();
					} catch (InvocationTargetException e) {
						e.printStackTrace();
					}
				}else {
					
					
					
					resp="No endpoint "+urlEndpoint+" found.\n"
				    +"--------------------\n"+input+"--------------------\n";
					
					for(String p : this.endPointMap.keySet()) {
						resp+=p+"\n";
					}
				}
				

				// writting

				String newData = "HTTP/1.1 200 OK\n"+
						"Date: Mon, 27 Jul 2009 12:28:53 GMT\n"+
						"Server: Apache/2.2.14 (Win32)\n"+
						"Last-Modified: Wed, 22 Jul 2009 19:15:56 GMT\n"+
						"Content-Length: "+resp.length()+"\n"+
						"Content-Type: text/html\n"+
						"Connection: Closed\n\n"+
						resp+"\n\n";

				ByteBuffer buf = ByteBuffer.allocate(4096);
				
				buf.clear();
				buf.put(newData.getBytes());

				buf.flip();

				while (buf.hasRemaining()) {
					socketChannel.write(buf);
				}

			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (socketChannel != null) {
					socketChannel.finishConnect();
					socketChannel.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
	}

	@Override
	public String onData(Message in) {

		return null;
	}

	public void addEndPoint(String args) throws NoSuchMethodException, SecurityException {
		
		String[] params = args.split("\\|");
		
		String endPoint = params[0];
		String[] instanceMethod = params[1].split("\\.");
		
		String instanceName = instanceMethod[0];
		String methodName = instanceMethod[1];
		
		Object instance = context.getInstance(instanceName);
		
		EndPoint ep = new EndPoint();
		
		ep.name = endPoint;
		ep.objRef = instance;
		ep.method = instance.getClass().getDeclaredMethod(methodName);
		
		this.endPointMap.put(ep.name, ep);
	}
	
	class EndPoint {
		
		String name;
		Object objRef;
		Method method;
		
	}
	
}
