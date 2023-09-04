package com.pharos.walker.customview;

import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.ScatterDataSet;

import java.util.List;

public class MyScatterDataSet extends ScatterDataSet {
    private List<Entry> mVals;
    private List<Integer> mColors;
    public MyScatterDataSet(List<Entry> yVals, String label) {
        super(yVals, label);
        this.mVals = yVals;
    }

    public void setColors(List<Integer> colors) {
        this.mColors = colors;
    }

    @Override
    public int getColor() {
        return super.getColor();
    }

    private int realIndex = -1;
    private int lastIndex = 0;
    private int zeroCount = 0;
    @Override
    public int getColor(int index) {
        if (index == 0){
            zeroCount ++;
        }
        if (zeroCount > 2){//解决加载动画第一次有可能加载小于两个 会导致数组越界的问题
            realIndex = -1;
            zeroCount = 0;
        }
        if (index >= lastIndex){
            realIndex = realIndex + 1;
        }else {
            realIndex = 0;
            zeroCount = 0;
        }
        lastIndex = index;
        Entry e = getEntryForIndex(realIndex);
        if (e.getY() < 3){
            return mColors.get(1);
        }else {
            return mColors.get(0);
        }
    }
}
