package com.pharos.walker.beans;

import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by xuhao on 2017/5/22.
 */

public class MessageBean extends DataSendBean {

    public MessageBean(GameEntity entity) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("msg", new Gson().toJson(entity));
            content = jsonObject.toString();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
