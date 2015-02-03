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

package com.github.baoti.pioneer.misc.util;

import org.junit.Assert;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.equalTo;

public class TextsTest {

    @Test
    public void testTrip() throws Exception {
        Assert.assertThat(Texts.trip(",,abc,,", ",").toString(), equalTo("abc"));
        Assert.assertThat(Texts.trip(",.abc,.", ",").toString(), equalTo(".abc,."));
        Assert.assertThat(Texts.trip(",.abc,.", ".").toString(), equalTo(",.abc,"));
        Assert.assertThat(Texts.trip(",.abc,.", ".,").toString(), equalTo("abc"));
        Assert.assertThat(Texts.trip("", ",").toString(), equalTo(""));
        Assert.assertThat(Texts.trip(",,,", ",").toString(), equalTo(""));
    }

    @Test
    public void testFilledStr() throws Exception {
        Assert.assertThat(Texts.filledStr(3, 'a'), equalTo("aaa"));
        Assert.assertThat(Texts.filledStr(0, 'a'), equalTo(""));
        Assert.assertThat(Texts.filledStr(1, ','), equalTo(","));
    }
}