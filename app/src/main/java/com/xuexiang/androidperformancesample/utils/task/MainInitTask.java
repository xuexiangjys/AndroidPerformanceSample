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

package com.xuexiang.androidperformancesample.utils.task;

import android.app.Application;

import androidx.annotation.NonNull;

import com.xuexiang.androidperformancesample.utils.performance.DoKitInit;
import com.xuexiang.androidperformancesample.utils.sdkinit.XBasicLibInit;
import com.xuexiang.xtask.api.step.SimpleTaskStep;
import com.xuexiang.xtask.core.ThreadType;

/**
 * 详情参见：https://github.com/xuexiangjys/XTask
 *
 * 主要初始化任务，放在第一位执行
 *
 * @author xuexiang
 * @since 2/17/22 12:43 AM
 */
public class MainInitTask extends SimpleTaskStep {

    private final Application mApplication;

    /**
     * 构造方法
     *
     * @param application 应用上下文
     */
    public MainInitTask(Application application) {
        mApplication = application;
    }

    @Override
    public void doTask() throws Exception {
        // 初始化基础库
        XBasicLibInit.init(mApplication);

        // DoKit
        DoKitInit.init(mApplication);
    }

    @Override
    public String getName() {
        return "MainInitTask";
    }

    @NonNull
    @Override
    public ThreadType getThreadType() {
        return ThreadType.SYNC;
    }
}
