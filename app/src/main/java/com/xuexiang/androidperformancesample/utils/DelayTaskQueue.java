/*
 * Copyright (C) 2023 xuexiangjys(xuexiangjys@163.com)
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

package com.xuexiang.androidperformancesample.utils;

import android.os.Looper;
import android.os.MessageQueue;

import java.util.LinkedList;
import java.util.Queue;

/**
 * 延迟任务执行队列
 *
 * @author xuexiang
 * @since 2023/4/13 02:23
 */
public class DelayTaskQueue {

    private final Queue<Runnable> mDelayTasks = new LinkedList<>();

    private final MessageQueue.IdleHandler mIdleHandler = () -> {
        if (mDelayTasks.size() > 0) {
            Runnable task = mDelayTasks.poll();
            if (task != null) {
                task.run();
            }
        }
        // mDelayTasks非空时返回ture表示下次继续执行，为空时返回false系统会移除该IdleHandler不再执行
        return !mDelayTasks.isEmpty();
    };

    public DelayTaskQueue addTask(Runnable task) {
        mDelayTasks.add(task);
        return this;
    }

    public void start() {
        Looper.myQueue().addIdleHandler(mIdleHandler);
    }
}