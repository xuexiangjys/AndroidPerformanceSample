/*
 * Copyright (C) 2022 xuexiangjys(xuexiangjys@163.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package com.xuexiang.androidperformancesample.utils.androidstartup;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.startup.Initializer;

import java.util.Collections;
import java.util.List;

/**
 * App Startup: https://developer.android.google.cn/topic/libraries/app-startup
 *
 * 初始化的顺序类似Android的事件传递：先是自上而下调用dependencies寻找依赖，然后再自下而上调用create进行初始化。
 *
 * 依赖于SDKInitializer的初始化，待其初始化后再初始化
 *
 * @author xuexiang
 * @since 2022/12/6 00:28
 */
public class AppStartInitializer implements Initializer<Context> {

    private static final String TAG = "AppStartInitializer";

    @NonNull
    @Override
    public Context create(@NonNull Context context) {
        // 4.待SDKInitializer的初始化完毕后，开始初始化
        Log.i(TAG, "AppStart init start...");
        return context;
    }

    @NonNull
    @Override
    public List<Class<? extends Initializer<?>>> dependencies() {
        // 1.首先走到这，找寻初始化依赖
        Log.i(TAG, "dependencies:SDKInitializer.class");
        return Collections.singletonList(SDKInitializer.class);
    }
}
