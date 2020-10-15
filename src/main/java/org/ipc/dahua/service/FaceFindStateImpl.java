package org.ipc.dahua.service;

import com.sun.jna.Pointer;
import org.ipc.dahua.commcon.Module;
import org.ipc.dahua.commcon.NetSDKLib;
import org.ipc.dahua.commcon.NetSDKLib.*;
import org.ipc.dahua.commcon.ToolKits;
import org.ipc.dahua.dto.FaceInfo;
import org.ipc.dahua.service.intf.*;


/**
 * @Author 洋芋_Sir
 * @Date 2020/7/1
 * @description
 **/
public class FaceFindStateImpl implements NetSDKLib.fFaceFindState {

    private Module module;
    private ISearchStateCallBack searchStateCallBack;
    private IReceiveFaceCallBack receiveFaceCallBack;

    public FaceFindStateImpl(Module module,ISearchStateCallBack searchStateCallBack,IReceiveFaceCallBack receiveFaceCallBack) {
        this.module=module;
        this.searchStateCallBack = searchStateCallBack;
        this.receiveFaceCallBack = receiveFaceCallBack;
    }

    @Override
    public void invoke(NetSDKLib.LLong lLoginID, NetSDKLib.LLong lAttachHandle, Pointer pstStates, int nStateNum, Pointer dwUser) {
        long invokeTime= System.currentTimeMillis();
        String invokeStr= String.valueOf(invokeTime);
        //System.out.println("回调开始时间:"+invokeStr);
        if (searchStateCallBack!=null){
            int nProgress=0;
            int nCount=0;
            if(nStateNum < 1) {
                return;
            }
            NET_CB_FACE_FIND_STATE[] msg = new NET_CB_FACE_FIND_STATE[nStateNum];
            for(int i = 0; i < nStateNum; i++) {
                msg[i] = new NET_CB_FACE_FIND_STATE();
            }
            ToolKits.GetPointerDataToStructArr(pstStates, msg);

            for(int i = 0; i < nStateNum; i++) {
                if(module.getnToken() == msg[i].nToken) {
                    nProgress = msg[i].nProgress;
                    nCount = msg[i].nCurrentCount; // 返回的总个数
                    searchStateCallBack.reStateChangeInvoke(module.getClientId(),lLoginID.longValue(),lAttachHandle.longValue(),nStateNum,nProgress,nCount);
                    if(nProgress == 100) {
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                Long beginTime= System.currentTimeMillis();
                                Long endTime;
                                while(true){
                                    endTime= System.currentTimeMillis();
                                    if ((endTime-beginTime)>5000){
                                        break;
                                    }

                                    NetSDKLib.CANDIDATE_INFOEX[] caInfoexs = module.doFindNextPerson(0, 1);
                                    if (caInfoexs == null) {
                                        //receiveFaceCallBack.receiveFaceInvoke(lLoginID.longValue(), lAttachHandle.longValue(), null);
                                        //Thread.sleep(100);
                                        continue;
                                    }

                                        String faceId=new String(caInfoexs[0].stPersonInfo.szUID).trim();
                                        module.setFaceId(faceId);
                                        FaceInfo faceInfo = new FaceInfo(new String(caInfoexs[0].stPersonInfo.szGroupID).trim(),
                                                faceId, String.valueOf(caInfoexs[0].bySimilarity));
//                                    long invokeTime3=System.currentTimeMillis();
//                                    String invokeStr3=String.valueOf(invokeTime3);
//                                    System.out.println("回调结束时间1:"+invokeStr3);
                                        receiveFaceCallBack.receiveFaceInvoke(module.getClientId(),lLoginID.longValue(), lAttachHandle.longValue(), faceInfo);
//                                        long invokeTime2=System.currentTimeMillis();
//                                        String invokeStr2=String.valueOf(invokeTime2);
//                                        System.out.println("回调结束时间2:"+invokeStr2);
                                        break;
                                }
                            }
                        }
                        ).start();
                    }
                }
            }

        }
    }
}
