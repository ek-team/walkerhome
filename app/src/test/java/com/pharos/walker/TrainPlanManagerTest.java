package com.pharos.walker;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * Created by zhanglun on 2021/5/31
 * Describe:
 */

public class TrainPlanManagerTest {
    private int load = 20;
    private int weight = 50;
    private int weekCount = 6;

    @Before
    public void setUp() throws Exception {
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void insertList4() {
        if (load >= 20)
            load = 20;
        subInsert4(0,6,1);
        float diff = (weight - load)/9f;
        subInsert4(diff,10,2);


    }
    private void subInsert4(float diff, int weekCount,int classId){
        for (int i = 0; i < weekCount; i++){
            System.out.println("第" + classId + "阶段：" + (int) (load + diff * i));
        }
    }
}