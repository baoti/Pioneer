package com.github.baoti.pioneer.entity;

/**
 * Created by liuyedong on 2014/12/26.
 */
public class Account {
    private String accountId;
    private String name;
    private int following;
    private int followers;
    private ImageBean avatar;

    public Account(String accountId, String name, int following, int followers, ImageBean avatar) {
        this.accountId = accountId;
        this.name = name;
        this.following = following;
        this.followers = followers;
        this.avatar = avatar;
    }

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

    public int getFollowers() {
        return followers;
    }

    public ImageBean getAvatar() {
        return avatar;
    }

    public static final Account ANONYMOUS = new Account("Anonymous", "Anonymous", 0, 0, null);
}
