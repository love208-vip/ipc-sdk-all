package org.ipc.dahua.service.intf;

/**
 * @Author 洋芋_Sir
 * @Date 2020/7/1
 * @description
 **/
public interface ISearchStateCallBack {
    void reStateChangeInvoke(String clientId, long loginID, long attacheHandle, int stateNum, int progress, int count);
}
