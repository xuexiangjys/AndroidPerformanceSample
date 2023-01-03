
借鉴于：[干货：ANR日志分析全面解析](https://zhuanlan.zhihu.com/p/378902923)


# ANR日志分析全面解析

## ANR产生机制

* 1.输入事件超时(5s): InputEvent Timeout
* 2.广播类型超时（前台15s，后台60s）: BroadcastReceiver Timeout
* 3.服务超时（前台20s，后台200s）: Service Timeout
* 4.ContentProvider类型

## 导致ANR的原因

> 很多开发者认为，那就是耗时操作导致ANR，全部是app应用层的问题。实际上，线上环境大部分ANR由**系统原因**导致。

### 应用层导致ANR（耗时操作）

* 函数阻塞：如死循环、主线程IO、处理大数据.
* 锁出错：主线程等待子线程的锁
* 内存紧张：系统分配给一个应用的内存是有上限的，长期处于内存紧张，会导致频繁内存交换，进而导致应用的一些操作超时.

### 系统导致ANR

* CPU被抢占：一般来说，前台在玩游戏，可能会导致你的后台广播被抢占CPU
* 系统服务无法及时响应：比如获取系统联系人等，系统的服务都是Binder机制，服务能力也是有限的，有可能系统服务长时间不响应导致ANR
* 其他应用占用的大量内存

## 分析日志

> 发生ANR的时候，系统会产生一份anr日志文件（手机的/data/anr 目录下)。

### CPU负载

日志全文搜索：cpu。`CPU usage`作为开头，分别显示各个进程占用的CPU的详细情况和占用情况。

1.关键词解释:

* user:用户态,kernel:内核态
* faults:内存缺页，minor——轻微的，major——重度，需要从磁盘拿数据
* iowait:IO使用（等待）占比
* irq:硬中断，softirq:软中断

2.CPU总结：

* `iowait`占比很高，意味着有很大可能，是io耗时导致ANR，具体进一步查看有没有进程faults major比较多。
* 单进程CPU的负载并不是以100%为上限，而是有几个核，就有百分之几百，如4核上限为400%。

### 内存信息

```
Total number of allocations 476778　　进程创建到现在一共创建了多少对象
Total bytes allocated 52MB　进程创建到现在一共申请了多少内存
Total bytes freed 52MB　　　进程创建到现在一共释放了多少内存
Free memory 777KB　　　 不扩展堆的情况下可用的内存
Free memory until GC 777KB　　GC前的可用内存
Free memory until OOME 383MB　　OOM之前的可用内存
Total memory 当前总内存（已用+可用）
Max memory 384MB  进程最多能申请的内存
```

从含义可以得出结论：Free memory until OOME 的值很小的时候，已经处于内存紧张状态。应用可能是占用了过多内存。
如果ANR时间点前后，日志里有打印onTrimMemory，也可以作为内存紧张的一个参考判断。

### 堆栈消息

> 堆栈信息是最重要的一个信息，展示了ANR发生的进程当前所有线程的状态。

线程状态有5种：新建、就绪、执行、阻塞、死亡。而Java中的线程状态有6种:

[](https://pic3.zhimg.com/v2-38a2f11b6e53f7fe543e59700853879a_r.jpg)

[](https://pic2.zhimg.com/80/v2-58305ddc02bb70b177a775186fd21261_1440w.webp)

* main线程处于 BLOCK、WAITING、TIMEWAITING状态，那基本上是函数阻塞导致ANR；

* 如果main线程无异常，则应该排查CPU负载和内存环境。


## 典型案例分析

> 先找主线程main，确认状态，再进行案例分析。

### 1.主线程无卡顿，处于正常状态堆栈

```
"main" prio=5 tid=1 Native
  | group="main" sCount=1 dsCount=0 flags=1 obj=0x74b38080 self=0x7ad9014c00
  | sysTid=23081 nice=0 cgrp=default sched=0/0 handle=0x7b5fdc5548
  | state=S schedstat=( 284838633 166738594 505 ) utm=21 stm=7 core=1 HZ=100
  | stack=0x7fc95da000-0x7fc95dc000 stackSize=8MB
  | held mutexes=
  kernel: __switch_to+0xb0/0xbc
  kernel: SyS_epoll_wait+0x288/0x364
  kernel: SyS_epoll_pwait+0xb0/0x124
  kernel: cpu_switch_to+0x38c/0x2258
  native: #00 pc 000000000007cd8c  /system/lib64/libc.so (__epoll_pwait+8)
  native: #01 pc 0000000000014d48  /system/lib64/libutils.so (android::Looper::pollInner(int)+148)
  native: #02 pc 0000000000014c18  /system/lib64/libutils.so (android::Looper::pollOnce(int, int*, int*, void**)+60)
  native: #03 pc 00000000001275f4  /system/lib64/libandroid_runtime.so (android::android_os_MessageQueue_nativePollOnce(_JNIEnv*, _jobject*, long, int)+44)
  at android.os.MessageQueue.nativePollOnce(Native method)
  at android.os.MessageQueue.next(MessageQueue.java:330)
  at android.os.Looper.loop(Looper.java:169)
  at android.app.ActivityThread.main(ActivityThread.java:7073)
  at java.lang.reflect.Method.invoke(Native method)
  at com.android.internal.os.RuntimeInit$MethodAndArgsCaller.run(RuntimeInit.java:536)
  at com.android.internal.os.ZygoteInit.main(ZygoteInit.java:876)
```

上述主线程堆栈就是一个很正常的空闲堆栈，表明主线程正在等待新的消息。

遇到这种空闲堆栈，可以按照第3节的方法去分析CPU、内存的情况。其次可以关注抓取日志的时间和ANR发生的时间是否相隔过久，时间过久这个堆栈就没有分析意义了。


### 2.主线程执行耗时操作

```
"main" prio=5 tid=1 Runnable
  | group="main" sCount=0 dsCount=0 flags=0 obj=0x72deb848 self=0x7748c10800
  | sysTid=8968 nice=-10 cgrp=default sched=0/0 handle=0x77cfa75ed0
  | state=R schedstat=( 24783612979 48520902 756 ) utm=2473 stm=5 core=5 HZ=100
  | stack=0x7fce68b000-0x7fce68d000 stackSize=8192KB
  | held mutexes= "mutator lock"(shared held)
  at com.example.test.MainActivity$onCreate$2.onClick(MainActivity.kt:20)——关键行！！！
  at android.view.View.performClick(View.java:7187)
  at android.view.View.performClickInternal(View.java:7164)
  at android.view.View.access$3500(View.java:813)
  at android.view.View$PerformClick.run(View.java:27640)
  at android.os.Handler.handleCallback(Handler.java:883)
  at android.os.Handler.dispatchMessage(Handler.java:100)
  at android.os.Looper.loop(Looper.java:230)
  at android.app.ActivityThread.main(ActivityThread.java:7725)
  at java.lang.reflect.Method.invoke(Native method)
  at com.android.internal.os.RuntimeInit$MethodAndArgsCaller.run(RuntimeInit.java:526)
  at com.android.internal.os.ZygoteInit.main(ZygoteInit.java:1034)
```

上述日志表明，主线程正处于执行状态，看堆栈信息可知不是处于空闲状态，发生ANR是因为一处click监听函数里执行了耗时操作。


### 3.主线程被锁阻塞

```
"main" prio=5 tid=1 Blocked
  | group="main" sCount=1 dsCount=0 flags=1 obj=0x72deb848 self=0x7748c10800
  | sysTid=22838 nice=-10 cgrp=default sched=0/0 handle=0x77cfa75ed0
  | state=S schedstat=( 390366023 28399376 279 ) utm=34 stm=5 core=1 HZ=100
  | stack=0x7fce68b000-0x7fce68d000 stackSize=8192KB
  | held mutexes=
  at com.example.test.MainActivity$onCreate$1.onClick(MainActivity.kt:15)
  - waiting to lock <0x01aed1da> (a java.lang.Object) held by thread 3 ——————关键行！！！
  at android.view.View.performClick(View.java:7187)
  at android.view.View.performClickInternal(View.java:7164)
  at android.view.View.access$3500(View.java:813)
  at android.view.View$PerformClick.run(View.java:27640)
  at android.os.Handler.handleCallback(Handler.java:883)
  at android.os.Handler.dispatchMessage(Handler.java:100)
  at android.os.Looper.loop(Looper.java:230)
  at android.app.ActivityThread.main(ActivityThread.java:7725)
  at java.lang.reflect.Method.invoke(Native method)
  at com.android.internal.os.RuntimeInit$MethodAndArgsCaller.run(RuntimeInit.java:526)
  at com.android.internal.os.ZygoteInit.main(ZygoteInit.java:1034)
   
  ........省略N行.....
   
  "WQW TEST" prio=5 tid=3 TimeWating
  | group="main" sCount=1 dsCount=0 flags=1 obj=0x12c44230 self=0x772f0ec000
  | sysTid=22938 nice=0 cgrp=default sched=0/0 handle=0x77391fbd50
  | state=S schedstat=( 274896 0 1 ) utm=0 stm=0 core=1 HZ=100
  | stack=0x77390f9000-0x77390fb000 stackSize=1039KB
  | held mutexes=
  at java.lang.Thread.sleep(Native method)
  - sleeping on <0x043831a6> (a java.lang.Object)
  at java.lang.Thread.sleep(Thread.java:440)
  - locked <0x043831a6> (a java.lang.Object)
  at java.lang.Thread.sleep(Thread.java:356)
  at com.example.test.MainActivity$onCreate$2$thread$1.run(MainActivity.kt:22)
  - locked <0x01aed1da> (a java.lang.Object)————————————————————关键行！！！
  at java.lang.Thread.run(Thread.java:919)
```

这是一个典型的主线程被锁阻塞的例子；

```
waiting to lock <0x01aed1da> (a java.lang.Object) held by thread 3
```

其中等待的锁是<0x01aed1da>，这个锁的持有者是线程 3。进一步搜索 “tid=3” 找到线程3， 发现它正在TimeWating。

那么ANR的原因找到了：线程3持有了一把锁，并且自身长时间不释放，主线程等待这把锁发生超时。在线上环境中，常见因锁而ANR的场景是SharePreference写入。

### 4.CPU被抢占

```
CPU usage from 0ms to 10625ms later (2020-03-09 14:38:31.633 to 2020-03-09 14:38:42.257):
  543% 2045/com.alibaba.android.rimet: 54% user + 89% kernel / faults: 4608 minor 1 major ————关键行！！！
  99% 674/android.hardware.camera.provider@2.4-service: 81% user + 18% kernel / faults: 403 minor
  24% 32589/com.wang.test: 22% user + 1.4% kernel / faults: 7432 minor 1 major
  ........省略N行.....
```

如上日志，第二行是钉钉的进程，占据CPU高达543%，抢占了大部分CPU资源，因而导致发生ANR。

### 5.内存紧张导致ANR

如果有一份日志，CPU和堆栈都很正常（不贴出来了），仍旧发生ANR，考虑是内存紧张。

接着去系统日志里搜索am_meminfo， 这个没有搜索到。再次搜索onTrimMemory，果然发现了很多条记录；

```
10-31 22:37:19.749 20733 20733 E Runtime : onTrimMemory level:80,pid:com.xxx.xxx:Launcher0
10-31 22:37:33.458 20733 20733 E Runtime : onTrimMemory level:80,pid:com.xxx.xxx:Launcher0
10-31 22:38:00.153 20733 20733 E Runtime : onTrimMemory level:80,pid:com.xxx.xxx:Launcher0
10-31 22:38:58.731 20733 20733 E Runtime : onTrimMemory level:80,pid:com.xxx.xxx:Launcher0
10-31 22:39:02.816 20733 20733 E Runtime : onTrimMemory level:80,pid:com.xxx.xxx:Launcher0
```

可以看出，在发生ANR的时间点前后，内存都处于紧张状态，level等级是80，80这个等级是很严重的，应用马上就要被杀死。

一般来说，发生内存紧张，会导致多个应用发生ANR，所以在日志中如果发现有多个应用一起ANR了，可以初步判定，此ANR与你的应用无关。

### 6.系统服务超时导致ANR

系统服务超时一般会包含`BinderProxy.transactNative`关键字，请看如下日志：

```
"main" prio=5 tid=1 Native
  | group="main" sCount=1 dsCount=0 flags=1 obj=0x727851e8 self=0x78d7060e00
  | sysTid=4894 nice=0 cgrp=default sched=0/0 handle=0x795cc1e9a8
  | state=S schedstat=( 8292806752 1621087524 7167 ) utm=707 stm=122 core=5 HZ=100
  | stack=0x7febb64000-0x7febb66000 stackSize=8MB
  | held mutexes=
  kernel: __switch_to+0x90/0xc4
  kernel: binder_thread_read+0xbd8/0x144c
  kernel: binder_ioctl_write_read.constprop.58+0x20c/0x348
  kernel: binder_ioctl+0x5d4/0x88c
  kernel: do_vfs_ioctl+0xb8/0xb1c
  kernel: SyS_ioctl+0x84/0x98
  kernel: cpu_switch_to+0x34c/0x22c0
  native: #00 pc 000000000007a2ac  /system/lib64/libc.so (__ioctl+4)
  native: #01 pc 00000000000276ec  /system/lib64/libc.so (ioctl+132)
  native: #02 pc 00000000000557d4  /system/lib64/libbinder.so (android::IPCThreadState::talkWithDriver(bool)+252)
  native: #03 pc 0000000000056494  /system/lib64/libbinder.so (android::IPCThreadState::waitForResponse(android::Parcel*, int*)+60)
  native: #04 pc 00000000000562d0  /system/lib64/libbinder.so (android::IPCThreadState::transact(int, unsigned int, android::Parcel const&, android::Parcel*, unsigned int)+216)
  native: #05 pc 000000000004ce1c  /system/lib64/libbinder.so (android::BpBinder::transact(unsigned int, android::Parcel const&, android::Parcel*, unsigned int)+72)
  native: #06 pc 00000000001281c8  /system/lib64/libandroid_runtime.so (???)
  native: #07 pc 0000000000947ed4  /system/framework/arm64/boot-framework.oat (Java_android_os_BinderProxy_transactNative__ILandroid_os_Parcel_2Landroid_os_Parcel_2I+196)
  at android.os.BinderProxy.transactNative(Native method) ————————————————关键行！！！
  at android.os.BinderProxy.transact(Binder.java:804)
  at android.net.IConnectivityManager$Stub$Proxy.getActiveNetworkInfo(IConnectivityManager.java:1204)—关键行！
  at android.net.ConnectivityManager.getActiveNetworkInfo(ConnectivityManager.java:800)
  at com.xiaomi.NetworkUtils.getNetworkInfo(NetworkUtils.java:2)
  at com.xiaomi.frameworkbase.utils.NetworkUtils.getNetWorkType(NetworkUtils.java:1)
  at com.xiaomi.frameworkbase.utils.NetworkUtils.isWifiConnected(NetworkUtils.java:1)
```

从堆栈可以看出获取网络信息发生了ANR：getActiveNetworkInfo。

前文有讲过：系统的服务都是Binder机制（16个线程），服务能力也是有限的，有可能系统服务长时间不响应导致ANR。如果其他应用占用了所有Binder线程，那么当前应用只能等待。

可进一步搜索：blockUntilThreadAvailable关键字：

```
at android.os.Binder.blockUntilThreadAvailable(Native method)
```

如果有发现某个线程的堆栈，包含此字样，可进一步看其堆栈，确定是调用了什么系统服务。此类ANR也是属于系统环境的问题，如果某类型机器上频繁发生此问题，应用层可以考虑规避策略。

