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
import androidx.annotation.Nullable;

import com.rousetime.android_startup.AndroidStartup;
import com.rousetime.android_startup.Startup;

import java.util.Arrays;
import java.util.List;

/**
 * 在子线程执行，耗时100ms，不阻塞主线程。
 *
 * @author xuexiang
 * @since 2022/12/7 01:57
 */
public class FourthAndroidStartup extends AndroidStartup<Context> {

    private static final String TAG = "FourthAndroidStartup";

    @Nullable
    @Override
    public Context create(@NonNull Context context) {
        Log.i(TAG, "Fourth init start, Thread:" + Thread.currentThread().getName());
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Log.i(TAG, "Fourth init end.");
        return context;
    }

    @Override
    public boolean callCreateOnMainThread() {
        return false;
    }

    @Override
    public boolean waitOnMainThread() {
        return false;
    }

    @Nullable
    @Override
    public List<Class<? extends Startup<?>>> dependencies() {
        Log.i(TAG, "dependencies:FirstAndroidStartup.class, SecondAndroidStartup.class, ThirdAndroidStartup.class");
        return Arrays.asList(FirstAndroidStartup.class, SecondAndroidStartup.class, ThirdAndroidStartup.class);
    }
}
