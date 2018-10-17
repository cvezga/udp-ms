package com.cvezga.sendorreader;

import java.io.InputStream;
import java.io.OutputStream;

public interface SensorReaderProtocol
{

    void handle( InputStream is, OutputStream os );

}
