package com.pharos.walker.services;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.Nullable;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.pharos.walker.beans.DataSendBean;
import com.pharos.walker.beans.InfoBean;
import com.pharos.walker.beans.PlanEntity;
import com.pharos.walker.beans.SubPlanEntity;
import com.pharos.walker.beans.TrainDataEntity;
import com.pharos.walker.beans.UserBean;
import com.pharos.walker.beans.UserTrainRecordEntity;
import com.pharos.walker.constants.Global;
import com.pharos.walker.constants.SocketCmd;
import com.pharos.walker.database.SubPlanManager;
import com.pharos.walker.database.TrainDataManager;
import com.pharos.walker.database.TrainPlanManager;
import com.pharos.walker.database.UserManager;
import com.pharos.walker.database.UserTrainRecordManager;
import com.xuhao.didi.core.iocore.interfaces.ISendable;
import com.xuhao.didi.core.pojo.OriginalData;
import com.xuhao.didi.socket.client.sdk.OkSocket;
import com.xuhao.didi.socket.common.interfaces.common_interfacies.server.IClient;
import com.xuhao.didi.socket.common.interfaces.common_interfacies.server.IClientIOCallback;
import com.xuhao.didi.socket.common.interfaces.common_interfacies.server.IClientPool;
import com.xuhao.didi.socket.common.interfaces.common_interfacies.server.IServerManager;
import com.xuhao.didi.socket.common.interfaces.common_interfacies.server.IServerShutdown;
import com.xuhao.didi.socket.server.action.ServerActionAdapter;

import java.lang.reflect.Type;
import java.nio.charset.Charset;
import java.util.List;

/**
 * Created by zhanglun on 2021/6/7
 * Describe:
 */
