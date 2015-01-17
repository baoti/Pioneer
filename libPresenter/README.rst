Presenter on Android
====================

This is a android MVP library.

这是一个帮助 Android 采用 MVP 模式实现表现层的库。其中主要包含 IView 与 Presenter，分别对应MVP中的V与P。

Presenter 拥有自己的生命周期，随着 View 状态的变化，Presenter 会激活 takeView/dropView/resume/pause 等生命周期方法；
只有在 takeView 与 dropView 之间，Presenter 才拥有 view，否则 hasView 返回 false, getView 返回 null。
因此 Presenter 可以被 Thread, AsyncTask 或 异步方法等等 强引用，而不用担心 View 与 Activity 在生命周期结束后被强引用导致的内存占用。

Presenter 的生命周期，由具体的 ActivityView/FragmentView 来驱动；
也可仿照ActivityView/FragmentView来实现其他的View，如 ListFragmentView。
也可直接继承ListView/LinearLayout或自定义控件来实现自己的View，这是 square.Flow 与 square.Mortar 的作法。

它也支持视图代理，可以通过代理来共用界面部分的代码。
具体可参考app模块中的 LoginDialogFragment/LoginFragment/LoginViewDelegate 。

补充：
Presenter的设计，借鉴了 square.Flow 与 square.Mortar 。

License
-------

    Copyright 2014, 2015 Sean Liu.

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
