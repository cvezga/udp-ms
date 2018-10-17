package com.cvezga.sendorreader;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

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
    
    private List<DataPoint> dataPoints = new ArrayList<>();
    private long removeDataTime;

    @Override
    public void init()
    {
        subscribe( "GET-SENDOR-DATA:"+sensorId, this::getSensorData );
        cron( readFrequency );
    }

    public String getSensorData(Message message) {
        StringBuilder sb = new StringBuilder();
        
        removeOldData();
        
        for(DataPoint dp : this.dataPoints)
        {
            sb.append( dp.getTime() ).append( "," ).append( dp.getValue() ).append( "," );
        }
        
        return sb.toString();
    }
    
    @Override
    public void onCron()
    {
        removeOldData();
        
        Socket socket = null;
        try
        {
            socket = new Socket( ip, port );
            socket.setKeepAlive( false );
            socket.setSoTimeout( timeout );
            
            InputStream is = socket.getInputStream();
            OutputStream os = socket.getOutputStream();
            
            protocol.handle(dataPoints,is,os);
 
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
    
    private void removeOldData()
    {
        long now = System.currentTimeMillis();
        long timeout = now - removeDataTime;
        
        Iterator<DataPoint> it = dataPoints.iterator();
        while( it.hasNext() )
        {
            DataPoint dp = it.next();
            if (  dp.getTime() < timeout) {
                it.remove();
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

    
    public void setRemoveDataTime( long removeDataTime )
    {
        this.removeDataTime = removeDataTime;
    }

}
