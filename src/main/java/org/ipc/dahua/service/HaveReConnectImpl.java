package org.ipc.dahua.service;

import com.sun.jna.Pointer;
import org.ipc.dahua.commcon.NetSDKLib;
import org.ipc.dahua.service.intf.IReConnectCallBack;


/**
 * @Author 洋芋_Sir
 * @Date 2020/7/1
 * @description
 **/
public class HaveReConnectImpl implements NetSDKLib.fHaveReConnect {
    private IReConnectCallBack reConnectCallBack;

    public HaveReConnectImpl(IReConnectCallBack reConnectCallBack) {
        this.reConnectCallBack = reConnectCallBack;
    }

    @Override
    public void invoke(NetSDKLib.LLong lLoginID, String pchDVRIP, int nDVRPort, Pointer dwUser) {
        if (reConnectCallBack!=null){
            reConnectCallBack.reConnectInvoke(lLoginID.longValue(),pchDVRIP,nDVRPort);
        }
    }
}
