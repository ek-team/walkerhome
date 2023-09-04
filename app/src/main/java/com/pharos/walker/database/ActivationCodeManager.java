package com.pharos.walker.database;

import com.pharos.walker.beans.ActivationCodeBean;
import com.pharos.walker.greendao.ActivationCodeBeanDao;
import com.pharos.walker.utils.GreenDaoHelper;

public class ActivationCodeManager {
    private static volatile ActivationCodeManager instance = null;

    private ActivationCodeBeanDao activationCodeBeanDao;

    private ActivationCodeManager() {
        activationCodeBeanDao = GreenDaoHelper.getDaoSession().getActivationCodeBeanDao();
    }

    public static ActivationCodeManager getInstance() {
        if (instance == null) {
            synchronized (ActivationCodeManager.class) {
                if (instance == null) {
                    instance = new ActivationCodeManager();
                }
            }
        }
        return instance;
    }
    public ActivationCodeBean getCodeBean(){
        return activationCodeBeanDao.queryBuilder().where(ActivationCodeBeanDao.Properties.Id.eq(0)).unique();
    }
    public void updateCodeBean(ActivationCodeBean bean){
        activationCodeBeanDao.update(bean);
    }
    public void insertCodeBean(ActivationCodeBean bean){
        activationCodeBeanDao.insertOrReplace(bean);
    }
}
