package com.pharos.walker.database;

import android.text.TextUtils;

import com.pharos.walker.beans.ChartRecordBean;
import com.pharos.walker.beans.TrainDataEntity;
import com.pharos.walker.beans.UserBean;
import com.pharos.walker.beans.UserTrainRecordEntity;
import com.pharos.walker.constants.AppKeyManager;
import com.pharos.walker.constants.Global;
import com.pharos.walker.greendao.DaoSession;
import com.pharos.walker.greendao.UserTrainRecordEntityDao;
import com.pharos.walker.utils.DateFormatUtil;
import com.pharos.walker.utils.GreenDaoHelper;
import com.pharos.walker.utils.SPHelper;
import com.pharos.walker.utils.SnowflakeIdUtil;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;

/**
 * Created by zhanglun on 2021/5/11
 * Describe:
 */
public class UserTrainRecordManager {
    private static volatile UserTrainRecordManager instance = null;
    private UserTrainRecordEntityDao userTrainRecordDao;
    private UserTrainRecordManager() {
        userTrainRecordDao = GreenDaoHelper.getDaoSession().getUserTrainRecordEntityDao();
    }

    public static UserTrainRecordManager getInstance() {
        if (instance == null) {
            synchronized (UserTrainRecordManager.class) {
                if (instance == null) {
                    instance = new UserTrainRecordManager();
                }
            }
        }else {
            DaoSession daoSession = GreenDaoHelper.getDaoSession();
            daoSession.clear();
        }
        return instance;
    }
    public UserTrainRecordEntity insert(UserTrainRecordEntity userTrainRecord){
        long currentTime = System.currentTimeMillis();
        long userId = SPHelper.getUserId();
        userTrainRecord.setIsUpload(Global.UploadStatus);
        userTrainRecord.setCreateDate(currentTime);
        userTrainRecord.setDateStr(DateFormatUtil.getDate2String(currentTime,AppKeyManager.DATE_YMD));
        userTrainRecord.setPlanId(TrainPlanManager.getInstance().getCurrentPlanId(userId));
        userTrainRecord.setClassId(TrainPlanManager.getInstance().getCurrentClassId(userId));
        userTrainRecord.setFrequency(getLastTimeFrequency(userId) + 1);
        userTrainRecord.setKeyId(SnowflakeIdUtil.getUniqueId());
        userTrainRecordDao.insert(userTrainRecord);
        return userTrainRecord;
//        UserBean userBean = UserManager.getInstance().loadByUserId(userId);
//        userBean.setIsRecordUpdate(0);
//        UserManager.getInstance().update(userBean);
    }
    public void update(UserTrainRecordEntity userTrainRecord){
        userTrainRecordDao.detachAll();
        userTrainRecordDao.update(userTrainRecord);
    }
    public int getTrainDay(long userId){
        List<UserTrainRecordEntity> userTrainRecordEntities = loadAll(userId);
        LinkedHashSet<String> linkedHashSet = new LinkedHashSet<>();
        for (UserTrainRecordEntity entity : userTrainRecordEntities){
            linkedHashSet.add(entity.getDateStr());
        }
        return linkedHashSet.size();
    }
    public void insertServerData(UserTrainRecordEntity userTrainRecord){
        userTrainRecordDao.insertOrReplace(userTrainRecord);
    }
    public void insertMany(List<UserTrainRecordEntity> userTrainRecordEntityList){
        userTrainRecordDao.insertOrReplaceInTx(userTrainRecordEntityList);
    }
    public void insertSingle(UserTrainRecordEntity userTrainRecord){
        userTrainRecord.setIsUpload(Global.UploadLocalStatus);
        userTrainRecordDao.insertOrReplace(userTrainRecord);
    }
    public List<UserTrainRecordEntity> loadAll(long userId){
//        DaoSession daoSession = GreenDaoHelper.getDaoSession();
//        daoSession.clear();
//        UserTrainRecordEntityDao userTrainRecordDao1 = GreenDaoHelper.getDaoSession().getUserTrainRecordEntityDao();
//        userTrainRecordDao.detachAll();//清除数据库缓存，解决数据获取不及时问题
//        String strSql="SELECT * FROM USER_TRAIN_RECORD";
//        List<UserTrainRecordEntity> list = userTrainRecordDao.queryRaw(strSql,null);
        List<UserTrainRecordEntity> list = userTrainRecordDao.queryBuilder().where(UserTrainRecordEntityDao.Properties.UserId.eq(userId)).orderAsc(UserTrainRecordEntityDao.Properties.CreateDate).list();
        return list;
    }
    public void clearById(long userId){
        userTrainRecordDao.deleteInTx(loadAll(userId));
        TrainDataManager.getInstance().clearByUserId(userId);
    }
    public List<UserTrainRecordEntity> loadAllUpdate(long userId){
        List<UserTrainRecordEntity> trainRecordEntities = loadAll(userId);
        List<UserTrainRecordEntity> newList = new ArrayList<>();
        for (UserTrainRecordEntity trainRecordEntity : trainRecordEntities){
            if (trainRecordEntity.getIsUpload() == Global.UploadStatus){
                newList.add(trainRecordEntity);
            }
        }

        return newList;
    }
    public List<UserTrainRecordEntity> loadMasterUpdate(long userId){
        List<UserTrainRecordEntity> trainRecordEntities = loadAll(userId);
        List<UserTrainRecordEntity> newList = new ArrayList<>();
        for (UserTrainRecordEntity trainRecordEntity : trainRecordEntities){
            if (trainRecordEntity.getIsUpload() != Global.UploadNetStatus){
                trainRecordEntity.setIsUpload(Global.UploadNetStatus);
                trainRecordEntity.setTrainDataList(TrainDataManager.getInstance().getTrainDataByDateAndFrequency(trainRecordEntity.getUserId(),trainRecordEntity.getDateStr(),trainRecordEntity.getFrequency()-1));
                newList.add(trainRecordEntity);
            }
        }

        return newList;
    }
    public void updateTrainUploadStatus(List<UserTrainRecordEntity> recordEntityList){
        for (UserTrainRecordEntity entity : recordEntityList){
            entity.setIsUpload(Global.UploadLocalStatus);
            update(entity);
        }
    }
    public void updateMasterTrainUploadStatus(List<UserTrainRecordEntity> recordEntityList){
        if (recordEntityList ==  null)
            return;
        for (UserTrainRecordEntity entity : recordEntityList){
            entity.setIsUpload(Global.UploadNetStatus);
            update(entity);
            if (entity.getTrainDataList() == null)
                break;
            for (TrainDataEntity trainDataEntity : entity.getTrainDataList()){
                trainDataEntity.setIsUpload( Global.UploadNetStatus);
                TrainDataManager.getInstance().update(trainDataEntity);
            }
        }
    }
    public void updateMasterTrainUploadStatus(UserTrainRecordEntity entity){
        entity.setIsUpload(Global.UploadNetStatus);
        update(entity);
        for (TrainDataEntity trainDataEntity : entity.getTrainDataList()){
            trainDataEntity.setIsUpload( Global.UploadNetStatus);
            TrainDataManager.getInstance().update(trainDataEntity);
        }
    }
    public int getLastTimeFrequency(long userId){
        long currentDate = System.currentTimeMillis();
        int frequency = 0;
        List<UserTrainRecordEntity> userTrainRecordEntities = userTrainRecordDao.queryBuilder().where(UserTrainRecordEntityDao.Properties.UserId.eq(userId),
                UserTrainRecordEntityDao.Properties.DateStr.eq(DateFormatUtil.getDate2String(currentDate, AppKeyManager.DATE_YMD))).list();
        for (UserTrainRecordEntity recordEntity: userTrainRecordEntities){
            if (frequency <= recordEntity.getFrequency()){
                frequency = recordEntity.getFrequency();
            }
        }
        return frequency;
    }
    public List<UserTrainRecordEntity> loadByDate(long userId,long dateTemp){
        return userTrainRecordDao.queryBuilder().where(UserTrainRecordEntityDao.Properties.UserId.eq(userId), UserTrainRecordEntityDao.Properties.DateStr.eq(DateFormatUtil.getDate2String(dateTemp, AppKeyManager.DATE_YMD))).list();
    }
    public List<UserTrainRecordEntity> loadByDate(long userId,String dateStr){
        return userTrainRecordDao.queryBuilder().where(UserTrainRecordEntityDao.Properties.UserId.eq(userId), UserTrainRecordEntityDao.Properties.DateStr.eq(dateStr)).list();
    }
    public List<ChartRecordBean> getChartData(long userId){
        if (!Global.USER_MODE)
            return null;
        List<UserTrainRecordEntity> userTrainRecordEntities = loadAll(userId);
        LinkedHashSet<String> linkedHashSet = new LinkedHashSet<>();
        for (UserTrainRecordEntity entity : userTrainRecordEntities){
            linkedHashSet.add(entity.getDateStr());
        }
        List<ChartRecordBean> chartRecordBeanList = new ArrayList<>();
        for (String dateStr: linkedHashSet){//遍历当前用户记录的日期
            ChartRecordBean chartRecordBean = new ChartRecordBean();
            chartRecordBean.setDate(dateStr);
            List<UserTrainRecordEntity> entityListByDate = loadByDate(userId,dateStr);//获取某个日期下的训练记录
            int painLevelTotal = 0;
            int targetWeightTotal = 0;
            int errorFeedbackTotal = 0;
            int classId = 0;
            List<ChartRecordBean.NumOfTimeBean> numOfTimeBeanList = new ArrayList<>();
            for (UserTrainRecordEntity entity : entityListByDate){//遍历训练记录
                painLevelTotal = painLevelTotal + entity.getPainLevel();
                targetWeightTotal = targetWeightTotal + entity.getTargetLoad();
                List<TrainDataEntity> trainDataEntities = TrainDataManager.getInstance().getTrainDataByDateAndFrequency(userId,dateStr,entity.getFrequency()-1);
                ChartRecordBean.NumOfTimeBean numOfTimeBean = new ChartRecordBean.NumOfTimeBean();
                int realLoad = 0;
                for (TrainDataEntity trainDataEntity : trainDataEntities){//遍历每踩的实际负重相加
                    realLoad = realLoad + trainDataEntity.getRealLoad();
                }
                if (trainDataEntities.size() != 0){
                    numOfTimeBean.setAverageWeight(realLoad/trainDataEntities.size());//保存每次的平均实际负重
                }
                numOfTimeBean.setDateSte(dateStr);
                numOfTimeBean.setFrequency(entity.getFrequency());
                numOfTimeBean.setTargetWeight(entity.getTargetLoad());
                if (!entity.getAdverseReactions().equals("无") && !TextUtils.isEmpty(entity.getAdverseReactions())){
                    errorFeedbackTotal++;
                    numOfTimeBean.setPainError(true);
                }else {
                    numOfTimeBean.setPainError(false);
                }
                numOfTimeBeanList.add(numOfTimeBean);
                classId = entity.getClassId();
            }
            chartRecordBean.setPainFeedback(errorFeedbackTotal);
            chartRecordBean.setClassId(classId);
            if (entityListByDate.size() != 0){
                chartRecordBean.setPainLevel(painLevelTotal/entityListByDate.size());//获取平均疼痛等级
                chartRecordBean.setTargetWeight(targetWeightTotal/entityListByDate.size());//获取平均目标负重
            }
            int realLoadTotal = 0;
            for (ChartRecordBean.NumOfTimeBean numOfTimeBean : numOfTimeBeanList){
                realLoadTotal = realLoadTotal + numOfTimeBean.getAverageWeight();
            }
            if (numOfTimeBeanList.size() != 0){
                chartRecordBean.setAverageWeight(realLoadTotal/numOfTimeBeanList.size());
                chartRecordBean.setNumOfTimeBeanList(numOfTimeBeanList);
            }

            chartRecordBeanList.add(chartRecordBean);
        }
        return chartRecordBeanList;
    }
}
