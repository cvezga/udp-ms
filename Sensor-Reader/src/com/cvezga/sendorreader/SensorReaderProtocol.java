package com.cvezga.sendorreader;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

public interface SensorReaderProtocol
{

    

    void handle( List<DataPoint> dataPoints, InputStream is, OutputStream os );

}
