package com.github.baoti.pioneer.event.audit;

/**
 * 支出
 *
 * Created by liuyedong on 14-12-19.
 */
public class AuditConsumeEvent {
    /**
     * 账号
     */
    public final String accountId;
    /**
     * 消费信息
     */
    public final String desc;
    /**
     * 货币种类
     */
    public final String currency;
    /**
     * 货币数量
     */
    public final double amount;

    public AuditConsumeEvent(String accountId, String desc, String currency, double amount) {
        this.accountId = accountId;
        this.desc = desc;
        this.currency = currency;
        this.amount = amount;
    }
}
