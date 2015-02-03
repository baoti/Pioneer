/*
 * Copyright (c) 2014-2015 Sean Liu.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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
