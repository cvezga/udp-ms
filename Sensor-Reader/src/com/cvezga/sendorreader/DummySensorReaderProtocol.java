package com.cvezga.sendorreader;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.Random;

public class DummySensorReaderProtocol implements SensorReaderProtocol
{

    private Random r = new Random();

    @Override
    public void handle( List<DataPoint> dataPoints, InputStream is, OutputStream os )
    {

        for ( int i = 0; i < 10000; i++ )
        {
            DataPoint dp = new DataPoint( System.currentTimeMillis(), r.nextInt( 500 ) );
            dataPoints.add( dp );

            sleep( 10 );
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

}
