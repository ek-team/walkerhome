package com.pharos.walker.database;

import com.pharos.walker.beans.OtherFeedbackBean;
import com.pharos.walker.greendao.OtherFeedbackBeanDao;
import com.pharos.walker.utils.GreenDaoHelper;

import java.util.List;

public class OtherFeedbackManager {
    private static volatile OtherFeedbackManager instance = null;

    private OtherFeedbackBeanDao otherFeedbackDao;

    private OtherFeedbackManager() {
        otherFeedbackDao = GreenDaoHelper.getDaoSession().getOtherFeedbackBeanDao();
    }

    public static OtherFeedbackManager getInstance() {
        if (instance == null) {
            synchronized (OtherFeedbackManager.class) {
                if (instance == null) {
                    instance = new OtherFeedbackManager();
                }
            }
        }
        return instance;
    }
    public void insert(OtherFeedbackBean bean){
        otherFeedbackDao.insertOrReplace(bean);
    }
    public List<OtherFeedbackBean> loadAll(){
        return otherFeedbackDao.loadAll();
    }
}
