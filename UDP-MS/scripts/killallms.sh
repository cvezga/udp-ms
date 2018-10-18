kill $(ps xww | grep java | grep udpms | awk '{print $1}')
