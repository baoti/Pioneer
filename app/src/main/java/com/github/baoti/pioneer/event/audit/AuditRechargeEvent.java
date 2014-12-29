package com.github.baoti.pioneer.event.audit;

/**
 * 充值
 *
 * Created by liuyedong on 14-12-19.
 */
public class AuditRechargeEvent {
    /**
     * 账号
     */
    public final String accountId;
    /**
     * 充值通道
     */
    public final String channel;
    /**
     * 货币种类
     */
    public final String currency;
    /**
     * 充值数量
     */
    public final double amount;

    public AuditRechargeEvent(String accountId, String channel, String currency, double amount) {
        this.accountId = accountId;
        this.channel = channel;
        this.currency = currency;
        this.amount = amount;
    }
}
