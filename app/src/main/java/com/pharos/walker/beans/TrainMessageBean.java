package com.pharos.walker.beans;

public class TrainMessageBean {
    private int trainTime;
    private int countOfTime;
    private int timesOfDay;

    public TrainMessageBean(int trainTime, int countOfTime, int timesOfDay) {
        this.trainTime = trainTime;
        this.countOfTime = countOfTime;
        this.timesOfDay = timesOfDay;
    }

    public TrainMessageBean(int trainTime, int countOfTime) {
        this.trainTime = trainTime;
        this.countOfTime = countOfTime;
    }
    public int getTimesOfDay() {
        return timesOfDay;
    }

    public void setTimesOfDay(int timesOfDay) {
        this.timesOfDay = timesOfDay;
    }
    public int getTrainTime() {
        return trainTime;
    }

    public void setTrainTime(int trainTime) {
        this.trainTime = trainTime;
    }

    public int getCountOfTime() {
        return countOfTime;
    }

    public void setCountOfTime(int countOfTime) {
        this.countOfTime = countOfTime;
    }
}
