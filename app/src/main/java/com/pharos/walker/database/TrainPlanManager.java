package com.pharos.walker.database;

import android.text.TextUtils;
import android.util.Log;

import com.pharos.walker.beans.PlanEntity;
import com.pharos.walker.beans.SubPlanEntity;
import com.pharos.walker.beans.TrainMessageBean;
import com.pharos.walker.constants.Global;
import com.pharos.walker.greendao.PlanEntityDao;
import com.pharos.walker.utils.DateFormatUtil;
import com.pharos.walker.utils.GreenDaoHelper;
import com.pharos.walker.utils.SPHelper;
import com.pharos.walker.utils.SnowflakeIdUtil;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhanglun on 2021/4/29
 * Describe:
 */
public class TrainPlanManager {
    private static volatile TrainPlanManager instance = null;

    private PlanEntityDao planEntityDao;

    private TrainPlanManager() {
        planEntityDao = GreenDaoHelper.getDaoSession().getPlanEntityDao();
    }

    public static TrainPlanManager getInstance() {
        if (instance == null) {
            synchronized (TrainPlanManager.class) {
                if (instance == null) {
                    instance = new TrainPlanManager();
                }
            }
        }
        return instance;
    }
    public List<PlanEntity> getPlanListByUserId(long userId){
        return planEntityDao.queryBuilder().where(PlanEntityDao.Properties.UserId.eq(userId)).list();
    }
    public List<PlanEntity> getMasterPlanListByUserId(long userId){
//        planEntityDao.detachAll();
        List<PlanEntity> tempPlanEntityList = planEntityDao.queryBuilder().where(PlanEntityDao.Properties.UserId.eq(userId)).list();
        List<PlanEntity> resultPlanEntityList = new ArrayList<>();
        for (int i = 0; i < tempPlanEntityList.size(); i++){//转换成服务端需要的entity
            if (i == 0){//添加子训练计划
                PlanEntity entity =  tempPlanEntityList.get(0);
                entity.setSubPlanEntityList(SubPlanManager.getInstance().loadDataByUserId(userId));
                resultPlanEntityList.add(entity);
            }else {
                resultPlanEntityList.add(tempPlanEntityList.get(i));
            }
        }
        return resultPlanEntityList;
    }
    public boolean isPlanEmpty(long userId){
        List<PlanEntity> planEntityList = getPlanListByUserId(userId);
        return planEntityList.size() <= 0;
    }
    public long getPlanUpdateDate(long userId){
        List<PlanEntity> list = getPlanListByUserId(userId);
        long lastDate = 0;
        for (PlanEntity planEntity:list) {
            if (!TextUtils.isEmpty(planEntity.getUpdateDate())){
                long currentDate = DateFormatUtil.getString2Date(planEntity.getUpdateDate());
                if (currentDate > lastDate){
                    lastDate = currentDate;
                }
            }

        }
        return lastDate;
    }
    public int comparePlanUpdateDate(long update,long userId){
        long current = getPlanUpdateDate(userId);
        if (update > current){
            return Global.UploadStatus;
        }else if (update == current){
            return Global.UploadNetStatus;
        }else{
            return Global.UploadLocalStatus;
        }
    }
    public void clearTrainPlanDatabaseByUserId(long userId){
        planEntityDao.deleteInTx(planEntityDao.queryBuilder().where(PlanEntityDao.Properties.UserId.eq(userId)).list());
        SubPlanManager.getInstance().clearPlanByUserId(userId);
    }
    public void clearAllTrainPlan(){
        planEntityDao.deleteAll();
        SubPlanManager.getInstance().clearAllSubTrainPlan();
    }
    public TrainMessageBean refreshPlanStatus(long userId){
        List<PlanEntity> planEntityList = getPlanListByUserId(userId);
        int trainTime = 5;
        int countOfTime = trainTime * Global.TrainCountMinute;
        int timesOfDay = 3;
        TrainMessageBean trainMessageBean = null;
        for (PlanEntity planEntity : planEntityList){
            if (System.currentTimeMillis() >= DateFormatUtil.getString2Date(planEntity.getStartDate()) &&
                    System.currentTimeMillis() < DateFormatUtil.getString2Date(planEntity.getEndDate())){
                planEntity.setPlanStatus(1);
                trainTime = planEntity.getTrainTime();
                countOfTime = planEntity.getCountOfTime();
                timesOfDay = planEntity.getTimeOfDay();
                trainMessageBean = new TrainMessageBean(trainTime,countOfTime,timesOfDay);
            }else if (System.currentTimeMillis() >= DateFormatUtil.getString2Date(planEntity.getEndDate())){
                planEntity.setPlanStatus(2);
            }else if (planEntity.getPlanStatus() == 1){
                planEntity.setPlanStatus(1);
                trainTime = planEntity.getTrainTime();
                countOfTime = planEntity.getCountOfTime();
                timesOfDay = planEntity.getTimeOfDay();
                trainMessageBean = new TrainMessageBean(trainTime,countOfTime,timesOfDay);
            }else {
                planEntity.setPlanStatus(0);
            }
            update(planEntity);
        }
        return trainMessageBean;
    }
    public long getCurrentPlanId(long userId){
        List<PlanEntity> planEntityList = getPlanListByUserId(userId);
        for (PlanEntity planEntity : planEntityList){
            if (System.currentTimeMillis() >= DateFormatUtil.getString2Date(planEntity.getStartDate()) &&
                    System.currentTimeMillis() < DateFormatUtil.getString2Date(planEntity.getEndDate())){
                return planEntity.getPlanId();
            }
        }
        return 0;
    }
    public int getCurrentClassId(long userId){
        List<PlanEntity> planEntityList = getPlanListByUserId(userId);
        for (PlanEntity planEntity : planEntityList){
            if (System.currentTimeMillis() >= DateFormatUtil.getString2Date(planEntity.getStartDate()) &&
                    System.currentTimeMillis() < DateFormatUtil.getString2Date(planEntity.getEndDate())){
                return planEntity.getClassId();
            }
        }
        return 0;
    }
    public int getInitLoad(long userId){
        List<PlanEntity> planEntityList = getPlanListByUserId(userId);
        if (planEntityList != null && planEntityList.size() > 0){
            return planEntityList.get(0).getLoad();
        }
        return 0;
    }
    public void insertMany(List<PlanEntity> planEntities){
        planEntityDao.insertOrReplaceInTx(planEntities);
    }
    public void insert(PlanEntity entity){
        planEntityDao.insertOrReplace(entity);
    }
    public boolean isLoadLocalUpdate(long planId,long userId,int netLoad){
        PlanEntity entity = planEntityDao.queryBuilder().where(PlanEntityDao.Properties.UserId.eq(userId),PlanEntityDao.Properties.PlanId.eq(planId)).unique();
        int currentLoad = 0;
        if (entity != null){
            currentLoad = entity.getLoad();
        }
        return currentLoad != netLoad;
    }
    public void update(PlanEntity planEntity){
        planEntityDao.update(planEntity);
    }
    private void insert(int loadWeight,int classId,String startDate,String endDate,int trainTime,int planTotalDay){
        PlanEntity planEntity = planEntityDao.queryBuilder().where(PlanEntityDao.Properties.UserId.eq(SPHelper.getUserId()),PlanEntityDao.Properties.ClassId.eq(classId)).unique();
        if (planEntity == null){
            planEntity = new PlanEntity();
            planEntity.setCreateDate(DateFormatUtil.getNowDate());
        }
        planEntity.setStartDate(startDate);
        planEntity.setWeight(SPHelper.getUser().getWeight());
        planEntity.setUserId(SPHelper.getUserId());
        planEntity.setUpdateDate(DateFormatUtil.getNowDate());
        planEntity.setClassId(classId);
        planEntity.setLoad(loadWeight);
        planEntity.setEndDate(endDate);
        planEntity.setTimeOfDay(3);//每天训练次数
        planEntity.setCountOfTime(trainTime * Global.TrainCountMinute);//每次训练几步
        planEntity.setPlanStatus(0);//计划状态 0未开始，1进行中，2完成
        planEntity.setPlanId(SnowflakeIdUtil.getUniqueId());
        planEntity.setTrainType(1);//训练方式 0按步数，1按时间
        planEntity.setPlanTotalDay(planTotalDay);//总训练周期（天）
        planEntity.setTrainTime(trainTime);//训练时间(分钟)
        planEntity.setKeyId(SnowflakeIdUtil.getUniqueId());
        planEntityDao.insertOrReplace(planEntity);
    }
    public void insertList1(int loadWeight){//全髋关节置换模板
//        long planId = SnowflakeIdUtil.getUniqueId();
        String startDate = SPHelper.getUser().getDate();
        SubPlanManager.getInstance().insert(startDate,loadWeight,1,6);
        String endDate = DateFormatUtil.increaseOneDayOneSecondLess(4*7,startDate);
        insert(loadWeight,1,startDate,endDate,8,6*7);
        String classTwoStartDate = DateFormatUtil.getString2DateIncreaseOneDay(endDate,null);
        int newLoadWeight = (int)(((Float.parseFloat(SPHelper.getUser().getWeight()) - loadWeight)/6) * 4 + loadWeight);//体重-评估负重 6周平均分配
        insert(newLoadWeight,2,classTwoStartDate,DateFormatUtil.increaseOneDayOneSecondLess(2*7,classTwoStartDate),25,6*7);

    }
    public void insertList2(int loadWeight){//全膝关节置换模板
        String startDate = SPHelper.getUser().getDate();
        String endDate = DateFormatUtil.increaseOneDayOneSecondLess(6*7,startDate);
        insert(loadWeight,1,startDate,endDate,Global.TrainTime,6*7);
        SubPlanManager.getInstance().insert(startDate,loadWeight,1,6);
    }
    public void insertList3(int loadWeight){//股骨近端骨折 转子间骨折
        String startDate = SPHelper.getUser().getDate();
        int weight = Integer.parseInt(SPHelper.getUser().getWeight());
        int newLoadWeight1 = (int) (weight * 0.87);
        int newLoadWeight2 = (int) (weight * 0.51);
        float weekDiff = (float)(weight * (0.87-0.51))/11;
        int weekCount = (int) Math.ceil((weight * (1-0.87)/weekDiff));//推算剩余的负重对应结束的时间
        if (weekCount == 0){
            weekCount = 1;
        }

        if(loadWeight > newLoadWeight1){
            insert(loadWeight, 3, startDate, DateFormatUtil.increaseOneDayOneSecondLess(weekCount * 7, startDate), Global.TrainTime, weekCount);
            SubPlanManager.getInstance().insert3(startDate, weight, loadWeight, 3, weekCount);
        } else if(loadWeight > newLoadWeight2){
            int weekNumer = (int) Math.ceil((weight * 0.87 - loadWeight)/weekDiff);//推算剩余的负重对应结束的时间
            if (weekNumer == 0){
                weekNumer = 1;
            }
            insert(loadWeight, 2, startDate, DateFormatUtil.increaseOneDayOneSecondLess(weekNumer * 7, startDate), Global.TrainTime, weekNumer);
            SubPlanManager.getInstance().insert3(startDate, newLoadWeight1, loadWeight, 2, weekNumer);
            String startDate1 = DateFormatUtil.getBeforeOrAfterDate(11 * 7, startDate);
            insert(newLoadWeight1, 3, startDate1, DateFormatUtil.increaseOneDayOneSecondLess(weekCount * 7, startDate1), Global.TrainTime, (12 + weekCount));
            SubPlanManager.getInstance().insert3(startDate1, weight, newLoadWeight1, 3, weekCount);
        } else {
            String endDate = DateFormatUtil.increaseOneDayOneSecondLess(7, startDate);
            insert(loadWeight, 1, startDate, endDate, Global.TrainTime, (12 + weekCount) * 7);
            SubPlanManager.getInstance().insert3(startDate, newLoadWeight2, loadWeight, 1, 7);
            String classTwoStartDate = DateFormatUtil.getString2DateIncreaseOneDay(endDate,null);
            insert(newLoadWeight2, 2, classTwoStartDate, DateFormatUtil.increaseOneDayOneSecondLess(11 * 7, classTwoStartDate), Global.TrainTime, (12 + weekCount));
            SubPlanManager.getInstance().insert3(classTwoStartDate, newLoadWeight1, newLoadWeight2, 2, 11);
            String startDate1 = DateFormatUtil.increaseOneDayOneSecondLess(11 * 7, classTwoStartDate);
            String classThreeStartDate = DateFormatUtil.getString2DateIncreaseOneDay(startDate1,null);
            insert(newLoadWeight1, 3, classThreeStartDate, DateFormatUtil.increaseOneDayOneSecondLess(weekCount * 7, classThreeStartDate), Global.TrainTime, (12 + weekCount));
            SubPlanManager.getInstance().insert3(classThreeStartDate, weight, newLoadWeight1, 3, weekCount);
        }
    }
    public void insertList4(int loadWeight){//胫骨平台骨折（钢板固定）
        String startDate = SPHelper.getUser().getDate();
        if (loadWeight >= 20)
            loadWeight = 20;
        SubPlanManager.getInstance().insert2(startDate, loadWeight,1,6);
        String endDate = DateFormatUtil.increaseOneDayOneSecondLess(6*7,startDate);
        insert(loadWeight,1,startDate,endDate,Global.TrainTime,6*7);
        String classTwoStartDate = DateFormatUtil.getString2DateIncreaseOneDay(endDate,null);
        insert(loadWeight,2,classTwoStartDate,DateFormatUtil.increaseOneDayOneSecondLess(10*7,classTwoStartDate),Global.TrainTime,10*7);
        SubPlanManager.getInstance().insert4(classTwoStartDate,Integer.parseInt(SPHelper.getUser().getWeight()),loadWeight,2,10);
    }
    public void insertList5(int loadWeight){//胫骨平台骨折（钢板内固定）
        String startDate = SPHelper.getUser().getDate();
        insert(loadWeight,1,startDate,DateFormatUtil.increaseOneDayOneSecondLess(39*7,startDate),Global.TrainTime,39*7);
        SubPlanManager.getInstance().insert5(startDate,Integer.parseInt(SPHelper.getUser().getWeight()),loadWeight,1,40);
    }
    public void insertList6(int loadWeight){//胫骨中段骨折（石膏固定）
        String startDate = SPHelper.getUser().getDate();
        SubPlanManager.getInstance().insert2(startDate, loadWeight,1,3);
        String endDate = DateFormatUtil.increaseOneDayOneSecondLess(3*7,startDate);
        insert(loadWeight,1,startDate,endDate,Global.TrainTime,3*7);
        String classTwoStartDate = DateFormatUtil.getString2DateIncreaseOneDay(endDate,null);
        insert(loadWeight,2,classTwoStartDate,DateFormatUtil.increaseOneDayOneSecondLess(24*7,classTwoStartDate),Global.TrainTime,24*7);
        SubPlanManager.getInstance().insert5(classTwoStartDate,Integer.parseInt(SPHelper.getUser().getWeight()),loadWeight,2,24);
    }
    public void insertList7(int loadWeight){//胫骨中段骨折（髓内钉）（桥接钢板）
        String startDate = SPHelper.getUser().getDate();
        insert(loadWeight,1,startDate,DateFormatUtil.increaseOneDayOneSecondLess(24*7,startDate),Global.TrainTime,24*7);
        SubPlanManager.getInstance().insert5(startDate,Integer.parseInt(SPHelper.getUser().getWeight()),loadWeight,1,24);
    }
    public void insertList8(int loadWeight){//踝关节骨折（钢板内固定）
        String startDate = DateFormatUtil.getBeforeOrAfterDate(2,SPHelper.getUser().getDate());
        insert(loadWeight,1,startDate,DateFormatUtil.increaseOneDayOneSecondLess(16*7,startDate),Global.TrainTime,16*7);
        SubPlanManager.getInstance().insert4(startDate,Integer.parseInt(SPHelper.getUser().getWeight()),loadWeight,1,16);
    }
    public void insertList9(int loadWeight){//跟骨骨折（钢板固定）
        String startDate = DateFormatUtil.getBeforeOrAfterDate(4*7,SPHelper.getUser().getDate());
        if (loadWeight >= 10)
            loadWeight = 10;
        SubPlanManager.getInstance().insert4(startDate,10, loadWeight,1,2);
        String endDate = DateFormatUtil.increaseOneDayOneSecondLess(2*7,startDate);
        insert(loadWeight,1,startDate,endDate,Global.TrainTime,2*7);
        String classTwoStartDate = DateFormatUtil.getString2DateIncreaseOneDay(endDate,null);
        insert(10,2,classTwoStartDate,DateFormatUtil.increaseOneDayOneSecondLess(2*7,classTwoStartDate),Global.TrainTime,2*7);
        SubPlanManager.getInstance().insert4(classTwoStartDate,10,10,2,2);

        String startDate1 = DateFormatUtil.increaseOneDayOneSecondLess(2*7,classTwoStartDate);
        String classThreeStartDate = DateFormatUtil.getString2DateIncreaseOneDay(startDate1,null);
        String endDate1 = DateFormatUtil.increaseOneDayOneSecondLess(2*7,classThreeStartDate);
        insert(20,3,classThreeStartDate,endDate1,Global.TrainTime,2*7);
        SubPlanManager.getInstance().insert4(classThreeStartDate,20,20,3,2);
        String classFourStartDate = DateFormatUtil.getString2DateIncreaseOneDay(endDate1,null);
        int weight = Integer.parseInt(SPHelper.getUser().getWeight());
        int weekCount = 0;
        if (weight <= 40){
            weekCount = 1;
            insert(40,4,classFourStartDate,DateFormatUtil.increaseOneDayOneSecondLess(weekCount*7,classFourStartDate),Global.TrainTime,weekCount*7);
            SubPlanManager.getInstance().insert4(classFourStartDate,weight,weight,4,weekCount);
        }else {
            weekCount = (weight-40)/10 + 1;
            insert(40,4,classFourStartDate,DateFormatUtil.increaseOneDayOneSecondLess(weekCount*7,classFourStartDate),Global.TrainTime,weekCount*7);
            SubPlanManager.getInstance().insert4(classFourStartDate,weight,40,4,weekCount);
        }
    }
    public void insertList10(int loadWeight){//踝关节韧带损伤（踝关节韧带重建术）
        String startDate = SPHelper.getUser().getDate();
        if (loadWeight >= 5)
            loadWeight = 5;
        String endDate = DateFormatUtil.increaseOneDayOneSecondLess(4*7,startDate);
        insert(loadWeight,1,startDate,endDate,Global.TrainTime,4*7);
        SubPlanManager.getInstance().insert4(startDate,loadWeight,loadWeight,1,4);
        String classTwoStartDate = DateFormatUtil.getString2DateIncreaseOneDay(endDate,null);
        float diff = (Float.parseFloat(SPHelper.getUser().getWeight()) - loadWeight)/12f ;//体重-评估负重 16周平均分配
        insert(loadWeight,2,classTwoStartDate,DateFormatUtil.increaseOneDayOneSecondLess(12*7,classTwoStartDate),Global.TrainTime,12*7);
        SubPlanManager.getInstance().insert4(classTwoStartDate,Integer.parseInt(SPHelper.getUser().getWeight()),loadWeight,2,12);
    }
    public void insertList11(int loadWeight){//股骨头坏死（腓骨移植术）
        String startDate = SPHelper.getUser().getDate();
        if (loadWeight >= 12)
            loadWeight = 12;
        String endDate = DateFormatUtil.increaseOneDayOneSecondLess(7*7,startDate);
        insert(loadWeight,1,startDate,endDate,Global.TrainTime,7*7);
        SubPlanManager.getInstance().insert4(startDate,loadWeight,loadWeight,1,7);
        String classTwoStartDate = DateFormatUtil.getString2DateIncreaseOneDay(endDate,null);
        int weight = Integer.parseInt(SPHelper.getUser().getWeight());
        int diff = 5;
        int weekCount = ((weight - loadWeight)/diff)*2;
        insert(loadWeight,2,classTwoStartDate,DateFormatUtil.increaseOneDayOneSecondLess(weekCount*7,classTwoStartDate),Global.TrainTime,weekCount*7);
        SubPlanManager.getInstance().insert5(classTwoStartDate,weight,loadWeight,2,weekCount);
    }
}
