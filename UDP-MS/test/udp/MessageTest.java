package udp;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class MessageTest {
	
	
	@Test
	public void shouldValidadMessageSTXETX() {
		
		String msg = "This is the message.";
		
		String received = new StringBuilder()
				.append( Constans.S_STX )
				.append( msg )
				.append( Constans.S_ETX )
				.toString();
		
		int idx1 = received.indexOf(Constans.S_STX);
		int idx2 = received.indexOf(Constans.S_ETX);
		
		if(idx1 == -1 || idx2 == -1){
			System.err.println("Invalid massage. Did not match STX or ETX");
		 
		}

		String mesaggeBody = received.substring(idx1+1,idx2);
		
		System.out.println(mesaggeBody);
		
		assertEquals(msg, mesaggeBody);
	}

}
