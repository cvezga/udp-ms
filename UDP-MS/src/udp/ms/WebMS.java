package udp.ms;

import java.io.StringReader;

import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import javax.xml.ws.Endpoint;
import javax.xml.ws.Provider;
import javax.xml.ws.Service;
import javax.xml.ws.ServiceMode;
import javax.xml.ws.WebServiceProvider;
import javax.xml.ws.http.HTTPBinding;

@WebServiceProvider
@ServiceMode(value = Service.Mode.MESSAGE)
public class WebMS extends AbstractMicroService implements Provider<Source> {

	@Override
	public void init() {
		    
		    String address = "http://127.0.0.1:8080/";
	        Endpoint.create(HTTPBinding.HTTP_BINDING, this).publish(address);

	        System.out.println("Service running at " + address);
//	        System.out.println("Type [CTRL]+[C] to quit!");
	}

	
	@Override
	public String onData(Message in) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Source invoke(Source request) {
		System.out.println("webms");
		System.out.println(request);
        return  new StreamSource(new StringReader("<p>Hello from WebMS!</p>"));
    }

}
