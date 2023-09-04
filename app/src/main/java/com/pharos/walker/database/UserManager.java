package com.pharos.walker.database;

import com.pharos.walker.beans.InfoBean;
import com.pharos.walker.beans.UserBean;
import com.pharos.walker.constants.Global;
import com.pharos.walker.greendao.UserBeanDao;
import com.pharos.walker.utils.DataTransformUtil;
import com.pharos.walker.utils.DateFormatUtil;
import com.pharos.walker.utils.GreenDaoHelper;
import com.pharos.walker.utils.SPHelper;
import com.pharos.walker.utils.SnowflakeIdUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhanglun on 2021/4/26
 * Describe:
 */
public class UserManager {
    private final static int limit = 20;
    private static volatile UserManager instance = null;

    private UserBeanDao mUserDao;

    private UserManager() {
        mUserDao = GreenDaoHelper.getDaoSession().getUserBeanDao();
    }

    public static UserManager getInstance() {
        if (instance == null) {
            synchronized (UserManager.class) {
                if (instance == null) {
                    instance = new UserManager();
                }
            }
        }
        return instance;
    }
    /**
     * 查询所有用户信息
     *
     * @param
     * @return
     */
    public List<UserBean> loadAll() {
        return mUserDao.loadAll();
    }
    /**
     * 查询所有用户信息
     *
     * @param
     * @return
     */
    public List<UserBean> loadAllByDate() {
        mUserDao.detachAll();
        List<UserBean> userBeanList = mUserDao.queryBuilder().where(UserBeanDao.Properties.CaseHistoryNo.notEq("123456")).orderDesc(UserBeanDao.Properties.CreateDate).list();
        userBeanList.add(0,loadGuest());
        return userBeanList;
    }
    /**
     * 查询所有用户信息
     *
     * @param
     * @return
     */
    public List<UserBean> loadAllExceptGuest() {
        return mUserDao.queryBuilder().where(UserBeanDao.Properties.CaseHistoryNo.notEq("123456")).list();
    }
    /**
     * 查询所有新建用户信息
     *
     * @param
     * @return
     */
    public List<UserBean> loadAllNewUser() {
        return mUserDao.queryBuilder().where(UserBeanDao.Properties.IsFirstRun.eq(Global.UploadStatus),UserBeanDao.Properties.IsUpload.eq(Global.UploadStatus)).list();
    }
    /**
     * 查询所有未同步云端的用户信息
     *
     * @param
     * @return
     */
    public List<UserBean> loadNoNetUploadUser() {
        List<UserBean> list = mUserDao.queryBuilder().where(UserBeanDao.Properties.CaseHistoryNo.notEq("123456"),UserBeanDao.Properties.IsUpload.notEq(Global.UploadNetStatus)).list();
        List<UserBean> userBeanList = new ArrayList<>();
        String macAddress = ActivationCodeManager.getInstance().getCodeBean().getMacAddress();
        for (UserBean bean: list) {
            bean.setMacAdd(macAddress);
            userBeanList.add(bean);
        }
        return userBeanList;
    }
    public UserBean loadByUserId(long userId){
        return mUserDao.queryBuilder().where(UserBeanDao.Properties.UserId.eq(userId)).unique();
    }
    /**
     * 查询所有新建用户信息
     *
     * @param
     * @return
     */
    public List<UserBean> loadCompareResult(String idString) {
        List<UserBean> userBeanList = loadAllExceptGuest();
        List<UserBean> userBeans = new ArrayList<>();
        for (int i = 0; i < userBeanList.size(); i++) {
            UserBean userBean = userBeanList.get(i);
            if (idString.contains(String.valueOf(userBean.getUserId()))){
                continue;
            }
            userBeans.add(userBean);
        }
        return userBeans;
    }
    /**
     * 查询所有新建用户信息
     *
     * @param
     * @return
     */
    public List<String> loadAllUserId() {
        List<UserBean> userBeanList = loadAllExceptGuest();
        List<String> idList = new ArrayList<>();
        for (int i = 0; i < userBeanList.size(); i++){
            idList.add(userBeanList.get(i).getUserId() + "");
        }
        return idList;
    }
    public List<String> loadAllUserIdNoPlan() {
        List<UserBean> userBeanList = loadAllExceptGuest();
        List<String> idList = new ArrayList<>();
        for (int i = 0; i < userBeanList.size(); i++){
            if (TrainPlanManager.getInstance().getPlanListByUserId(userBeanList.get(i).getUserId()).size() <= 0){
                idList.add(userBeanList.get(i).getUserId() + "");
            }
        }
        return idList;
    }
    public List<InfoBean> loadAllUserIdPlanUpdate() {
        List<UserBean> userBeanList = loadAllExceptGuest();
        List<InfoBean> idList = new ArrayList<>();
        for (int i = 0; i < userBeanList.size(); i++){
            InfoBean infoBean  = new InfoBean();
            if (TrainPlanManager.getInstance().getPlanListByUserId(userBeanList.get(i).getUserId()).size() <= 0){
                infoBean.setUserId(userBeanList.get(i).getUserId());
                infoBean.setUpdateDate(0);
            }else {
                infoBean.setUserId(userBeanList.get(i).getUserId());
                infoBean.setUpdateDate(TrainPlanManager.getInstance().getPlanUpdateDate(userBeanList.get(i).getUserId()));
            }
            idList.add(infoBean);
        }
        return idList;
    }
    public List<Long> loadAllUserIdRecord() {
        List<UserBean> userBeanList = loadAllExceptGuest();
        List<Long> idList = new ArrayList<>();
        for (int i = 0; i < userBeanList.size(); i++){
            if (UserTrainRecordManager.getInstance().loadAll(userBeanList.get(i).getUserId()).size() > 0 && (userBeanList.get(i).getIsRecordUpdate() == 0)){
                idList.add(userBeanList.get(i).getUserId());
            }
        }
        return idList;
    }
    /**
     * 查询访客信息
     *
     * @param
     * @return
     */
    public UserBean loadGuest() {
        return mUserDao.queryBuilder().where(UserBeanDao.Properties.Id.eq(0),UserBeanDao.Properties.CaseHistoryNo.eq("123456")).unique();
    }
    /**
     * 查询访客信息
     *
     * @param
     * @return
     */
    public  void changeGuest(String name,String flag) {
        UserBean userBean = loadGuest();
        if (userBean != null && !name.equals(userBean.getName())){
            userBean.setName(name);
            mUserDao.detachAll();
            mUserDao.update(userBean);
            if (flag.equals("123456")){
                SPHelper.saveUser(userBean);
            }
        }

    }

