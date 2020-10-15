package org.ipc.dahua.service.intf;

/**
 * @Author 洋芋_Sir
 * @Date 2020/7/1
 * @description
 **/
public interface IReConnectCallBack{
    void reConnectInvoke(long loginID, String ip, int port);
}
