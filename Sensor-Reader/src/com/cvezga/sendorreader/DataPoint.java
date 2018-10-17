package com.cvezga.sendorreader;


public class DataPoint
{

    private long time;
    private long value;
    
    public DataPoint( long time, long value )
    {
        super();
        this.time = time;
        this.value = value;
    }

    
    public long getTime()
    {
        return time;
    }

    
    public long getValue()
    {
        return value;
    }
    
    
}
