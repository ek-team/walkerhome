package com.pharos.walker.beans;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

/**
 * Created by zhanglun on 2021/5/21
 * Describe:
 */
public class ChartRecordBean implements Parcelable {
    private String date;
    private int painLevel;
    private int targetWeight;
    private int averageWeight;
    private int painFeedback;
    private int classId;

    public int getClassId() {
        return classId;
    }

    public void setClassId(int classId) {
        this.classId = classId;
    }

    private List<NumOfTimeBean> numOfTimeBeanList;

    public List<NumOfTimeBean> getNumOfTimeBeanList() {
        return numOfTimeBeanList;
    }

    public void setNumOfTimeBeanList(List<NumOfTimeBean> numOfTimeBeanList) {
        this.numOfTimeBeanList = numOfTimeBeanList;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public int getPainLevel() {
        return painLevel;
    }

    public void setPainLevel(int painLevel) {
        this.painLevel = painLevel;
    }

    public int getTargetWeight() {
        return targetWeight;
    }

    public void setTargetWeight(int targetWeight) {
        this.targetWeight = targetWeight;
    }

    public int getAverageWeight() {
        return averageWeight;
    }

    public void setAverageWeight(int averageWeight) {
        this.averageWeight = averageWeight;
    }

    public int getPainFeedback() {
        return painFeedback;
    }

    public void setPainFeedback(int painFeedback) {
        this.painFeedback = painFeedback;
    }

    public static class NumOfTimeBean implements Parcelable {
        private String dateSte;
        private int targetWeight;
        private int averageWeight;
        private int frequency;

        public boolean isPainError() {
            return isPainError;
        }

        public void setPainError(boolean painError) {
            isPainError = painError;
        }

        private boolean isPainError;

        public int getFrequency() {
            return frequency;
        }

        public void setFrequency(int frequency) {
            this.frequency = frequency;
        }

        public String getDateSte() {
            return dateSte;
        }

        public void setDateSte(String dateSte) {
            this.dateSte = dateSte;
        }

        public int getTargetWeight() {
            return targetWeight;
        }

        public void setTargetWeight(int targetWeight) {
            this.targetWeight = targetWeight;
        }

        public int getAverageWeight() {
            return averageWeight;
        }

        public void setAverageWeight(int averageWeight) {
            this.averageWeight = averageWeight;
        }

        public NumOfTimeBean() {
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeString(this.dateSte);
            dest.writeInt(this.targetWeight);
            dest.writeInt(this.averageWeight);
            dest.writeInt(this.frequency);
            dest.writeByte(this.isPainError ? (byte) 1 : (byte) 0);
        }

        protected NumOfTimeBean(Parcel in) {
            this.dateSte = in.readString();
            this.targetWeight = in.readInt();
            this.averageWeight = in.readInt();
            this.frequency = in.readInt();
            this.isPainError = in.readByte() != 0;
        }

        public static final Creator<NumOfTimeBean> CREATOR = new Creator<NumOfTimeBean>() {
            @Override
            public NumOfTimeBean createFromParcel(Parcel source) {
                return new NumOfTimeBean(source);
            }

            @Override
            public NumOfTimeBean[] newArray(int size) {
                return new NumOfTimeBean[size];
            }
        };
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.date);
        dest.writeInt(this.painLevel);
        dest.writeInt(this.targetWeight);
        dest.writeInt(this.averageWeight);
        dest.writeInt(this.painFeedback);
        dest.writeInt(this.classId);
        dest.writeTypedList(this.numOfTimeBeanList);
    }

    public ChartRecordBean() {
    }

    protected ChartRecordBean(Parcel in) {
        this.date = in.readString();
        this.painLevel = in.readInt();
        this.targetWeight = in.readInt();
        this.averageWeight = in.readInt();
        this.painFeedback = in.readInt();
        this.classId = in.readInt();
        this.numOfTimeBeanList = in.createTypedArrayList(NumOfTimeBean.CREATOR);
    }

    public static final Parcelable.Creator<ChartRecordBean> CREATOR = new Parcelable.Creator<ChartRecordBean>() {
        @Override
        public ChartRecordBean createFromParcel(Parcel source) {
            return new ChartRecordBean(source);
        }

        @Override
        public ChartRecordBean[] newArray(int size) {
            return new ChartRecordBean[size];
        }
    };
}
