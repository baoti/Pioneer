package com.github.baoti.pioneer.event;

/**
 * 登录账号发生变更, 账号切换, 账号登录, 账号注销 都将产生该事件
 *
 * Created by liuyedong on 14-12-22.
 */
public class AccountChangedEvent {
    public final boolean hasAccount;
    public final String accountId;

    public AccountChangedEvent(boolean hasAccount, String accountId) {
        this.hasAccount = hasAccount;
        this.accountId = accountId;
    }
}
