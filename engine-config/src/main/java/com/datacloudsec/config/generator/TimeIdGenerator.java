package com.datacloudsec.config.generator;

public class TimeIdGenerator extends IdentityIdGenerator {

    private static long hour = 3600000;

    private static int moveValue = 30;

    private static int maxValue = Integer.MAX_VALUE;

    public synchronized Long generate(long time) {
        long value = generate() % maxValue;
        return (time / hour) << moveValue | value;
    }

    /**
     * 损失小时精度
     *
     * @param id
     * @return
     */
    public synchronized Long revert(long id) {
        return (id >> moveValue) * hour;
    }
}
