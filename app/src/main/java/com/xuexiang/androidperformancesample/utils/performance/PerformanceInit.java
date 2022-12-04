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

import com.xuexiang.androidperformancesample.MyApp;

import io.github.xanderwang.performance.PERF;

/**
 * 详情参见：https://github.com/xanderwang/performance
 *
 * @author xuexiang
 * @since 2022/12/4 21:18
 */
public final class PerformanceInit {

    private static final String TAG = "ANRWatchDog";

    private PerformanceInit() {
        throw new UnsupportedOperationException("u can't instantiate me...");
    }

    /**
     * 初始化并启动监听
     */
    public static void init(@NonNull Application application) {
        if (!MyApp.isDebug()) {
            return;
        }
        // issue 文件保存目录
        PERF.init(new PERF.Builder()
                .checkUI(true, 500) // 检查 ui lock
                .checkFps(true, 1000) // 检查 fps
                // hook不稳定
//                .checkIPC(true) // 检查 ipc 调用
//                .checkThread(true) // 检查线程和线程池
//                .checkBitmap(true) // 检测 Bitmap 的创建
                .globalTag(TAG) // 全局 logcat tag ,方便过滤
                .cacheDirSupplier(application::getCacheDir).maxCacheSizeSupplier(() -> {
                    // issue 文件最大占用存储空间
                    return 10 * 1024 * 1024;
                }).uploaderSupplier(() -> logFile -> {
                    // issue 文件上传接口
                    return false;
                }).build());
    }

}
