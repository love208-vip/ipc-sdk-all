package org.ipc.dahua.test;

import com.sun.jna.Pointer;
import com.sun.jna.ptr.IntByReference;
import org.ipc.dahua.commcon.ErrorCode;
import org.ipc.dahua.commcon.NetSDKLib;
import org.ipc.dahua.service.VideoStatSumImpl;
import org.ipc.dahua.service.intf.IVideoStatCallBack;

/**
 * @Author 洋芋_Sir
 * @Date 2020/6/30
 * @description SDK调用客户端, 调用顺序，init()->login()
 **/
public class FlowClient {
    static NetSDKLib netsdkApi = NetSDKLib.NETSDK_INSTANCE;
    private String address = "192.168.0.163";
    private int port = 37777;
    private String username = "admin";
    private String password = "admin123";
    private IVideoStatCallBack callBack;
    private VideoStatSumImpl videoStatSum;

    private NetSDKLib.NET_DEVICEINFO_Ex deviceInfo = new NetSDKLib.NET_DEVICEINFO_Ex();
    private static NetSDKLib.LLong loginHandle = new NetSDKLib.LLong(0);

    public FlowClient(IVideoStatCallBack callBack) {
        this("192.168.0.163", 37777, "admin", "admin123", callBack);
    }

    public FlowClient(String ip, int port, String name, String password, IVideoStatCallBack callBack) {
        this.address = ip;
        this.port = port;
        this.username = name;
        this.password = password;
        this.callBack = callBack;
        videoStatSum = new VideoStatSumImpl();
        videoStatSum.setCallBack(callBack);
    }

    private static class DisconnectCallback implements NetSDKLib.fDisConnect {
        private static DisconnectCallback instance = new DisconnectCallback();

        private DisconnectCallback() {
        }

        public static DisconnectCallback getInstance() {
            return instance;
        }

        public void invoke(NetSDKLib.LLong lLoginID, String pchDVRIP, int nDVRPort, Pointer dwUser) {
            System.out.printf("Device[%s:%d] Disconnect!\n", pchDVRIP, nDVRPort);
        }
    }

    private static class HaveReconnectCallback implements NetSDKLib.fHaveReConnect {
        private static HaveReconnectCallback instance = new HaveReconnectCallback();

        private HaveReconnectCallback() {
        }

        public static HaveReconnectCallback getInstance() {
            return instance;
        }

        public void invoke(NetSDKLib.LLong lLoginID, String pchDVRIP, int nDVRPort, Pointer dwUser) {
            System.out.printf("Device[%s:%d] HaveReconnected!\n", pchDVRIP, nDVRPort);
        }
    }

    /**
     * 结束
     */
    public void end() {
        System.out.println("End Test");
        if (loginHandle.longValue() != 0) {
            netsdkApi.CLIENT_Logout(loginHandle);
        }
        System.out.println("See You...");

        netsdkApi.CLIENT_Cleanup();
    }

    /**
     * 初始化
     */
    public void Init() {
        //初始化SDK库
        netsdkApi.CLIENT_Init(DisconnectCallback.getInstance(), null);

        //设置断线自动重练功能
        netsdkApi.CLIENT_SetAutoReconnect(HaveReconnectCallback.getInstance(), null);

        // 向设备登入
        final int nSpecCap = 0; /// login device by TCP
        final IntByReference error = new IntByReference();

        loginHandle = netsdkApi.CLIENT_LoginEx2(address, (short) port, username,
                password, nSpecCap, null, deviceInfo, error);

        if (loginHandle.longValue() == 0) {
            System.err.printf("Login Device [%s:%d] Failed ! Last Error[%x]\n", address, port, netsdkApi.CLIENT_GetLastError());
            return;
        }

        System.out.printf("Login Device [%s:%d] Success. \n", address, port);
    }

    /**
     * 获取错误代码
     * @return
     */
    public static String getErrorCodePrint() {
        return "\n{error code: (0x80000000|" + (netsdkApi.CLIENT_GetLastError() & 0x7fffffff) + ").参考  NetSDKLib.java }"
                + " - {error info:" + ErrorCode.getErrorCode(netsdkApi.CLIENT_GetLastError()) + "}\n";
    }

    /**
     * 订阅视频统计 句柄
     */
    private NetSDKLib.LLong videoStatHandle = new NetSDKLib.LLong(0);

