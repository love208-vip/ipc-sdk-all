package org.ipc.dahua.test;


import org.ipc.dahua.commcon.Module;
import org.ipc.dahua.dto.*;
import org.ipc.dahua.service.*;
import org.ipc.dahua.service.intf.*;

/**
 * @Author 洋芋_Sir
 * @Date 2020/6/30
 * @description  非线程安全，每次使用时创建新实例
 **/
public class Client {
    private Module module;
    private CallBack callBack;
    private Config config;

    public String getClientId() {
        return module.getClientId();
    }

    public void setClientId(String clientId) {
         module.setClientId(clientId);
    }


    public Client(IDisConnectCallBack disConnectCallBack, IReConnectCallBack reConnectCallBack,
                  ISearchStateCallBack searchStateCallBack,
                  IReceiveFaceCallBack receiveFaceCallBack,
                  ICatchFaceCallBack catchFaceCallBack) {
        this.module=new Module();
        this.callBack=new CallBack(this.module,disConnectCallBack,reConnectCallBack,searchStateCallBack,receiveFaceCallBack,catchFaceCallBack);
    }

    public ResultInfo logout(){
        return module.logout();
    }

    public ResultInfo login(Config config){
        this.config=config;
        return module.login(config);
    }

    public ResultInfo init(){
        return Module.init(callBack,true);
    }

    public void clean(){
         Module.cleanup();
    }

    public ResultInfo addFace(FaceInfo faceInfo){
        return module.addFace(faceInfo);
    }

    public ResultInfo editFace(FaceInfo faceInfo){
        return module.editFace(faceInfo);
    }

    public ResultInfo delFace(FaceInfo faceInfo){
        return module.deleteFace(faceInfo);
    }

    public ResultInfo search(byte[] image,int length,String clientId){
        setClientId(clientId);
        return module.searchByFace(image,length,config.getSimilar(),callBack,0);
    }

    public ResultInfo syncSearch(byte[] image,int length,String clientId,long timeOut){
        setClientId(clientId);
        return module.searchByFace(image,length,config.getSimilar(),callBack,timeOut);
    }

    public ResultInfo startAnalyzer(){
        return module.startRealRecognize(callBack,config.getChannels());
    }
    public ResultInfo stopAnalyzer(){
        return module.stopSearchFace();
    }

    public ResultInfo getDeviceInfo(){
        return module.getDeviceInfo();
    }
}
