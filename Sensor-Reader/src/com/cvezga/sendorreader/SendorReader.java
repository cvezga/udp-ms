package com.cvezga.sendorreader;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;

import udp.ms.AbstractMicroService;
import udp.ms.Message;

public class SendorReader extends AbstractMicroService
{

    private String ip;

    private int port;

    private int readFrequency;

    private int timeout = 5000;

    private SensorReaderProtocol protocol;
    
    private String sensorId;

    @Override
    public void init()
    {
        subscribe( "GET-SENDOR-DATA:"+sensorId, this::getSensorData );
        cron( readFrequency );
    }

    public String getSensorData(Message message) {
        
        return null;
    }
    
    @Override
    public void onCron()
    {
        Socket socket = null;
        try
        {
            socket = new Socket( ip, port );
            socket.setKeepAlive( false );
            socket.setSoTimeout( timeout );
            
            InputStream is = socket.getInputStream();
            OutputStream os = socket.getOutputStream();
            
            protocol.handle(is,os);
 
        }
        catch (SocketTimeoutException  e) {
            e.printStackTrace();
        }
        catch ( UnknownHostException e )
        {
            e.printStackTrace();
        }
        catch ( IOException e )
        {
            e.printStackTrace();
        }
        finally
        {
            try
            {
                socket.close();
            }
            catch ( IOException e )
            {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }

    public void setIp( String ip )
    {
        this.ip = ip;
    }

    public void setPort( int port )
    {
        this.port = port;
    }

    public void setReadFrequency( int readFrequency )
    {
        this.readFrequency = readFrequency;
    }

    public void setProtocol( SensorReaderProtocol protocol )
    {
        this.protocol = protocol;
    }

    public void setTimeout( int timeout )
    {
        this.timeout = timeout;
    }

    
    public void setSensorId( String sensorId )
    {
        this.sensorId = sensorId;
    }

}
