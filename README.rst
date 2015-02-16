Pioneer
=======

It is a android project which using "Best Practices" with "Dependency Inject", "View Inject" and
"Event Bus".

使用的库有:

  - com.jakewharton:butterknife
    View 注入
  - com.squareup.dagger:dagger
    依赖 注入
  - com.squareup:otto
    使用 Event Bus
  - com.squareup.picasso:picasso
    图片的加载与缓存
  - com.squareup.retrofit:retrofit
    封装 REST API

单元测试库使用了 Robolectric_

补充：
该项目的 MVP_ 以及代码结构，灵感来自 Android-CleanArchitecture_ ，同时也做了大量改动。
Presenter的设计，借鉴了 square's Flow_ 与 Mortar_ 。

.. _Robolectric:
   https://github.com/robolectric/robolectric

.. _MVP:
   https://github.com/baoti/Pioneer/tree/master/libPresenter

.. _Android-CleanArchitecture:
   https://github.com/android10/Android-CleanArchitecture

.. _Flow:
   https://github.com/square/flow

.. _Mortar:
   https://github.com/square/mortar

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
