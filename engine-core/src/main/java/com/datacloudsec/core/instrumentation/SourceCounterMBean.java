package com.datacloudsec.core.instrumentation;

public interface SourceCounterMBean {

    long getEventReceivedCount();

    long getEventAcceptedCount();

    long getAppendReceivedCount();

    long getAppendAcceptedCount();

    long getAppendBatchReceivedCount();

    long getAppendBatchAcceptedCount();

    long getStartTime();

    long getStopTime();

    String getType();

    long getOpenConnectionCount();

    long getEventReadFail();

    long getChannelWriteFail();

    long getGenericProcessingFail();

}
