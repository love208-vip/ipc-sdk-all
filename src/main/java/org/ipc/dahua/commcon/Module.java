package org.ipc.dahua.commcon;

import com.sun.jna.Memory;
import com.sun.jna.ptr.IntByReference;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

import org.ipc.dahua.dto.*;
import org.ipc.dahua.service.CallBack;


/**
 * @Author 洋芋_Sir
 * @Date 2020/6/30
 * @description
 **/
public class Module {
    public static NetSDKLib netsdk = NetSDKLib.NETSDK_INSTANCE;
    private NetSDKLib.LLong m_hLoginHandle = new NetSDKLib.LLong(0);
    private NetSDKLib.LLong m_FindHandle = new NetSDKLib.LLong(0);
    private NetSDKLib.LLong m_attachHandle = new NetSDKLib.LLong(0);
    private NetSDKLib.LLong analyzerHandle = new NetSDKLib.LLong(0);
    private AtomicInteger faceId = null;
    private static int deviceCount = 1;
    private static ConcurrentHashMap<Integer, String> channelMap = new ConcurrentHashMap<Integer, String>();
    private int progress = 0;
    private int nToken = 0;
    private boolean isLogin = false;
    private String loginIP;
    private static boolean isInit = false;
    private static boolean isOpenLog = false;
    private String clientId;

    public String getLoginIP() {
        return loginIP;
    }

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public String getFaceId() {
        if (faceId != null) {
            return String.valueOf(faceId.get());
        } else {
            return null;
        }
    }

    public void setFaceId(String faceId) {
        if (faceId != null) {
            this.faceId = new AtomicInteger();
            this.faceId.set(Integer.parseInt(faceId));
        }
    }

    public NetSDKLib.LLong getAnalyzerHandle() {
        return analyzerHandle;
    }

    public void setAnalyzerHandle(NetSDKLib.LLong analyzerHandle) {
        this.analyzerHandle = analyzerHandle;
    }

    public NetSDKLib.LLong getM_FindHandle() {
        return m_FindHandle;
    }

    public void setM_FindHandle(NetSDKLib.LLong m_FindHandle) {
        this.m_FindHandle = m_FindHandle;
    }

    public NetSDKLib.LLong getM_attachHandle() {
        return m_attachHandle;
    }

    public void setM_attachHandle(NetSDKLib.LLong m_attachHandle) {
        this.m_attachHandle = m_attachHandle;
    }

    public int getnToken() {
        return nToken;
    }

    public void setnToken(int nToken) {
        this.nToken = nToken;
    }

    public static Memory byteArrayToMemory(byte[] image, int length) {
        Memory memory = new Memory(length);
        memory.write(0, image, 0, length);
        return memory;
    }

    public static byte[] memoryToByteArray(Memory memory, int length) {
        byte[] image = new byte[length];
        memory.read(0, image, 0, length);
        return image;
    }