public class SocketServices extends Service implements IClientIOCallback {
    private IServerManager mServerManager;
    private int mPort = 9001;
    private Gson gson = new Gson();
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mServerManager = OkSocket.server(mPort).registerReceiver(new ServerActionAdapter() {
            @Override
            public void onServerListening(int serverPort) {
                Log.i("ServerCallback", Thread.currentThread().getName() + " onServerListening,serverPort:" + serverPort);
            }

            @Override
            public void onClientConnected(IClient client, int serverPort, IClientPool clientPool) {
                Log.i("ServerCallback", Thread.currentThread().getName() + " onClientConnected,serverPort:" + serverPort + "--ClientNums:" + clientPool.size() + "--ClientTag:" + client.getUniqueTag());
                client.addIOCallback(SocketServices.this);
            }

            @Override
            public void onClientDisconnected(IClient client, int serverPort, IClientPool clientPool) {
                Log.i("ServerCallback", Thread.currentThread().getName() + " onClientDisconnected,serverPort:" + serverPort + "--ClientNums:" + clientPool.size() + "--ClientTag:" + client.getUniqueTag());
                client.removeIOCallback(SocketServices.this);
            }

            @Override
            public void onServerWillBeShutdown(int serverPort, IServerShutdown shutdown, IClientPool clientPool, Throwable throwable) {
                Log.i("ServerCallback", Thread.currentThread().getName() + " onServerWillBeShutdown,serverPort:" + serverPort + "--ClientNums:" + clientPool
                        .size());
                shutdown.shutdown();
            }

            @Override
            public void onServerAlreadyShutdown(int serverPort) {
                Log.i("ServerCallback", Thread.currentThread().getName() + " onServerAlreadyShutdown,serverPort:" + serverPort);
            }
        });
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (!mServerManager.isLive()) {
            mServerManager.listen();
        }
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mServerManager != null && mServerManager.isLive()){
            mServerManager.shutdown();
        }
    }

    @Override
    public void onClientRead(OriginalData originalData, IClient client, IClientPool<IClient, String> clientPool) {
        String receiveResult = new String(originalData.getBodyBytes(), Charset.forName("utf-8"));
        if (TextUtils.isEmpty(receiveResult))
            return;
        String data = "";
        String head;
        int endPosition = receiveResult.indexOf(Global.Delimiter);
        if (endPosition > 0){
            data = receiveResult.substring(endPosition);
            head = receiveResult.substring(0,endPosition);
        }else {
            head = receiveResult;
        }
        switch (head){
            case SocketCmd.SYNC_USER:
                data = data.replaceAll("\\[","");
                handleSyncUser(client,data);
                break;
            case SocketCmd.SYNC_PLAN:
                handleSyncPlan(client,data);
                break;
            case SocketCmd.SYNC_PLAN_ASK:
                handleReceiveSyncPlan(client,data);
                break;
            case SocketCmd.SYNC_SUB_PLAN_ASK:
                handleReceiveSyncSubPlan(data);
                break;
            case SocketCmd.SYNC_SUB_PLAN:
                data = data.replaceAll("\\[","");
                handleSyncSubPlan(client,data);
                break;
            case SocketCmd.SYNC_TRAIN_RECORD_ASK:
                data = data.replaceAll("\\[","");
                handleSyncRecord(client,data);
                break;
            case SocketCmd.SYNC_TRAIN_DATA_ASK:
                handleSyncTrainData(data);
                break;
        }

    }
    private void handleSyncUser(IClient client,String data){
        List<UserBean> userBeanList = UserManager.getInstance().loadCompareResult(data);
        for (UserBean userBean : userBeanList){
            String result = SocketCmd.SYNC_USER_ASK + gson.toJson(userBean);
            DataSendBean msgDataBean = new DataSendBean(result);
            client.send(msgDataBean);
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        String result = SocketCmd.SYNC_USER_FINISH + "{";
        DataSendBean msgDataBean = new DataSendBean(result);
        client.send(msgDataBean);
    }
    private void handleSyncPlan(IClient client,String data){
        if (!TextUtils.isEmpty(data)){
            Type typeRecord = new TypeToken<List<InfoBean>>(){}.getType();
            List<InfoBean> infoBeans = gson.fromJson(data,typeRecord);
            for (int i = 0; i < infoBeans.size(); i++){
                int updateStatus =  TrainPlanManager.getInstance().comparePlanUpdateDate(infoBeans.get(i).getUpdateDate(),infoBeans.get(i).getUserId());
                String result = "";
                if (updateStatus == Global.UploadStatus){
                    result = SocketCmd.SYNC_PLAN + Global.Delimiter +  infoBeans.get(i).getUserId();
                }else if (updateStatus == Global.UploadLocalStatus){
                    List<PlanEntity> planEntityList = TrainPlanManager.getInstance().getPlanListByUserId(infoBeans.get(i).getUserId());
                    result = SocketCmd.SYNC_PLAN_ASK + gson.toJson(planEntityList);
                }
                DataSendBean msgDataBean = new DataSendBean(result);
                client.send(msgDataBean);
                try {
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
        String result = SocketCmd.SYNC_TRAIN_RECORD + Global.Delimiter;
        DataSendBean msgDataBean = new DataSendBean(result);
        client.send(msgDataBean);

    }
    private void handleReceiveSyncPlan(IClient client,String dataStr){
        Type type = new TypeToken<List<PlanEntity>>(){}.getType();
        List<PlanEntity> planEntityList = gson.fromJson(dataStr,type);
        TrainPlanManager.getInstance().insertMany(planEntityList);
        String userId = "";
        if (planEntityList.size() > 0){
            userId = planEntityList.get(0).getUserId() + "";
        }
        if (TextUtils.isEmpty(userId))
            return;
        String request = SocketCmd.SYNC_SUB_PLAN + Global.Delimiter + userId;
        DataSendBean msgDataBean = new DataSendBean(request);
        client.send(msgDataBean);
    }
    private void handleReceiveSyncSubPlan(String dataStr){
        Type type1 = new TypeToken<List<SubPlanEntity>>(){}.getType();
        List<SubPlanEntity> subPlanEntityList = gson.fromJson(dataStr,type1);
        SubPlanManager.getInstance().insertMany(subPlanEntityList);
    }
    private void handleSyncSubPlan(IClient client,String data){
        if (!TextUtils.isEmpty(data)){
            List<SubPlanEntity> subPlanEntityList = SubPlanManager.getInstance().loadDataByUserId(Long.parseLong(data));
            String result = SocketCmd.SYNC_SUB_PLAN_ASK + gson.toJson(subPlanEntityList);
            DataSendBean msgDataBean = new DataSendBean(result);
            client.send(msgDataBean);
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
    private void handleSyncRecord(IClient client,String data){
//        Type typeRecord = new TypeToken<List<UserTrainRecordEntity>>(){}.getType();
        UserTrainRecordEntity userTrainRecord = gson.fromJson(data,UserTrainRecordEntity.class);
        UserTrainRecordManager.getInstance().insertSingle(userTrainRecord);
        String recordUserId = userTrainRecord.getUserId() + "";
        String recordFrequency = userTrainRecord.getFrequency() + "";
        String dateDay = userTrainRecord.getDateStr() + "";
        if (TextUtils.isEmpty(recordUserId))
            return;
        String request = SocketCmd.SYNC_TRAIN_DATA + Global.Delimiter + recordUserId + Global.Comma + dateDay + Global.Comma + recordFrequency ;
        DataSendBean msgDataBean = new DataSendBean(request);
        client.send(msgDataBean);
    }
    private void handleSyncTrainData(String data){
        Type typeRecord = new TypeToken<List<TrainDataEntity>>(){}.getType();
        List<TrainDataEntity> trainDataEntities = gson.fromJson(data,typeRecord);
        TrainDataManager.getInstance().insertMany(trainDataEntities);
    }

    @Override
    public void onClientWrite(ISendable sendable, IClient client, IClientPool<IClient, String> clientPool) {

    }
}
