package com.pharos.walker.constants;

/**
 * Created by zhanglun on 2021/6/15
 * Describe:
 */
public class Api {
    public static final String baseUrl = "http://pharos.ewj100.com";
//    public static final String baseUrl = "http://tyuftw.natappfree.cc";
//    public static final String testUrl = "http://tyuftw.natappfree.cc";
    public static final String updateUrl = "https://pharos.ewj100.com/walknew.apk";
    public static final String getNewVersion = baseUrl + "/deviceVersion/newVersion";//get  参数type  传数字 101 长沙下肢 102 安徽下肢  201 长沙气动 202 安徽气动
    public static final String uploadVersionInfo = baseUrl + "/productStock/updateVersion"; //put "参数 versionStr（版本）  macAddress（mac地址）";
    public static final String downloadAppUrl = baseUrl + "/file/"; //get "参数 {date}/{fileName}";
    public static final String tokenUrl = baseUrl + "/oauth/token";//参数username :test Password：test
    public static final String uploadUser = baseUrl + "/palnUser/saveBatch";//post 上传用户
    public static final String getUserInfo = baseUrl + "/palnUser/getByUId/";//{uid } get
    public static final String uploadPlan = baseUrl +  "/plan/save";//post 上传计划
    public static final String getPlan = baseUrl + "/plan/listByUid/";//{uid }用户id get 查询计划
    public static final String uploadTrainRecord = baseUrl + "/planUserTrainRecord/save";//post 上传训练计划
    public static final String getRecord = baseUrl + "/planUserTrainRecord/pageByUid/";//{uid } 用户id get 获取训练记录
    public static final String getActivationCode = baseUrl + "/productStock/getByMac/";// {macAddress}  get 获取激活码
    public static final String qrUrl = baseUrl + "/user/srBindAdress/";// {uid}  二位码链接
    public static final String getServerTimestamp = baseUrl + "/sys/getTime";// {uid}  获取服务器时间
    public static final String getHospitalByAddress = baseUrl + "/HospitalInfo/listCompatible";// get  获取当前地址下的医院  参数 province（省） city（市） area（区）
    public static final String getDoctorByHospital = baseUrl + "/HospitalInfo/listByDoctor";// get  获取当前医院下的医生  参数 id（医院id)
    public static final String getPlatformQr = baseUrl + "/liveQrCode/qrcodeByMac/";// get  获取平台二维码
    public static final String getPlatformQrScanUser = baseUrl + "/deviceScanSignLog/list";// 参数macAddress get  获取平台二维码扫描用户
    public static final String deletePlatformQrScanUser = baseUrl + "/deviceScanSignLog/deleteByMacAddress/";// {macAddress} get  删除平台二维码扫描用户
    public static final String clearPlatformQrScanUser = baseUrl + "/palnUser/cleanBindInfo";// {macAdd} get  删除平台二维码扫描用户
    public static final String getHospitalByMac = baseUrl + "/productStock/getBindHospital/";// {macAddress} get 获取设备绑定的医院
    public static final String checkUser = baseUrl + "/palnUser/checkIdCard";// get 参数,userId macAdd 判断当前用户是否属于这个设备
    public static final String uploadEvaluateRecord = baseUrl + "/evaluationRecords/addEvaluationRecords";// 上传评估记录
    public static final String getEvaluateRecord = baseUrl + "/evaluationRecords/getEvaluationRecords";// 获取评估记录
    public static final String getQrCodeLink = baseUrl + "/liveQrCode/getLiveQrCodeUrl";// 获取设备二维码链接
//    public static final String updateUrl = "https://www.flssh.cn/jiqiren2/public/uploads/20210609/7738d738127aeeb377cac86d3ad03dd7.apk";
}