    public static ResultInfo init(CallBack callBack, boolean openLog) {
        ResultInfo resultInfo = new ResultInfo();
        isInit = netsdk.CLIENT_Init(callBack.getDisConnect(), null);
        if (!isInit) {
            resultInfo.setRet(false);
            resultInfo.setMsg("Initialize SDK failed");
            return resultInfo;
        }
        //打开日志，可选
        if (openLog) {
            NetSDKLib.LOG_SET_PRINT_INFO setLog = new NetSDKLib.LOG_SET_PRINT_INFO();
            File path = new File("./sdklog/");
            if (!path.exists()) {
                path.mkdir();
            }
            String logPath = path.getAbsoluteFile().getParent() + "\\sdklog\\" + ToolKits.getDate() + ".log";
            setLog.nPrintStrategy = 0;
            setLog.bSetFilePath = 1;
            System.arraycopy(logPath.getBytes(), 0, setLog.szLogFilePath, 0, logPath.getBytes().length);
            System.out.println(logPath);
            setLog.bSetPrintStrategy = 1;
            isOpenLog = netsdk.CLIENT_LogOpen(setLog);
            if (!isOpenLog) {
                resultInfo.setRet(false);
                resultInfo.setMsg(String.format("Failed to open NetSDK log.%s", ToolKits.getErrorCodePrint()));
                return resultInfo;
            }
        }

        // 设置断线重连回调接口，设置过断线重连成功回调函数后，当设备出现断线情况，SDK内部会自动进行重连操作
        // 此操作为可选操作，但建议用户进行设置
        netsdk.CLIENT_SetAutoReconnect(callBack.getHaveReConnect(), null);

        //设置登录超时时间和尝试次数，可选
        int waitTime = 5000; //登录请求响应超时时间设置为5S
        int tryTimes = 1;    //登录时尝试建立链接1次
        netsdk.CLIENT_SetConnectTime(waitTime, tryTimes);

        // // GDPR使能全局开关
        netsdk.CLIENT_SetGDPREnable(true);

        // 设置更多网络参数，NET_PARAM的nWaittime，nConnectTryNum成员与CLIENT_SetConnectTime
        // 接口设置的登录设备超时时间和尝试次数意义相同,可选
        NetSDKLib.NET_PARAM netParam = new NetSDKLib.NET_PARAM();
        netParam.nConnectTime = 10000;      // 登录时尝试建立链接的超时时间
        netParam.nGetConnInfoTime = 3000;   // 设置子连接的超时时间
        netsdk.CLIENT_SetNetworkParam(netParam);

        resultInfo.setRet(true);
        resultInfo.setMsg("Initialize SDK success");
        return resultInfo;
    }

    public static void cleanup() {
        if (isOpenLog) {
            netsdk.CLIENT_LogClose();
        }

        if (isInit) {
            netsdk.CLIENT_Cleanup();
        }
    }

    public ResultInfo login(Config config) {
        ResultInfo resultInfo = new ResultInfo();
        if (isLogin) {
            resultInfo.setRet(true);
            resultInfo.setMsg("is in");
            return resultInfo;
        }
        if (!isInit) {
            resultInfo.setRet(false);
            resultInfo.setMsg("Initialize is Failed,can not login");
            return resultInfo;
        }

        NetSDKLib.NET_IN_LOGIN_WITH_HIGHLEVEL_SECURITY pstInParam = new NetSDKLib.NET_IN_LOGIN_WITH_HIGHLEVEL_SECURITY();
        pstInParam.nPort = config.getPort();
        pstInParam.szIP = config.getIp().getBytes();
        pstInParam.szPassword = config.getPasswd().getBytes();
        pstInParam.szUserName = config.getName().getBytes();
        NetSDKLib.NET_OUT_LOGIN_WITH_HIGHLEVEL_SECURITY pstOutParam = new NetSDKLib.NET_OUT_LOGIN_WITH_HIGHLEVEL_SECURITY();
        //pstOutParam.stuDeviceInfo=m_stDeviceInfo;
        m_hLoginHandle = netsdk.CLIENT_LoginWithHighLevelSecurity(pstInParam, pstOutParam);
        if (m_hLoginHandle.longValue() == 0) {
            resultInfo.setRet(false);
            resultInfo.setMsg(String.format("Login Device[%s] Port[%d]Failed. %s", config.getIp(), config.getPort(), ToolKits.getErrorCodePrint()));
        } else {
            isLogin = true;
            loginIP = config.getIp();
            resultInfo.setRet(true);
            resultInfo.setMsg(String.format("Login Success [%s]", config.getIp()));
            deviceCount = pstOutParam.stuDeviceInfo.byChanNum;
        }
        return resultInfo;
    }

