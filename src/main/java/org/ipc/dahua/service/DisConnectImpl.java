package org.ipc.dahua.service;

import com.sun.jna.Pointer;
import org.ipc.dahua.commcon.NetSDKLib;
import org.ipc.dahua.service.intf.IDisConnectCallBack;

/**
 * @Author 洋芋_Sir
 * @Date 2020/7/1
 * @description
 **/
public class DisConnectImpl implements NetSDKLib.fDisConnect {

    private IDisConnectCallBack disConnectCallBack;

    public DisConnectImpl(IDisConnectCallBack disConnectCallBack) {
        this.disConnectCallBack = disConnectCallBack;
    }

    @Override
    public void invoke(NetSDKLib.LLong lLoginID, String pchDVRIP, int nDVRPort, Pointer dwUser) {
        if (disConnectCallBack!=null){
            disConnectCallBack.disConnectInvoke(lLoginID.longValue(),pchDVRIP,nDVRPort);
        }
    }
}
