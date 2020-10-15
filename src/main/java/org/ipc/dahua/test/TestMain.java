package org.ipc.dahua.test;


import org.ipc.dahua.commcon.Module;
import org.ipc.dahua.dto.Config;
import org.ipc.dahua.dto.ResultInfo;
import org.ipc.dahua.service.intf.ICatchFaceCallBack;
import org.ipc.dahua.service.intf.IReceiveFaceCallBack;
import org.ipc.dahua.service.intf.ISearchStateCallBack;

import java.io.*;
import java.net.ConnectException;
import java.net.MalformedURLException;

/**
 * @Author 洋芋_Sir
 * @Date 2020/7/2
 * @description 测试实例
 **/
public class TestMain {
    IReceiveFaceCallBack receiveFaceCallBack;
    ISearchStateCallBack searchStateCallBack;
    ICatchFaceCallBack catchFaceCallBack;
    byte[] buffer = null;

    private void getBytes(String filePath) {
        try {
            File file = new File(filePath);
            FileInputStream fis = new FileInputStream(file);
            ByteArrayOutputStream bos = new ByteArrayOutputStream(1024);
            byte[] b = new byte[1024];
            int n;
            while ((n = fis.read(b)) != -1) {
                bos.write(b, 0, n);
            }
            fis.close();
            bos.close();
            buffer = bos.toByteArray();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void init() {
//        receiveFaceCallBack=new ReceiveFaceImpl();
//        searchStateCallBack=new SearchStateImpl();
//        catchFaceCallBack=new CatchFaceImpl();
        //getBytes("C:\\Users\\dell\\Desktop\\timg (11).jpg");

    }

    public void test() throws InterruptedException {
        Config config = new Config("192.168.0.100", 37777, "admin", "admin123", "80", -1);
        Client client = new Client(null, null, searchStateCallBack, receiveFaceCallBack, catchFaceCallBack);
        ResultInfo resultInfo = client.init();
        if (!resultInfo.getRet()) {
            System.out.println(resultInfo.getMsg());
            return;
        }
        resultInfo = client.login(config);
        if (!resultInfo.getRet()) {
            System.out.println(resultInfo.getMsg());
            return;
        }


//        long startTime=System.currentTimeMillis();
//          resultInfo=client.search(buffer,buffer.length,"1234567890");
//        long endTime=System.currentTimeMillis();


//        String str= String.valueOf(endTime-startTime);
//        String str2= String.valueOf(endTime);
//        System.out.println("调用查询时间:"+str);
//        System.out.println("调用完成时间:"+str2);


//        System.out.println("开始同步搜索:"+String.valueOf(System.currentTimeMillis()));
//        resultInfo=client.syncSearch(buffer,buffer.length,"1234567890",4000);
//        System.out.println("结束同步搜索:"+String.valueOf(System.currentTimeMillis()));
//        System.out.println("同步搜索结果:"+resultInfo.getMsg());


        if (!resultInfo.getRet()) {
            System.out.println(resultInfo.getMsg());
        }

        long st = System.currentTimeMillis();
        client.getDeviceInfo();
        String ss = Module.channelToSerialNo(0);
        long et = System.currentTimeMillis();
        String s1 = String.valueOf(et - st);
        System.out.println("通道查询序列号时间:" + s1 + "  查询到的序列号是:" + ss);
        client.startAnalyzer();

        long startTime2 = System.currentTimeMillis();
        while (true) {
            long endTime2 = System.currentTimeMillis();
//            if ((endTime2-startTime2)>4000){
//                return;
//            }
            // System.out.println("等待返回:"+System.currentTimeMillis());
            Thread.sleep(100);
        }
    }

    public static void main(String[] args) throws Exception,InterruptedException, MalformedURLException, ConnectException {
//        TestMain test=new TestMain();
//        test.init();
//
//        test.test();


//
//        CyclicBarrier barrier=new CyclicBarrier(10, new Runnable() {
//            @Override
//            public void run() {
//                System.out.println("集合完毕");
//            }
//        });
//
//        for (int i=0;i<10;i++){
//            new Thread(new Runnable() {
//                @Override
//                public void run() {
//                    try {
//                        barrier.await();
//                    } catch (InterruptedException e) {
//                        e.printStackTrace();
//                    } catch (BrokenBarrierException e) {
//                        e.printStackTrace();
//                    }
//                    test.init();
//                    try {
//                        test.test();
//                    } catch (InterruptedException e) {
//                        e.printStackTrace();
//                    }
//                }
//            }).start();
//        }


        //System.out.println( "Hello World!" );


        // 客流眼客流统计
        FlowClient client = new FlowClient(new TestVideoStat());
        //Client client=new Client("192.168.0.163",37777,"admin","admin123",new TestVideoStat());
        client.Init();                          // 初始化
        client.attachVideoStatSummary();        // 订阅
        // 网络摄像机支持onvif 协议，这里登录摄像机可以查询摄像头的硬件信息
//        OnvifDevice device = new OnvifDevice("192.168.0.163","admin","admin123");
//        System.out.println(device.getDeviceInfo().toString());
        while (true) {
            Thread.sleep(1000);
        }

    }


}
