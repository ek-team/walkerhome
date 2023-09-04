package com.pharos.walker.beans;

import android.bluetooth.BluetoothGatt;

import java.util.Arrays;

/**
 * Created by zhanglun on 2021/4/9
 * Describe:
 */
public class BleBean {
    private BluetoothGatt gatt;
    private byte[] bytes;
    private int type;  // 1-左手  2-右手
    private int connectState;  //   -1-未连接  0-连接中  1-已连接
    private String name;
    private String address;
    private int rssi;
    private String data;

    public BleBean(String address, int rssi, String data) {
        this.address = address;
        this.rssi = rssi;
        this.data = data;
    }

    public BleBean(BluetoothGatt gatt, String name) {
        this.gatt = gatt;
        this.name = name;
        this.type = 1;
    }

    public BleBean(String address, byte[] bytes) {
        this.address = address;
        this.bytes = bytes;
    }

    public BleBean(int type, String name, String address) {
        this.type = type;
        this.name = name;
        this.address = address;
    }

    public BleBean(String name, String address, int connectState) {
        this.name = name;
        this.address = address;
        this.connectState = connectState;
    }
    public BleBean(String name, String address, int connectState, int rssi) {
        this.name = name;
        this.address = address;
        this.connectState = connectState;
        this.rssi = rssi;
    }
    public BleBean(int type,String name, String address, int rssi) {
        this.type = type;
        this.name = name;
        this.address = address;
        this.rssi = rssi;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public int getRssi() {
        return rssi;
    }

    public void setRssi(int rssi) {
        this.rssi = rssi;
    }

    public byte[] getBytes() {
        return bytes;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public BluetoothGatt getGatt() {
        return gatt;
    }

    public void setGatt(BluetoothGatt gatt) {
        this.gatt = gatt;
    }

    public int getConnectState() {
        return connectState;
    }

    public void setConnectState(int connectState) {
        this.connectState = connectState;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    @Override
    public String toString() {
        return "BleBean{" +
                "gatt=" + gatt +
                ", bytes=" + Arrays.toString(bytes) +
                ", type=" + type +
                ", connectState=" + connectState +
                ", name='" + name + '\'' +
                ", address='" + address + '\'' +
                '}';
    }
}
