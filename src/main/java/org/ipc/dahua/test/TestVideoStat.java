package org.ipc.dahua.test;


import org.ipc.dahua.service.intf.IVideoStatCallBack;

public class TestVideoStat implements IVideoStatCallBack {
    @Override
    public void videoStatInvoke(int todayEnter, int todayExit) {
        System.out.println("今天进入多少人（todayEnter）:"+todayEnter);
        System.out.println("今天出来多少人（todayExit）:"+todayExit);
    }
}