    /**
     * 添加新用户
     *
     * @param userBean
     * @return
     */
    public void insert(UserBean userBean, int mode) {
//        userBean.setPingYin(MyFunc.toPinyin(userBean.getName()));
        if (mode == 0){
            userBean.setCreateDate(DateFormatUtil.getNowDate());
            userBean.setIsFirstRun(Global.UploadStatus);
            userBean.setIsUpload(Global.UploadStatus);
            userBean.setIsRecordUpdate(1);
            userBean.setKeyId(SnowflakeIdUtil.getUniqueId());
            mUserDao.insert(userBean);
            SPHelper.saveUser(userBean);
//            TrainPlanManager.getInstance().insertList(SnowflakeIdUtil.getUniqueId());
        }else if (mode == 1){
            userBean.setUpdateDate(DateFormatUtil.getNowDate());
            userBean.setIsUpload(Global.UploadStatus);
            mUserDao.update(userBean);
        }else if (mode == 3){
            mUserDao.insert(userBean);
        }else if (mode == 4){
            mUserDao.insert(userBean);
            Global.USER_MODE = true;
            SPHelper.saveUser(userBean);
        }
    }
    /**
     * 添加新用户
     *
     * @param id
     * @return
     */
    public UserBean initUser(Long id) {
        if (mUserDao.queryBuilder().where(UserBeanDao.Properties.Id.eq(id)).list().size() > 0){
            return mUserDao.queryBuilder().where(UserBeanDao.Properties.Id.eq(id)).unique();
        }
        UserBean userBean = new UserBean();
        userBean.setUserId(SnowflakeIdUtil.getUniqueId());
        userBean.setId(id);
        userBean.setCaseHistoryNo("123456");
        userBean.setCreateDate(DateFormatUtil.getNowDate());
        userBean.setSex(0);
        userBean.setName("出厂检验");
        userBean.setAge(50);
        userBean.setDiagnosis("全髋关节置换");
        userBean.setWeight("70");
        userBean.setIsUpload(-1);
        userBean.setIsFirstRun(-1);
        userBean.setKeyId(SnowflakeIdUtil.getUniqueId());
        userBean.setDate(DateFormatUtil.getNowDate());
        mUserDao.insert(userBean);
        return userBean;
    }
    public void insertSyncUser(UserBean userBean){
        userBean.setIsFirstRun(0);
        mUserDao.insert(userBean);
    }
    /**
     * 修改用户信息
     *
     * @param
     * @return
     */
    public void update(UserBean userBean) {
//        userBean.setPingYin(MyFunc.toPinyin(userBean.getName()));
        userBean.setUpdateDate(DateFormatUtil.getNowDate());
        mUserDao.update(userBean);
    }
    /**
     * 修改用户上传状态
     *
     * @param
     * @return
     */
    public void updateUserUploadStatus(List<UserBean> userBeanList,int uploadStatus) {
        for (UserBean userBean : userBeanList){
            userBean.setIsUpload(uploadStatus);
            userBean.setUpdateDate(DateFormatUtil.getNowDate());
            mUserDao.update(userBean);
        }

    }
    /**
     * 根据姓名模糊查询用户列表
     *
     * @param name
     * @return
     */
    public List<UserBean> loadByNameLike(String name) {
        return mUserDao.queryBuilder().where(UserBeanDao.Properties.Name.like("%" + name + "%")).orderDesc(UserBeanDao.Properties.CreateDate).list();
    }
    /**
     *
     * 根据ID删除用户
     *
     * @param id
     */
    public void deleteById(long id) {
        UserBean user = mUserDao.queryBuilder().where(UserBeanDao.Properties.UserId.eq(id)).unique();
        mUserDao.delete(user);
        TrainPlanManager.getInstance().clearTrainPlanDatabaseByUserId(id);
        UserTrainRecordManager.getInstance().clearById(id);
    }
    /**
     * 分页加载数据
     *
     * @param page
     * @return
     */
    public List<UserBean> load(int page) {
        return mUserDao.queryBuilder().offset(page * limit).limit(limit).list();
    }
    public boolean isUniqueUserId(long userId){
        return mUserDao.queryBuilder().where(UserBeanDao.Properties.UserId.eq(userId)).unique() == null;
    }
    public boolean isUniqueValue(String value, int mode){
        List<UserBean> userBeans = mUserDao.queryBuilder().where(UserBeanDao.Properties.CaseHistoryNo.eq(value)).list();
        if (mode == 0 && userBeans.size() <= 0){
            return true;
        }
        return mode != 0 && userBeans.size() <= 1;
    }
}
