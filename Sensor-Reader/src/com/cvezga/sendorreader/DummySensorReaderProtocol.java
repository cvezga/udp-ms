package com.cvezga.sendorreader;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

public class DummySensorReaderProtocol implements SensorReaderProtocol
{

    private List<DataPoint> dataPoints = new ArrayList<>();
    private Random r = new Random();
    private long removeDataTime;
    
    
    @Override
    public void handle( InputStream is, OutputStream os )
    {
        
        removeOldData();
       
        for(int i=0; i<10000; i++)
        {
            DataPoint dp = new DataPoint( System.currentTimeMillis(), r.nextInt( 500 ) );
            dataPoints.add( dp );
            
            sleep(10);
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


    private void sleep( int i )
    {
        try
        {
            Thread.sleep( i );
        }
        catch ( InterruptedException e )
        {
            e.printStackTrace();
        }
        
    }


    
    public void setRemoveDataTime( long removeDataTime )
    {
        this.removeDataTime = removeDataTime;
    }

}
