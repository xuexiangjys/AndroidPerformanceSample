/*
 * Copyright (C) 2019 xuexiangjys(xuexiangjys@163.com)
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

package com.xuexiang.androidperformancesample.fragment;

import com.xuexiang.androidperformancesample.core.BaseSimpleListFragment;
import com.xuexiang.xpage.annotation.Page;

import java.util.List;

/**
 * 超时无响应测试
 *
 * @author xuexiang
 * @since 2019-07-08 00:52
 */
@Page(name = "ANR测试")
public class ANRTestFragment extends BaseSimpleListFragment {


    @Override
    protected List<String> initSimpleData(List<String> lists) {
        lists.add("ANR 2000ms");
        lists.add("ANR 4000ms");
        return lists;
    }

    @Override
    protected void onItemClick(int position) {
        switch (position) {
            case 0:
                mockANR(2000);
                break;
            case 1:
                mockANR(4000);
                break;
            default:
                break;
        }
    }


    private void mockANR(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
