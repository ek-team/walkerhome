package com.pharos.walker.database;

import com.pharos.walker.beans.TrainDataEntity;
import com.pharos.walker.constants.AppKeyManager;
import com.pharos.walker.greendao.TrainDataEntityDao;
import com.pharos.walker.utils.DateFormatUtil;
import com.pharos.walker.utils.GreenDaoHelper;
import com.pharos.walker.utils.SPHelper;

import java.util.List;

/**
 * Created by zhanglun on 2021/5/20
 * Describe:
 */
public class TrainDataManager {
    private static volatile TrainDataManager instance = null;

    private TrainDataEntityDao trainDataDao;

    private TrainDataManager() {
        trainDataDao = GreenDaoHelper.getDaoSession().getTrainDataEntityDao();
    }

    public static TrainDataManager getInstance() {
        if (instance == null) {
            synchronized (TrainDataManager.class) {
                if (instance == null) {
                    instance = new TrainDataManager();
                }
            }
        }
        return instance;
    }
    public void insert(TrainDataEntity trainDataEntity){
        long userId = SPHelper.getUserId();
        long date = System.currentTimeMillis();
        trainDataEntity.setCreateDate(date);
        trainDataEntity.setDateStr(DateFormatUtil.getDate2String(date, AppKeyManager.DATE_YMD));
        trainDataEntity.setPlanId(TrainPlanManager.getInstance().getCurrentPlanId(userId));
        trainDataEntity.setClassId(TrainPlanManager.getInstance().getCurrentClassId(userId));
        trainDataEntity.setIsUpload(0);
        trainDataEntity.setUserId(userId);
        trainDataEntity.setFrequency(UserTrainRecordManager.getInstance().getLastTimeFrequency(userId));
        trainDataDao.insert(trainDataEntity);
    }
    public void insertMany(List<TrainDataEntity> trainDataEntities){
        if (trainDataEntities == null)
            return;
        trainDataDao.insertOrReplaceInTx(trainDataEntities);

    }
    public void update(TrainDataEntity trainData){
        trainDataDao.update(trainData);

    }

    public List<TrainDataEntity> getTrainDataByDate(long userId,String date){
        return trainDataDao.queryBuilder().where(TrainDataEntityDao.Properties.UserId.eq(userId),TrainDataEntityDao.Properties.DateStr.eq(date)).list();
    }
    public List<TrainDataEntity> getTrainDataByUserId(long userId){
//        trainDataDao.detachAll();
        return trainDataDao.queryBuilder().where(TrainDataEntityDao.Properties.UserId.eq(userId)).list();
    }
    public List<TrainDataEntity> getTrainDataByDateAndFrequency(long userId,String date,int frequency){
//        trainDataDao.detachAll();
        return trainDataDao.queryBuilder().where(TrainDataEntityDao.Properties.UserId.eq(userId),TrainDataEntityDao.Properties.DateStr.eq(date),
                TrainDataEntityDao.Properties.Frequency.eq(frequency)).orderAsc(TrainDataEntityDao.Properties.CreateDate).list();
    }
    public void clearByUserId(long userId){
        trainDataDao.deleteInTx(getTrainDataByUserId(userId));
    }
}
