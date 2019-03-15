package com.datacloudsec.core.source;

import java.net.DatagramSocket;

/**
 * @Date 2019/1/14 9:11
 */
public abstract class AbstractDatagramSocketSource extends Thread {
    public DatagramSocket ds;

    protected void setDatagramSocket(DatagramSocket ds) {
        this.ds = ds;
    }

    @Override
    public void interrupt() {
        try {
            ds.close();
        } finally {
            super.interrupt();
        }
    }
}
