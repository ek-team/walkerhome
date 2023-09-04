package com.pharos.walker.utils;

import android.util.Log;

import com.google.gson.Gson;
import com.pharos.walker.beans.PlanEntity;
import com.pharos.walker.beans.SubPlanEntity;
import com.pharos.walker.beans.TokenBean;
import com.pharos.walker.constants.Api;
import com.pharos.walker.constants.Global;
import com.pharos.walker.database.SubPlanManager;
import com.pharos.walker.database.TrainPlanManager;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import okhttp3.Request;

/**
 * Created by zhanglun on 2021/5/7
 * Describe:
 */
public class MyUtil {
    public static int getDiagnosticNum(String diag){
        switch (diag){
            case "请选择":
                return 0;
            case "全髋关节置换":
                return 1;
            case "全膝关节置换":
                return 2;
            case "股骨颈骨折":
                return 3;
            case "股骨转子间骨折":
                return 4;
            case "胫骨平台骨折（钢板固定）":
                return 5;
            case "胫骨平台骨折（钢板内固定）":
                return 6;
            case "胫骨中段骨折（石膏固定）":
                return 7;
            case "胫骨中段骨折（髓内钉）":
                return 8;
            case "胫骨中段骨折（桥接钢板）":
                return 9;
            case "胫骨远端骨折":
                return 10;
            case "踝关节骨折（钢板内固定）":
                return 11;
            case "跟骨骨折（钢板固定）":
                return 12;
            case "踝关节韧带损伤（踝关节韧带重建术）":
                return 13;
            case "股骨头坏死（腓骨移植术）":
                return 14;
            default:
                return 15;

        }
    }
    public static void insertTemplate(int saveResult){
        int diagType = getDiagnosticNum(SPHelper.getUser().getDiagnosis());
        saveResult = getCalcValue(saveResult);
        switch (diagType){
            case 0:
                break;
            case 1://全髋关节置换
                TrainPlanManager.getInstance().insertList1(saveResult);
                SubPlanManager.getInstance().modifySubPlanData(25, Global.MinTrainStep,Global.MaxTrainStep);
                break;
            case 2://全膝关节置换
                TrainPlanManager.getInstance().insertList2(saveResult);
                SubPlanManager.getInstance().modifySubPlanData(Global.TrainTime, Global.MinTrainStep,Global.MaxTrainStep);
                break;
            case 3://股骨颈骨折
            case 4://股骨转子间骨折
                TrainPlanManager.getInstance().insertList3(saveResult);
                SubPlanManager.getInstance().modifySubPlanData(Global.TrainTime, Global.MinTrainStep,Global.MaxTrainStep);
                break;
            case 5://胫骨平台骨折（钢板固定）
                TrainPlanManager.getInstance().insertList4(saveResult);
                SubPlanManager.getInstance().modifySubPlanData(Global.TrainTime, Global.MinTrainStep,Global.MaxTrainStep);
                break;
            case 6://胫骨平台骨折（钢板内固定）
                TrainPlanManager.getInstance().insertList5(saveResult);
                SubPlanManager.getInstance().modifySubPlanData(Global.TrainTime, Global.MinTrainStep,Global.MaxTrainStep);
                break;
            case 7://胫骨中段骨折（石膏固定）
                TrainPlanManager.getInstance().insertList6(saveResult);
                SubPlanManager.getInstance().modifySubPlanData(Global.TrainTime, Global.MinTrainStep,Global.MaxTrainStep);
                break;
            case 8://胫骨中段骨折（髓内钉）
            case 9://胫骨中段骨折（桥接钢板）
                TrainPlanManager.getInstance().insertList7(saveResult);
                SubPlanManager.getInstance().modifySubPlanData(Global.TrainTime, Global.MinTrainStep,Global.MaxTrainStep);
                break;
            case 10://胫骨远端骨折
                break;
            case 11://踝关节骨折（钢板内固定）
                TrainPlanManager.getInstance().insertList8(saveResult);
                SubPlanManager.getInstance().modifySubPlanData(Global.TrainTime, Global.MinTrainStep,Global.MaxTrainStep);
                break;
            case 12://跟骨骨折（钢板固定）
                TrainPlanManager.getInstance().insertList9(saveResult);
                SubPlanManager.getInstance().modifySubPlanData(Global.TrainTime, Global.MinTrainStep,Global.MaxTrainStep);
                break;
            case 13://踝关节韧带损伤（踝关节韧带重建术）
                TrainPlanManager.getInstance().insertList10(saveResult);
                SubPlanManager.getInstance().modifySubPlanData(Global.TrainTime, Global.MinTrainStep,Global.MaxTrainStep);
                break;
            case 14://股骨头坏死（腓骨移植术）
                TrainPlanManager.getInstance().insertList11(saveResult);
                SubPlanManager.getInstance().modifySubPlanData(Global.TrainTime, Global.MinTrainStep,Global.MaxTrainStep);
                break;
        }
    }
    public static int getCalcValue(int loadWeight){
        List<PlanEntity> planEntityList = TrainPlanManager.getInstance().getPlanListByUserId(SPHelper.getUserId());
        SubPlanEntity currentSubPlanEntity = SubPlanManager.getInstance().getThisWeekLoadEntity(SPHelper.getUserId());
        if (planEntityList != null && planEntityList.size() > 0  && currentSubPlanEntity != null){
            int currentWeight =  currentSubPlanEntity.getLoad();
            List<SubPlanEntity> subPlanEntityList = SubPlanManager.getInstance().loadDataByUserId(SPHelper.getUserId());
            int startLoad = TrainPlanManager.getInstance().getInitLoad(SPHelper.getUserId());
            int diff = 0;
            boolean isEnd = false;
            if (loadWeight > currentWeight){
                for (int i = 0; i < 100; i++){
                    diff++;
                    for (SubPlanEntity entity: subPlanEntityList){
                        if ((entity.getLoad() >= currentWeight) && (currentWeight + diff >= loadWeight)){
                            isEnd = true;
                            break;
                        }
                    }
                    if (isEnd){
                        break;
                    }
                }
                loadWeight = (startLoad + diff <= 0)? 1:startLoad + diff;//值最少1kg
            }
            if (loadWeight < currentWeight){
                for (int i = 0; i < 100; i++){
                    diff--;
                    for (SubPlanEntity entity: subPlanEntityList){
                        if ((entity.getLoad() <= currentWeight) && (currentWeight + diff <= loadWeight)){
                            isEnd = true;
                            break;
                        }
                    }
                    if (isEnd){
                        break;
                    }
                }
                loadWeight = (startLoad + diff <= 0)? 1:startLoad + diff;//值最少1kg
            }
            Log.e("Insert train plan", "insertList1:startLoad =  " + startLoad );
            Log.e("Insert train plan", "insertList1:diff =  " + diff );
        }
        return loadWeight;
    }
    public static String getPlanSummar(){
        int diagType = getDiagnosticNum(SPHelper.getUser().getDiagnosis());
        switch (diagType){
            case 1:
            case 2:
                return "术后第一天开始负重，逐步负重,6 周内达完全负重";
            case 3:
            case 4:
                return "1周时为健侧 51%，逐步增加,12周时为健侧 87%，直至 100%";
            case 5:
                return "6周时20kg，逐步增加，16周左右达到健侧100%";
            case 6:
                return "2周为一个周期，逐步由起始重量增加至完全负重，总周期39周";
            case 7:
            case 8:
            case 9:
                return "2周为一个周期，逐步由起始重量增加至完全负重，总周期24周";
            case 11:
                return "术后两天开始训练，逐渐 16 周后达到完全负重";
            case 12:
                return "术后两天开始训练，6周内10kg，8周20kg，10周40kg，直至完全负重";
            case 13:
                return "5公斤开始，逐步负重直至完全负重";
            case 14:
                return "术后七周达到12公斤，每2周增加5公斤，直到完全负重";
            default:
                return "无";
        }
    }
    public static String getMac() {
        String macSerial = null;
        String str = "";
        try
        {
            Process pp = Runtime.getRuntime().exec("cat /sys/class/net/wlan0/address ");
            InputStreamReader ir = new InputStreamReader(pp.getInputStream());
            LineNumberReader input = new LineNumberReader(ir);

            for (; null != str;)
            {
                str = input.readLine();
                if (str != null)
                {
                    macSerial = str.trim();// 去空格
                    break;
                }
            }
        } catch (IOException ex) {
            // 赋予默认值
            ex.printStackTrace();
        }
        return macSerial;
    }
    /**
     * android 7.0及以上 （2）扫描各个网络接口获取mac地址
     *
     */
    /**
     * 获取设备HardwareAddress地址
     *
     * @return
     */
    public static String getMachineHardwareAddress() {
        Enumeration<NetworkInterface> interfaces = null;
        try {
            interfaces = NetworkInterface.getNetworkInterfaces();
        } catch (SocketException e) {
            e.printStackTrace();
        }
        String hardWareAddress = null;
        NetworkInterface iF = null;
        if (interfaces == null) {
            return null;
        }
        while (interfaces.hasMoreElements()) {
            iF = interfaces.nextElement();
            try {
                hardWareAddress = bytesToString(iF.getHardwareAddress());
                if (hardWareAddress != null)
                    break;
            } catch (SocketException e) {
                e.printStackTrace();
            }
        }
        return hardWareAddress;
    }
    /***
     * byte转为String
     *
     * @param bytes
     * @return
     */
    private static String bytesToString(byte[] bytes) {
        if (bytes == null || bytes.length == 0) {
            return null;
        }
        StringBuilder buf = new StringBuilder();
        for (byte b : bytes) {
            buf.append(String.format("%02X:", b));
        }
        if (buf.length() > 0) {
            buf.deleteCharAt(buf.length() - 1);
        }
        return buf.toString();
    }
    public static void getToken(){
        OkHttpUtils.getAsyncToken(Api.tokenUrl + "?grant_type=client_credentials" , true, new OkHttpUtils.DataCallBack() {
            @Override
            public void requestFailure(Request request, IOException e) {
                e.printStackTrace();

            }

            @Override
            public void requestSuccess(String result) throws Exception {
                Log.e("MainActivity", "requestSuccess: " + result);
                TokenBean tokenBean = new Gson().fromJson(result,TokenBean.class);
                if (tokenBean.getCode() == 0){
                    SPHelper.saveToken(tokenBean.getData().getAccess_token());
                }

            }
        });
    }
    //deleteFile(newFile("data/data/"+getPackageName()));
    public static void deleteFile(File file) {
        if (file.exists()) {
            if (file.isFile()){
                file.delete();
                return;
            }
            if (file.isDirectory()) {
                File[] childFile = file.listFiles();
                if (childFile == null || childFile.length == 0) {
                    file.delete();
                    return;
                }
                for (File f : childFile) {
                    deleteFile(f);
                }
                file.delete();
            }
        }
    }
    /**
     * 获取目录下所有文件
     * @param path 指定目录路径
     * @return
     */
    public static List<String> getFilesAllName(String path) {
        File file=new File(path);
        File[] files=file.listFiles();
        if (files == null){
            Log.e("error","空目录");
            return null;
        }
        List<String> s = new ArrayList<>();
        for(int i =0;i<files.length;i++){
            s.add(files[i].getAbsolutePath());
        }
        return s;
    }
    public static boolean isNoPlanUser(){
        return getDiagnosticNum(SPHelper.getUser().getDiagnosis()) >= 15 || getDiagnosticNum(SPHelper.getUser().getDiagnosis()) == 10;
    }
}
