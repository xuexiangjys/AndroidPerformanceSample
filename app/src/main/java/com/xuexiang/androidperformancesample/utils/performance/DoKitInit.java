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

package com.xuexiang.androidperformancesample.utils.performance;

import android.app.Application;

import androidx.annotation.NonNull;

import com.didichuxing.doraemonkit.DoKit;
import com.xuexiang.androidperformancesample.MyApp;

import io.github.xanderwang.performance.PERF;

/**
 * 详情参见：https://xingyun.xiaojukeji.com/docs/dokit#/intro
 *
 * @author xuexiang
 * @since 2022/12/4 23:11
 */
public final class DoKitInit {

    private DoKitInit() {
        throw new UnsupportedOperationException("u can't instantiate me...");
    }

    /**
     * 初始化
     */
    public static void init(@NonNull Application application) {
        new DoKit.Builder(application)
//                .productId("需要使用平台功能的话，需要到dokit.cn平台申请id")
                .build();
    }

}
