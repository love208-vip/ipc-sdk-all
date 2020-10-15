package org.ipc.dahua.service.intf;


import org.ipc.dahua.dto.FaceInfo;

/**
 * @Author 洋芋_Sir
 * @Date 2020/7/6
 * @description
 **/
public interface ICatchFaceCallBack {
    void catchFaceInvoke(String loginIP, int channel, FaceInfo faceInfo);
}
