package com.datacloudsec.core.tools;

public class SystemClock implements Clock {

    public long currentTimeMillis() {
        return System.currentTimeMillis();
    }

}

