package org.ipc.dahua.service;

import com.sun.jna.Pointer;
import org.ipc.dahua.commcon.NetSDKLib;
import org.ipc.dahua.service.intf.IVideoStatCallBack;

/**
 * 客流统计
 */
public class VideoStatSumImpl implements NetSDKLib.fVideoStatSumCallBack{

    private IVideoStatCallBack callBack;

    public IVideoStatCallBack getCallBack() {
        return callBack;
    }

    public void setCallBack(IVideoStatCallBack callBack) {
        this.callBack = callBack;
    }

    @Override
    public void invoke(NetSDKLib.LLong lAttachHandle, NetSDKLib.NET_VIDEOSTAT_SUMMARY pBuf, int dwBufLen, Pointer dwUser) {
        if (callBack!=null){
            System.out.println("通道号："+ pBuf.nChannelID);
            callBack.videoStatInvoke(pBuf.stuEnteredSubtotal.nToday,pBuf.stuExitedSubtotal.nToday);
        }
    }
}
