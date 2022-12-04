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

package com.xuexiang.androidperformancesample.utils.performance.anr;

import com.github.anrwatchdog.ANRWatchDog;
import com.xuexiang.androidperformancesample.BuildConfig;
import com.xuexiang.androidperformancesample.MyApp;
import com.xuexiang.androidperformancesample.utils.Utils;
import com.xuexiang.xutil.common.logger.Logger;

/**
 * 详情参见：https://github.com/SalomonBrys/ANR-WatchDog
 *
 * ANR看门狗监听器初始化
 *
 * @author xuexiang
 * @since 2020-02-18 15:08
 */
public final class ANRWatchDogInit {

    private static final String TAG = "ANRWatchDog";

    /**
     * ANR监听间隔【可自定义修改】
     */
    private static final int LISTENER_INTERVAL = 1500;

    /**
     * ANR监听触发的时间
     */
    private static final int ANR_DURATION = 2 * LISTENER_INTERVAL;

    /**
     * ANR静默处理【就是不处理，直接记录一下日志】
     */
    private final static ANRWatchDog.ANRListener SILENT_LISTENER = error -> {
        Utils.error("发生了ANR！");
        Logger.eTag(TAG, error);
    };

    /**
     * ANR自定义处理【可以是记录日志用于上传】
     */
    private final static ANRWatchDog.ANRListener CUSTOM_LISTENER = error -> {
        Logger.eTag(TAG, "Detected Application Not Responding!", error);
        //这里进行ANR的捕获后的操作
        throw error;
    };

    /**
     * 初始化并启动看门狗
     */
    public static void init() {
        init(LISTENER_INTERVAL);
    }

    /**
     * 初始化并启动看门狗
     *
     * @param timeoutInterval 监听间隔
     */
    public static void init(int timeoutInterval) {
        if (!MyApp.isDebug()) {
            return;
        }
        sANRWatchDog = new ANRWatchDog(timeoutInterval);
        sANRWatchDog.setANRInterceptor(duration -> {
            long ret = ANR_DURATION - duration;
            if (ret > 0) {
                Logger.wTag(TAG, "Intercepted ANR that is too short (" + duration + " ms), postponing for " + ret + " ms.");
            }
            //当返回是0或者负数时，就会触发ANR监听回调
            return ret;
        }).setANRListener(SILENT_LISTENER).start();
        Logger.d("ANR看门狗监听器启动...");
    }

    /**
     * ANR看门狗
     */
    private static ANRWatchDog sANRWatchDog;

    private ANRWatchDogInit() {
        throw new UnsupportedOperationException("u can't instantiate me...");
    }

    public static ANRWatchDog getANRWatchDog() {
        return sANRWatchDog;
    }
}
