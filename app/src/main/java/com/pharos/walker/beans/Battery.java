package com.pharos.walker.beans;

/**
 * Created by zhanglun on 2021/4/19
 * Describe:
 */
public class Battery {
    private int batteryStatus;
    private int batteryVolume;

    public Battery(int batteryStatus, int batteryVolume) {
        this.batteryStatus = batteryStatus;
        this.batteryVolume = batteryVolume;
    }

    public int getBatteryStatus() {
        return batteryStatus;
    }

    public void setBatteryStatus(int batteryStatus) {
        this.batteryStatus = batteryStatus;
    }

    public int getBatteryVolume() {
        return batteryVolume;
    }

    public void setBatteryVolume(int batteryVolume) {
        this.batteryVolume = batteryVolume;
    }
}
