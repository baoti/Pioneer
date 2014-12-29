package com.github.baoti.pioneer.entity;

/**
 * Created by liuyedong on 2014/12/26.
 */
public class Account {
    private String accountId;
    private String name;
    private int following;
    private int fans;
    private ImageBean avatar;

    @Override
    public String toString() {
        return "accountId=" + accountId;
    }

    public String getAccountId() {
        return accountId;
    }

    public String getName() {
        return name;
    }

    public int getFollowing() {
        return following;
    }

    public int getFans() {
        return fans;
    }

    public ImageBean getAvatar() {
        return avatar;
    }

    public static final Account ANONYMOUS = new Account();
    static {
        ANONYMOUS.accountId = "Anonymous";
        ANONYMOUS.name = "Anonymous";
    }
}
