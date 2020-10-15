package org.ipc.dahua.dto;

/**
 * @Author 洋芋_Sir
 * @Date 2020/6/30
 * @description
 **/
public class ResultInfo {
    private boolean ret;
    private String msg;

    public ResultInfo() {
        this.ret = false;
        this.msg = "default message";
    }

    public ResultInfo(boolean ret, String msg) {
        this.ret = ret;
        this.msg = msg;
    }

    public boolean getRet() {
        return ret;
    }

    public void setRet(boolean ret) {
        this.ret = ret;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }


}
