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
import com.didichuxing.doraemonkit.DoKitCallBack;
import com.didichuxing.doraemonkit.kit.network.bean.NetworkRecord;
import com.xuexiang.xutil.common.logger.Logger;

/**
 * 详情参见：https://xingyun.xiaojukeji.com/docs/dokit#/intro
 *
 * @author xuexiang
 * @since 2022/12/4 23:11
 */
public final class DoKitInit {

    private static final String TAG = "DoKitInit";

    private DoKitInit() {
        throw new UnsupportedOperationException("u can't instantiate me...");
    }

    /**
     * 初始化
     */
    public static void init(@NonNull Application application) {
        new DoKit.Builder(application)
                .callBack(DOKIT_CALLBACK)
//                .productId("需要使用平台功能的话，需要到dokit.cn平台申请id")
                .build();
    }


    private static DoKitCallBack DOKIT_CALLBACK = new DoKitCallBack() {
        @Override
        public void onCpuCallBack(float value, @NonNull String filePath) {
            Logger.iTag(TAG, "onCpuCallBack, value:" + value + ", filePath:" + filePath);
        }

        @Override
        public void onFpsCallBack(float value, @NonNull String filePath) {
            Logger.iTag(TAG, "onFpsCallBack, value:" + value + ", filePath:" + filePath);
        }

        @Override
        public void onMemoryCallBack(float value, @NonNull String filePath) {
            Logger.iTag(TAG, "onMemoryCallBack, value:" + value + ", filePath:" + filePath);
        }

        @Override
        public void onNetworkCallBack(@NonNull NetworkRecord networkRecord) {
            Logger.iTag(TAG, "onNetworkCallBack, networkRecord:" + networkRecord.mRequest);
        }
    };

}
