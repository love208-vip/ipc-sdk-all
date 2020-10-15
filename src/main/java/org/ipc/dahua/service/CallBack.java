package org.ipc.dahua.service;

import com.sun.jna.Callback;
import org.ipc.dahua.commcon.Module;
import org.ipc.dahua.commcon.NetSDKLib;
import org.ipc.dahua.service.intf.*;

/**
 * @Author 洋芋_Sir
 * @Date 2020/7/1
 * @description 回调
 **/
public class CallBack {
    private NetSDKLib.fDisConnect disConnect;
    private NetSDKLib.fHaveReConnect haveReConnect;
    private NetSDKLib.fFaceFindState faceFindState;

    public NetSDKLib.fAnalyzerDataCallBack getAnalyzerDataCallBack() {
        return analyzerDataCallBack;
    }

    public void setAnalyzerDataCallBack(NetSDKLib.fAnalyzerDataCallBack analyzerDataCallBack) {
        this.analyzerDataCallBack = analyzerDataCallBack;
    }

    private NetSDKLib.fAnalyzerDataCallBack analyzerDataCallBack;
    private IDisConnectCallBack disConnectCallBack;
    private IReConnectCallBack reConnectCallBack;
    private ISearchStateCallBack searchStateCallBack;
    private IReceiveFaceCallBack receiveFaceCallBack;
    private ICatchFaceCallBack catchFaceCallBack;

    public Callback getDisConnect() {
        return disConnect;
    }

    public Callback getHaveReConnect() {
        return haveReConnect;
    }

    public NetSDKLib.fFaceFindState getFaceFindState() {
        return faceFindState;
    }

    public IDisConnectCallBack getDisConnectCallBack() {
        return disConnectCallBack;
    }

    public IReConnectCallBack getReConnectCallBack() {
        return reConnectCallBack;
    }

    public ISearchStateCallBack getSearchStateCallBack() {
        return searchStateCallBack;
    }

    public IReceiveFaceCallBack getReceiveFaceCallBack() {
        return receiveFaceCallBack;
    }

    public CallBack(Module data, IDisConnectCallBack disConnectCallBack, IReConnectCallBack reConnectCallBack,
                    ISearchStateCallBack searchStateCallBack,
                    IReceiveFaceCallBack receiveFaceCallBack,
                    ICatchFaceCallBack catchFaceCallBack) {
        this.disConnectCallBack = disConnectCallBack;
        this.reConnectCallBack = reConnectCallBack;
        this.searchStateCallBack = searchStateCallBack;
        this.receiveFaceCallBack = receiveFaceCallBack;
        this.catchFaceCallBack = catchFaceCallBack;
        disConnect = new DisConnectImpl(disConnectCallBack);
        haveReConnect = new HaveReConnectImpl(reConnectCallBack);
        faceFindState = new FaceFindStateImpl(data, searchStateCallBack, receiveFaceCallBack);
        analyzerDataCallBack = new AnalyzerDataImpl(data, catchFaceCallBack);
    }
}
