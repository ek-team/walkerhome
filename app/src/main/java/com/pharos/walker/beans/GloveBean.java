package com.pharos.walker.beans;


import androidx.annotation.IntDef;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Created by zhanglun on 2020/6/3
 * Describe:
 */
@Entity(nameInDb = "GLOVE")
public class GloveBean {
    public static final String COMMA = ",";

    @IntDef({Type.LEFT_HAND, Type.RIGHT_HAND})
    @Retention(RetentionPolicy.SOURCE)
    public @interface Type {
        int LEFT_HAND = 1;
        int RIGHT_HAND = 2;
    }

    @IntDef({
            Size.SS,
            Size.S,
            Size.M,
            Size.L,
            Size.XL
    })
    @Retention(RetentionPolicy.SOURCE)
    public @interface Size {
        int SS = 1;
        int S = 2;
        int M = 3;
        int L = 4;
        int XL = 5;
    }

    @Id(autoincrement = true)
    private Long id;                    //ID
    private Long userId;
    private String name;                //姓名
    private int type;                   // 1-左手版本  2-右手版本
    private int size;                   // 1-SS  2-S  3-M  4-L  5-X
    private String lows;                // 伸直灵敏度（0,0,0,0,0 用,号分割）
    private String ups;                 // 握拳灵敏度（0,0,0,0,0 用,号分割）
    private String initializePosition;  // 初始位置（0,0,0,0,0 用,号分割）
    private String customAngle;         // 自定义角度（0,0,0,0,0 用,号分割）
    private String motorCurrent;        // 舵机电流（0,0,0,0,0 用,号分割）
    private String createDate;          // 创建时间
    private String updateDate;          // 更新时间
    @Generated(hash = 756482282)
    public GloveBean(Long id, Long userId, String name, int type, int size,
                     String lows, String ups, String initializePosition, String customAngle,
                     String motorCurrent, String createDate, String updateDate) {
        this.id = id;
        this.userId = userId;
        this.name = name;
        this.type = type;
        this.size = size;
        this.lows = lows;
        this.ups = ups;
        this.initializePosition = initializePosition;
        this.customAngle = customAngle;
        this.motorCurrent = motorCurrent;
        this.createDate = createDate;
        this.updateDate = updateDate;
    }

    @Generated(hash = 1712574238)
    public GloveBean() {
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserId() {
        return this.userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getType() {
        return this.type;
    }

    public void setType(@Type int type) {
        this.type = type;
    }

    public int getSize() {
        return this.size;
    }

    public void setSize(@Size int size) {
        this.size = size;
    }

    public String getLows() {
        return this.lows;
    }

    public void setLows(String lows) {
        this.lows = lows;
    }

    public String getUps() {
        return this.ups;
    }

    public void setUps(String ups) {
        this.ups = ups;
    }

    public String getInitializePosition() {
        return this.initializePosition;
    }

    public void setInitializePosition(String initializePosition) {
        this.initializePosition = initializePosition;
    }

    public String getCustomAngle() {
        return this.customAngle;
    }

    public void setCustomAngle(String customAngle) {
        this.customAngle = customAngle;
    }

    public String getMotorCurrent() {
        return this.motorCurrent;
    }

    public void setMotorCurrent(String motorCurrent) {
        this.motorCurrent = motorCurrent;
    }

    public String getCreateDate() {
        return this.createDate;
    }

    public void setCreateDate(String createDate) {
        this.createDate = createDate;
    }

    public String getUpdateDate() {
        return this.updateDate;
    }

    public void setUpdateDate(String updateDate) {
        this.updateDate = updateDate;
    }
}