    public ResultInfo getDeviceInfo() {

//        // 显示源信息数组初始化
//        NetSDKLib.NET_MATRIX_CAMERA_INFO[] cameras = new NetSDKLib.NET_MATRIX_CAMERA_INFO[deviceCount];
//        for(int i = 0; i < deviceCount; i++) {
//            cameras[i] = new NetSDKLib.NET_MATRIX_CAMERA_INFO();
//        }
//
//        cameras[0].nUniqueChannel = 0;
//        cameras[0].nChannelID = 0; // 通道号
//
//        /*
//         * 入参
//         */
//        NetSDKLib.NET_IN_MATRIX_SET_CAMERAS stuIn = new NetSDKLib.NET_IN_MATRIX_SET_CAMERAS();
//        stuIn.nCameraCount = deviceCount;
//        stuIn.pstuCameras = new Memory(cameras[0].size() * deviceCount);
//        stuIn.pstuCameras.clear(cameras[0].size() * deviceCount);
//
//        ToolKits.SetStructArrToPointerData(cameras, stuIn.pstuCameras);  // 将数组内存拷贝到Pointer
//
//        /*
//         * 出参
//         */
//        NetSDKLib.NET_OUT_MATRIX_SET_CAMERAS stuOut = new NetSDKLib.NET_OUT_MATRIX_SET_CAMERAS();
//        if(netsdk.CLIENT_MatrixSetCameras(m_hLoginHandle, stuIn, stuOut, 5000)) {
//            System.out.println("设置显示源成功！");
//        } else {
//            System.err.println("设置显示源失败！" + ToolKits.getErrorCodePrint());
//        }

        //////////////////////////////////////

        ResultInfo resultInfo = new ResultInfo();
        if (!isLogin) {
            resultInfo.setRet(false);
            resultInfo.setMsg("no login,can not add face");
            return resultInfo;
        }
        if (!isInit) {
            resultInfo.setRet(false);
            resultInfo.setMsg("Initialize is Failed,can not login");
            return resultInfo;
        }
        NetSDKLib.NET_MATRIX_CAMERA_INFO[] cameras = new NetSDKLib.NET_MATRIX_CAMERA_INFO[deviceCount];
        for (int i = 0; i < deviceCount; i++) {
            cameras[i] = new NetSDKLib.NET_MATRIX_CAMERA_INFO();
        }

        /*
         *  入参
         */
        NetSDKLib.NET_IN_MATRIX_GET_CAMERAS stuIn = new NetSDKLib.NET_IN_MATRIX_GET_CAMERAS();

        /*
         *  出参
         */
        NetSDKLib.NET_OUT_MATRIX_GET_CAMERAS stuOut = new NetSDKLib.NET_OUT_MATRIX_GET_CAMERAS();
        stuOut.nMaxCameraCount = deviceCount;
        stuOut.pstuCameras = new Memory(cameras[0].size() * deviceCount);
        stuOut.pstuCameras.clear(cameras[0].size() * deviceCount);

        ToolKits.SetStructArrToPointerData(cameras, stuOut.pstuCameras);  // 将数组内存拷贝到Pointer

        if (netsdk.CLIENT_MatrixGetCameras(m_hLoginHandle, stuIn, stuOut, 5000)) {
            ToolKits.GetPointerDataToStructArr(stuOut.pstuCameras, cameras);  // 将 Pointer 的内容 输出到   数组
            channelMap.clear();
            for (int j = 0; j < stuOut.nRetCameraCount; j++) {
                resultInfo.setRet(true);
                resultInfo.setMsg("cameras:" + stuOut.nRetCameraCount);

                if (cameras[j].bRemoteDevice == 1) {
                    channelMap.put(cameras[j].nUniqueChannel, new String(cameras[j].stuRemoteDevice.szSerialNo).trim());
                }
            }
        } else {
            resultInfo.setRet(false);
            resultInfo.setMsg("获取所有有效显示源失败:" + ToolKits.getErrorCodePrint());
        }
        return resultInfo;
    }


    public static String channelToSerialNo(Integer channel) {
        return channelMap.get(channel);
    }

    public ResultInfo logout() {
        ResultInfo resultInfo = new ResultInfo();
        if (m_hLoginHandle.longValue() == 0) {
            resultInfo.setRet(false);
            resultInfo.setMsg(String.format("no handle"));
        }

        boolean ret = netsdk.CLIENT_Logout(m_hLoginHandle);
        if (ret) {
            isLogin = false;
            resultInfo.setRet(true);
            resultInfo.setMsg("now is quit");
            m_hLoginHandle.setValue(0);
        } else {
            resultInfo.setRet(false);
            resultInfo.setMsg(String.format("Logout Failed.%s", ToolKits.getErrorCodePrint()));
        }

        return resultInfo;
    }