    /**
     * 订阅
     */
    public void attachVideoStatSummary() {
        if (loginHandle.longValue() == 0) {
            return;
        }

        NetSDKLib.NET_IN_ATTACH_VIDEOSTAT_SUM videoStatIn = new NetSDKLib.NET_IN_ATTACH_VIDEOSTAT_SUM();
        videoStatIn.nChannel = 0;
        videoStatIn.cbVideoStatSum = videoStatSum;

        NetSDKLib.NET_OUT_ATTACH_VIDEOSTAT_SUM videoStatOut = new NetSDKLib.NET_OUT_ATTACH_VIDEOSTAT_SUM();

        videoStatHandle = netsdkApi.CLIENT_AttachVideoStatSummary(loginHandle, videoStatIn, videoStatOut, 5000);
        if (videoStatHandle.longValue() == 0) {
            System.err.printf("订阅失败，错误消息 = \nAttach Failed!LastError = %s\n", getErrorCodePrint());
            return;
        }

        System.out.printf("订阅成功，等待设备通知消息。\nAttach Success!Wait Device Notify Information\n");

        ////////////////////////////////////////////////////////////////////

        // 显示源信息数组初始化
       /* int nCameraCount = 10;
        NetSDKLib.NET_MATRIX_CAMERA_INFO[]  cameras = new NetSDKLib.NET_MATRIX_CAMERA_INFO[nCameraCount];
        for(int i = 0; i < nCameraCount; i++) {
            cameras[i] = new NetSDKLib.NET_MATRIX_CAMERA_INFO();
        }

        *//*
         *  入参
         *//*
        NetSDKLib.NET_IN_MATRIX_GET_CAMERAS stuIn = new NetSDKLib.NET_IN_MATRIX_GET_CAMERAS();

        *//*
         *  出参
         *//*
        NetSDKLib.NET_OUT_MATRIX_GET_CAMERAS stuOut = new NetSDKLib.NET_OUT_MATRIX_GET_CAMERAS();
        stuOut.nMaxCameraCount = nCameraCount;
        stuOut.pstuCameras = new Memory(cameras[0].size() * nCameraCount);
        stuOut.pstuCameras.clear(cameras[0].size() * nCameraCount);

        ToolKits.SetStructArrToPointerData(cameras, stuOut.pstuCameras);  // 将数组内存拷贝到Pointer

        if(netsdkApi.CLIENT_MatrixGetCameras(loginHandle, stuIn, stuOut, 5000)) {
            ToolKits.GetPointerDataToStructArr(stuOut.pstuCameras, cameras);  // 将 Pointer 的内容 输出到   数组

            for(int j = 0; j < stuOut.nRetCameraCount; j++) {
                if(cameras[j].bRemoteDevice == 0) {
                    System.out.println("[远程设备]");
                    System.out.println("NVR通道号：" + cameras[j].nUniqueChannel);
                    System.out.println("前端通道号(远程设备)：" + cameras[j].nChannelID);
                    System.out.println("设备ID : " + new String(cameras[j].szDevID).trim());
                    System.out.println("IP : " + new String(cameras[j].stuRemoteDevice.szIp).trim());
                    System.out.println("nPort : " + cameras[j].stuRemoteDevice.nPort);
                    System.out.println("szUser : " + new String(cameras[j].stuRemoteDevice.szUser).trim());
                    System.out.println("szPwd : " + new String(cameras[j].stuRemoteDevice.szPwd).trim());
                    System.out.println("通道个数 : " + cameras[j].stuRemoteDevice.nVideoInputChannels + "\n");
                } else {
                    System.out.println("[模拟设备]");
                    System.out.println("NVR通道号：" + cameras[j].nUniqueChannel);
                    System.out.println("前端通道号(远程设备)：" + cameras[j].nChannelID);
                    System.out.println("设备ID : " + new String(cameras[j].szDevID).trim() + "\n");
                }
            }
        } else {
            System.err.println("获取所有有效显示源失败！" + ToolKits.getErrorCodePrint());
        }*/

    }

    /**
     * 退订
     */
    public void detachVideoStatSummary() {
        if (videoStatHandle.longValue() != 0) {
            netsdkApi.CLIENT_DetachVideoStatSummary(videoStatHandle);
            videoStatHandle.setValue(0);
        }
    }
}
