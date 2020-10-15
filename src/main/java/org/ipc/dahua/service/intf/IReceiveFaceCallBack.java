package org.ipc.dahua.service.intf;


import org.ipc.dahua.dto.FaceInfo;

/**
 * @Author 洋芋_Sir
 * @Date 2020/7/1
 * @description
 **/
public interface IReceiveFaceCallBack {
    void receiveFaceInvoke(String clientId, long loginID, long attachHandle, FaceInfo faceInfo);
}