    public ResultInfo addFace(FaceInfo faceInfo) {
        ResultInfo resultInfo = new ResultInfo();
        if (!isLogin) {
            resultInfo.setRet(false);
            resultInfo.setMsg("no login,can not add face");
            return resultInfo;
        }

        NetSDKLib.NET_IN_OPERATE_FACERECONGNITIONDB stuIn = new NetSDKLib.NET_IN_OPERATE_FACERECONGNITIONDB();
        stuIn.emOperateType = NetSDKLib.EM_OPERATE_FACERECONGNITIONDB_TYPE.NET_FACERECONGNITIONDB_ADD;

        ///////// 使用人员扩展信息 //////////
        stuIn.bUsePersonInfoEx = 1;

        // 人脸库ID
        System.arraycopy(faceInfo.getGroupId().getBytes(), 0, stuIn.stPersonInfoEx.szGroupID, 0, faceInfo.getGroupId().getBytes().length);

        // 生日设置
        if (faceInfo.isBirthday()) {
            stuIn.stPersonInfoEx.wYear = (short) faceInfo.getBirthday().getYear();
            stuIn.stPersonInfoEx.byMonth = (byte) faceInfo.getBirthday().getMonth();
            stuIn.stPersonInfoEx.byDay = (byte) faceInfo.getBirthday().getDay();
        }

        // 性别,1-男,2-女,作为查询条件时,此参数填0,则表示此参数无效
        stuIn.stPersonInfoEx.bySex = (byte) faceInfo.getSex();

        // 人员名字
        if (faceInfo.getName() != null) {
            try {
                System.arraycopy(faceInfo.getName().getBytes("GBK"), 0, stuIn.stPersonInfoEx.szPersonName, 0, faceInfo.getName().getBytes("GBK").length);
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }

        // 证件类型
        stuIn.stPersonInfoEx.byIDType = (byte) faceInfo.getIdType();

        // 证件号
        if (faceInfo.getIdNumber() != null) {
            System.arraycopy(faceInfo.getIdNumber().getBytes(), 0, stuIn.stPersonInfoEx.szID, 0, faceInfo.getIdNumber().getBytes().length);
        }

        // 图片张数、大小、缓存设置
        if (faceInfo.getImage() != null) {
            stuIn.stPersonInfoEx.wFacePicNum = 1; // 图片张数
            stuIn.stPersonInfoEx.szFacePicInfo[0].dwFileLenth = (int) faceInfo.getImage().length;  // 图片大小
            stuIn.stPersonInfoEx.szFacePicInfo[0].dwOffSet = 0;

            stuIn.nBufferLen = (int) faceInfo.getImage().length;
            ;
            stuIn.pBuffer = byteArrayToMemory(faceInfo.getImage(), faceInfo.getImage().length);
        }

        /*
         * 出参
         */
        NetSDKLib.NET_OUT_OPERATE_FACERECONGNITIONDB stuOut = new NetSDKLib.NET_OUT_OPERATE_FACERECONGNITIONDB();

        stuIn.write();
        boolean bRet = netsdk.CLIENT_OperateFaceRecognitionDB(m_hLoginHandle, stuIn, stuOut, 3000);
        stuOut.read();
        if (bRet) {
            resultInfo.setRet(true);
            String faceId = new String(stuOut.szUID).trim();
            resultInfo.setMsg(faceId);
            faceInfo.setFaceId(faceId);
        } else {
            resultInfo.setRet(false);
            resultInfo.setMsg(String.format("add face fail.%s", ToolKits.getErrorCodePrint()));
        }
        return resultInfo;
    }

    public ResultInfo editFace(FaceInfo faceInfo) {
        ResultInfo resultInfo = new ResultInfo();
        if (!isLogin) {
            resultInfo.setRet(false);
            resultInfo.setMsg("no login,can not edit face");
            return resultInfo;
        }
        // 入参
        NetSDKLib.NET_IN_OPERATE_FACERECONGNITIONDB stuIn = new NetSDKLib.NET_IN_OPERATE_FACERECONGNITIONDB();
        stuIn.emOperateType = NetSDKLib.EM_OPERATE_FACERECONGNITIONDB_TYPE.NET_FACERECONGNITIONDB_MODIFY;

        ///////// 使用人员扩展信息  ////////
        stuIn.bUsePersonInfoEx = 1;

        // 人脸库ID
        if (faceInfo.getGroupId() != null) {
            System.arraycopy(faceInfo.getGroupId().getBytes(), 0, stuIn.stPersonInfoEx.szGroupID, 0, faceInfo.getGroupId().getBytes().length);
        }

        // 人员唯一标识符
        if (faceInfo.getFaceId() != null) {
            System.arraycopy(faceInfo.getFaceId().getBytes(), 0, stuIn.stPersonInfoEx.szUID, 0, faceInfo.getFaceId().getBytes().length);
        }

        // 生日设置
        if (faceInfo.isBirthday()) {
            stuIn.stPersonInfoEx.wYear = (short) faceInfo.getBirthday().getYear();
            stuIn.stPersonInfoEx.byMonth = (byte) faceInfo.getBirthday().getMonth();
            stuIn.stPersonInfoEx.byDay = (byte) faceInfo.getBirthday().getDay();
        }

        // 性别,1-男,2-女,作为查询条件时,此参数填0,则表示此参数无效

        stuIn.stPersonInfoEx.bySex = (byte) faceInfo.getSex();

        // 人员名字
        if (faceInfo.getName() != null) {
            try {
                System.arraycopy(faceInfo.getName().getBytes("GBK"), 0, stuIn.stPersonInfoEx.szPersonName, 0, faceInfo.getName().getBytes("GBK").length);
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }

        // 证件类型
        stuIn.stPersonInfoEx.byIDType = (byte) faceInfo.getIdType();
        // 证件号
        if (faceInfo.getIdNumber() != null) {
            System.arraycopy(faceInfo.getIdNumber().getBytes(), 0, stuIn.stPersonInfoEx.szID, 0, faceInfo.getIdNumber().getBytes().length);
        }

        // 图片张数、大小、缓存设置
        // 图片张数、大小、缓存设置
        if (faceInfo.getImage() != null) {
            stuIn.stPersonInfoEx.wFacePicNum = 1; // 图片张数
            stuIn.stPersonInfoEx.szFacePicInfo[0].dwFileLenth = (int) faceInfo.getImage().length;  // 图片大小
            stuIn.stPersonInfoEx.szFacePicInfo[0].dwOffSet = 0;

            stuIn.nBufferLen = (int) faceInfo.getImage().length;
            ;
            stuIn.pBuffer = byteArrayToMemory(faceInfo.getImage(), faceInfo.getImage().length);
        }

        // 出参
        NetSDKLib.NET_OUT_OPERATE_FACERECONGNITIONDB stuOut = new NetSDKLib.NET_OUT_OPERATE_FACERECONGNITIONDB();

        stuIn.write();
        boolean ret = netsdk.CLIENT_OperateFaceRecognitionDB(m_hLoginHandle, stuIn, stuOut, 3000);
        stuOut.read();
        if (ret) {
            resultInfo.setRet(true);
            resultInfo.setMsg("edit face success");
        } else {
            resultInfo.setRet(false);
            resultInfo.setMsg(String.format("edit face fail.%S", ToolKits.getErrorCodePrint()));
        }
        return resultInfo;
    }

    public ResultInfo deleteFace(FaceInfo faceInfo) {
        ResultInfo resultInfo = new ResultInfo();
        if (!isLogin) {
            resultInfo.setRet(false);
            resultInfo.setMsg("no login,can not delete face");
            return resultInfo;
        }
        NetSDKLib.NET_IN_OPERATE_FACERECONGNITIONDB stuIn = new NetSDKLib.NET_IN_OPERATE_FACERECONGNITIONDB();
        stuIn.emOperateType = NetSDKLib.EM_OPERATE_FACERECONGNITIONDB_TYPE.NET_FACERECONGNITIONDB_DELETE;

        //////// 使用人员扩展信息  //////////
        stuIn.bUsePersonInfoEx = 1;

        // GroupID 赋值
        System.arraycopy(faceInfo.getGroupId().getBytes(), 0, stuIn.stPersonInfoEx.szGroupID, 0, faceInfo.getGroupId().getBytes().length);

        // UID赋值
        System.arraycopy(faceInfo.getFaceId().getBytes(), 0, stuIn.stPersonInfoEx.szUID, 0, faceInfo.getFaceId().getBytes().length);

        /*
         *  出参
         */
        NetSDKLib.NET_OUT_OPERATE_FACERECONGNITIONDB stuOut = new NetSDKLib.NET_OUT_OPERATE_FACERECONGNITIONDB();

        boolean ret = netsdk.CLIENT_OperateFaceRecognitionDB(m_hLoginHandle, stuIn, stuOut, 3000);
        if (ret) {
            resultInfo.setRet(true);
            resultInfo.setMsg("delete face success");
        } else {
            resultInfo.setRet(false);
            resultInfo.setMsg(String.format("delete face fail.%s", ToolKits.getErrorCodePrint()));
        }
        return resultInfo;
    }

    public ResultInfo stopSearchFace() {
        ResultInfo resultInfo = new ResultInfo();
        if (!isLogin) {
            resultInfo.setRet(false);
            resultInfo.setMsg("no login,can not search face");
            return resultInfo;
        }
        if (m_FindHandle.longValue() == 0) {
            resultInfo.setRet(false);
            resultInfo.setMsg("not is searching");
            return resultInfo;
        }
        boolean ret = netsdk.CLIENT_StopFindFaceRecognition(m_FindHandle);
        if (ret) {
            resultInfo.setRet(true);
            resultInfo.setMsg("search is stopped");
        } else {
            resultInfo.setRet(false);
            resultInfo.setMsg(String.format("stop search failed.%s", ToolKits.getErrorCodePrint()));
        }
        return resultInfo;
    }

    public ResultInfo searchByFace(byte[] image, int length, String similar, CallBack callBack, long timeOut) {
        long searchByFaceTime = System.currentTimeMillis();
        String searchByFaceStr = String.valueOf(searchByFaceTime);
        System.out.println("查询调用开始时间:" + searchByFaceStr);


        nToken = 0;
        int nTotalCount = 0;
        ResultInfo resultInfo = new ResultInfo();
        if (!isLogin) {
            resultInfo.setRet(false);
            resultInfo.setMsg("no login,can not search face");
            return resultInfo;
        }
        /*
         * 入参, IVVS设备，查询条件只有  stuInStartFind.stPerson 里的参数有效
         */
        NetSDKLib.NET_IN_STARTFIND_FACERECONGNITION stuIn = new NetSDKLib.NET_IN_STARTFIND_FACERECONGNITION();

        // 人员信息查询条件是否有效, 并使用扩展结构体
        stuIn.bPersonExEnable = 1;

        // 图片信息

        if (image.length > 0 && length > 0) {
            Memory memory = byteArrayToMemory(image, length);
            stuIn.pBuffer = memory;
            stuIn.nBufferLen = (int) memory.size();
            stuIn.stPersonInfoEx.wFacePicNum = 1;
            stuIn.stPersonInfoEx.szFacePicInfo[0].dwOffSet = 0;
            stuIn.stPersonInfoEx.szFacePicInfo[0].dwFileLenth = (int) memory.size();
        }

        // 相似度
        if (!similar.isEmpty()) {
            stuIn.stMatchOptions.nSimilarity = Integer.parseInt(similar);
        }

        stuIn.stFilterInfo.nGroupIdNum = 0;
        stuIn.stFilterInfo.nRangeNum = 1;

        stuIn.stFilterInfo.szRange[0] = NetSDKLib.EM_FACE_DB_TYPE.NET_FACE_DB_TYPE_BLACKLIST;  // 待查询数据库类型，设备只支持一个

        /*
         * 出参
         */
        NetSDKLib.NET_OUT_STARTFIND_FACERECONGNITION stuOut = new NetSDKLib.NET_OUT_STARTFIND_FACERECONGNITION();
        stuIn.write();
        boolean ret = netsdk.CLIENT_StartFindFaceRecognition(m_hLoginHandle, stuIn, stuOut, 4000);
        stuOut.read();
        if (ret) {
            m_FindHandle = stuOut.lFindHandle;
            nTotalCount = stuOut.nTotalCount;
            nToken = stuOut.nToken;
        } else {
            resultInfo.setRet(false);
            resultInfo.setMsg(String.format("CLIENT_StartFindFaceRecognition Failed, Error:%s", ToolKits.getErrorCodePrint()));
            return resultInfo;
        }

        if (nTotalCount == 0) {   // 查询失败
            // 查询失败，关闭查询
            return stopSearchFace();
        } else if (nTotalCount == -1) {  // 设备正在处理，通过订阅来查询处理进度
            if (timeOut > 0) {
                resultInfo = attachSearchState(callBack);
                if (resultInfo.getRet() == false) {
                    return resultInfo;
                }
                long startTime = System.currentTimeMillis();
                while (true) {
                    long endTime = System.currentTimeMillis();
                    if ((endTime - startTime) > timeOut) {
                        resultInfo.setRet(false);
                        resultInfo.setMsg("wait search timeout");
                        return resultInfo;
                    } else {
                        if (faceId != null) {
                            resultInfo.setRet(true);
                            resultInfo.setMsg(String.valueOf(faceId.get()));
                            faceId = null;
                            return resultInfo;
                        }
                        try {
                            Thread.sleep(100);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
            } else {
//                long searchByFaceTime2=System.currentTimeMillis();
//                String searchByFaceStr2=String.valueOf(searchByFaceTime2);
//                System.out.println("查询调用结束时间:"+searchByFaceStr2);
                return attachSearchState(callBack);
            }
        } else {
            Long beginTime = System.currentTimeMillis();
            Long endTime;
            while (true) {
                endTime = System.currentTimeMillis();
                if ((endTime - beginTime) > 5000) {
                    break;
                }

                NetSDKLib.CANDIDATE_INFOEX[] caInfoexs = doFindNextPerson(0, 1);
                if (caInfoexs == null) {
                    //receiveFaceCallBack.receiveFaceInvoke(lLoginID.longValue(), lAttachHandle.longValue(), null);
                    //Thread.sleep(100);
                    continue;
                }

                //for (int j = 0; j < caInfoexs.length; j++) {
                //index = i + nFindCount * nCount + 1;
                FaceInfo faceInfo = new FaceInfo(new String(caInfoexs[0].stPersonInfo.szGroupID).trim(),
                        new String(caInfoexs[0].stPersonInfo.szUID).trim(), String.valueOf(caInfoexs[0].bySimilarity));
                callBack.getReceiveFaceCallBack().receiveFaceInvoke(clientId, m_hLoginHandle.longValue(), m_attachHandle.longValue(), faceInfo);
                break;
            }

            // 关闭查询
            return doFindClosePerson();
        }
    }

    public NetSDKLib.CANDIDATE_INFOEX[] doFindNextPerson(int beginNum, int nCount) {
        /*
         *入参
         */
        NetSDKLib.NET_IN_DOFIND_FACERECONGNITION stuIn = new NetSDKLib.NET_IN_DOFIND_FACERECONGNITION();
        stuIn.lFindHandle = m_FindHandle;
        stuIn.nCount = nCount;     // 当前想查询的记录条数
        stuIn.nBeginNum = beginNum;     // 查询起始序号

        /*
         * 出参
         */
        NetSDKLib.NET_OUT_DOFIND_FACERECONGNITION stuOut = new NetSDKLib.NET_OUT_DOFIND_FACERECONGNITION();
        ;
        stuOut.bUseCandidatesEx = 1;                // 是否使用候选对象扩展结构体

        // 必须申请内存，每次查询几个，必须至少申请几个，最大申请20个
        for (int i = 0; i < nCount; i++) {
            stuOut.stuCandidatesEx[i].stPersonInfo.szFacePicInfo[0].nFilePathLen = 256;
            stuOut.stuCandidatesEx[i].stPersonInfo.szFacePicInfo[0].pszFilePath = new Memory(256);
        }

        stuIn.write();
        stuOut.write();
        if (netsdk.CLIENT_DoFindFaceRecognition(stuIn, stuOut, 4000)) {
            stuIn.read();
            stuOut.read();

            if (stuOut.nCadidateExNum == 0) {
                return null;
            }

            // 获取到的信息
            NetSDKLib.CANDIDATE_INFOEX[] stuCandidatesEx = new NetSDKLib.CANDIDATE_INFOEX[stuOut.nCadidateExNum];
            for (int i = 0; i < stuOut.nCadidateExNum; i++) {
                stuCandidatesEx[i] = new NetSDKLib.CANDIDATE_INFOEX();
                stuCandidatesEx[i] = stuOut.stuCandidatesEx[i];
            }

            return stuCandidatesEx;
        } else {
            // System.out.println("CLIENT_DoFindFaceRecognition Failed, Error:" + ToolKits.getErrorCodePrint());
        }

        return null;
    }

    public ResultInfo doFindClosePerson() {
        ResultInfo resultInfo = new ResultInfo();
        if (m_FindHandle.longValue() != 0) {
            boolean bRet = netsdk.CLIENT_StopFindFaceRecognition(m_FindHandle);
            resultInfo.setRet(bRet);
            resultInfo.setMsg("stop search...");
        }
        return resultInfo;
    }

    public ResultInfo attachSearchState(CallBack callBack) {
        ResultInfo resultInfo = new ResultInfo();
        if (!isLogin) {
            resultInfo.setRet(false);
            resultInfo.setMsg("no login,can not attach search");
            return resultInfo;
        }

        NetSDKLib.NET_IN_FACE_FIND_STATE stuIn = new NetSDKLib.NET_IN_FACE_FIND_STATE();
        stuIn.nTokenNum = 1;
        stuIn.nTokens = new IntByReference(nToken);  // 查询令牌
        stuIn.cbFaceFindState = callBack.getFaceFindState();

        /*
         * 出参
         */
        NetSDKLib.NET_OUT_FACE_FIND_STATE stuOut = new NetSDKLib.NET_OUT_FACE_FIND_STATE();

        stuIn.write();
        m_attachHandle = netsdk.CLIENT_AttachFaceFindState(m_hLoginHandle, stuIn, stuOut, 4000);

        String str = String.valueOf(System.currentTimeMillis());

        //System.out.println("Attach完成时间"+str);
        stuOut.read();

//        long attachSearchStateTime=System.currentTimeMillis();
//        String attachSearchStateStr=String.valueOf(attachSearchStateTime);
//        System.out.println("Attach完成时间:"+attachSearchStateStr);

        if (m_attachHandle.longValue() != 0) {
            resultInfo.setRet(true);
            resultInfo.setMsg("start search...attachSearchState Succeed!");
        } else {
            resultInfo.setRet(false);
            resultInfo.setMsg(String.format("attachSearchState Fail.%s", ToolKits.getErrorCodePrint()));
        }
        return resultInfo;
    }


    public ResultInfo startRealRecognize(CallBack callBack, int channels) {
        ResultInfo resultInfo = new ResultInfo();
        if (!isLogin) {
            resultInfo.setRet(false);
            resultInfo.setMsg("no login,can not attach search");
            return resultInfo;
        }
        int bNeedPicture = 0; // 是否需要图片

        int channel = (channels == 1) ? 0 : -1;
        analyzerHandle = netsdk.CLIENT_RealLoadPictureEx(m_hLoginHandle, channel,
                NetSDKLib.EVENT_IVS_FACERECOGNITION, bNeedPicture, callBack.getAnalyzerDataCallBack(), null, null);
        if (analyzerHandle.longValue() == 0) {
            resultInfo.setRet(false);
            resultInfo.setMsg("CLIENT_RealLoadPictureEx Failed, Error:" + ToolKits.getErrorCodePrint());
            return resultInfo;
        } else {
            resultInfo.setRet(true);
            String channelCount = (channels == 1) ? "1" : "所有";
            System.out.println("通道数[" + channelCount + "]订阅成功！");
        }
        return resultInfo;
    }

    public void stopRealRecognize() {
        if (analyzerHandle.longValue() != 0) {
            netsdk.CLIENT_StopLoadPic(analyzerHandle);
            analyzerHandle.setValue(0);
        }
    }
}
