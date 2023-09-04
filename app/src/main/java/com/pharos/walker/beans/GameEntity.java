package com.pharos.walker.beans;

/**
 * 游戏操作类
 */
public class GameEntity {

    public static final String OPERATING_START_GAME = "start_game";
    public static final String OPERATING_STOP_GAME = "stop_game";
    public static final String OPERATING_START_GAME_DOG = "start_game_dog";
    public static final String OPERATING_START_GAME_FOREST = "start_game_forest";
    public static final String OPERATING_START_GAME_BEACH = "start_game_beach";
    public static final String OPERATING_SET_VALUE = "set_value";
    public static final String OPERATING_SET_COUNT = "set_count";

    private String operating;
    private float value;
    private float weight;

    public GameEntity() {

    }

    public GameEntity(String operating) {
        this.operating = operating;
    }

    public GameEntity(String operating, float value) {
        this.operating = operating;
        this.value = value;
    }

    public GameEntity(String operating, float value, float weight) {
        this.operating = operating;
        this.value = value;
        this.weight = weight;
    }

    public String getOperating() {
        return operating;
    }

    public void setOperating(String operating) {
        this.operating = operating;
    }

    public float getValue() {
        return value;
    }

    public void setValue(float value) {
        this.value = value;
    }

    public float getWeight() {
        return weight;
    }

    public void setWeight(float weight) {
        this.weight = weight;
    }
}
