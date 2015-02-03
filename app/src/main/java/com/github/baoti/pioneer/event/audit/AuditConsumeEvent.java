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
