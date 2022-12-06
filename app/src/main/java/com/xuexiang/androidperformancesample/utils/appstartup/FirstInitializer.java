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

package com.xuexiang.androidperformancesample.utils.appstartup;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.startup.Initializer;

import java.util.Collections;
import java.util.List;

/**
 * FirstInitializer, 由于没有依赖，所以首先被加载初始化
 *
 * @author xuexiang
 * @since 2022/12/6 00:37
 */
public class FirstInitializer implements Initializer<Context> {

    private static final String TAG = "FirstInitializer";

    @NonNull
    @Override
    public Context create(@NonNull Context context) {
        // 3.由于没有需要依赖的初始化，因此直接进行初始化
        Log.i(TAG, "First init start...");
        return context;
    }

    @NonNull
    @Override
    public List<Class<? extends Initializer<?>>> dependencies() {
        // 2.其次走到这，找寻初始化依赖
        Log.i(TAG, "dependencies:emptyList");
        return Collections.emptyList();
    }
}
