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

import java.util.List;

/**
 *
 * Android Startup: https://github.com/idisfkj/android-startup/blob/master/README-ch.md
 *
 * 1.callCreateOnMainThread：用来控制create()方法调时所在的线程，返回true代表在主线程执行。
 * 2.waitOnMainThread(): 用来控制当前初始化的组件是否需要在主线程进行等待其完成。如果返回true，将在主线程等待，并且阻塞主线程。
 *
 * @author xuexiang
 * @since 2022/12/7 00:59
 */
public class FirstAndroidStartup extends AndroidStartup<Context> {

    private static final String TAG = "FirstAndroidStartup";

    @Nullable
    @Override
    public Context create(@NonNull Context context) {
        Log.i(TAG, "First init start, Thread:" + Thread.currentThread().getName());
        try {
            Thread.sleep(5);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Log.i(TAG, "First init end.");
        return context;
    }

    @Override
    public boolean callCreateOnMainThread() {
        return true;
    }

    /**
     * 虽然waitOnMainThread()返回了false，但由于它是在主线程中执行，而主线程默认是阻塞的，所以callCreateOnMainThread()返回true时，该方法设置将失效。
     */
    @Override
    public boolean waitOnMainThread() {
        return false;
    }

    @Nullable
    @Override
    public List<Class<? extends Startup<?>>> dependencies() {
        Log.i(TAG, "dependencies:null");
        return null;
    }
}
