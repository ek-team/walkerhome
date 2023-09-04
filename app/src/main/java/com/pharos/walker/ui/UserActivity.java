package com.pharos.walker.ui;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.appcompat.widget.SearchView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.pharos.walker.beans.ActivationCodeBean;
import com.pharos.walker.beans.DeviceBindHospitalBean;
import com.pharos.walker.beans.PlanEntity;
import com.pharos.walker.beans.ServerPlanEntity;
import com.pharos.walker.beans.TokenBean;
import com.pharos.walker.beans.UserBean;
import com.pharos.walker.MainActivity;
import com.pharos.walker.R;
import com.pharos.walker.adapter.UsersAdapter;
import com.pharos.walker.constants.Api;
import com.pharos.walker.constants.Global;
import com.pharos.walker.constants.MessageEvent;
import com.pharos.walker.customview.GridSpacingItemDecoration;
import com.pharos.walker.customview.rxdialog.RxDialogSureCancel;
import com.pharos.walker.database.ActivationCodeManager;
import com.pharos.walker.database.SubPlanManager;
import com.pharos.walker.database.TrainPlanManager;
import com.pharos.walker.database.UserManager;
import com.pharos.walker.error.CrashHandler;
import com.pharos.walker.utils.DateFormatUtil;
import com.pharos.walker.utils.NetworkUtils;
import com.pharos.walker.utils.OkHttpUtils;
import com.pharos.walker.utils.SPHelper;
import com.pharos.walker.utils.ToastUtils;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.Request;

/**
 * Created by zhanglun on 2021/4/25
 * Describe:
 */
public class UserActivity extends BaseActivity {
    @BindView(R.id.iv_back)
    ImageView ivBack;
    @BindView(R.id.recyclerView)
    RecyclerView mRecyclerView;
    @BindView(R.id.add)
    Button add;
    @BindView(R.id.user_search_view)
    SearchView userSearchView;
    private UsersAdapter mAdapter;
    private UserBean userBean;
    private UserManager mUserManager;
    private int page = 0;
    private ActivationCodeBean codeBean;
    private boolean isNewInsertUser = false;
    private int GetUser = 0;
    private int GetHosipital = 1;
    @Override
    protected void initialize(Bundle savedInstanceState) {
        ButterKnife.bind(this);
        initView();
//        initData();
        initSearchView();
        codeBean = ActivationCodeManager.getInstance().getCodeBean();
        if (codeBean == null || TextUtils.isEmpty(codeBean.getMacAddress())){
            ToastUtils.showShort("mac地址获取失败");
        }else if (NetworkUtils.isConnected()){
            getScanUser();
            getHospitalInfo(codeBean.getMacAddress());
        }
    }

    private void initView() {
        registerForContextMenu(mRecyclerView);
        mAdapter = new UsersAdapter(this);
        mRecyclerView.addItemDecoration(new GridSpacingItemDecoration(4, 20, true));
        mRecyclerView.setLayoutManager(new GridLayoutManager(this, 4));
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setAdapter(mAdapter);
        mAdapter.setOnItemClickListener(position -> {
            UserBean userBean = mAdapter.getData().get(position);
            if (userBean.getId() == 0 && userBean.getCaseHistoryNo().equals("123456")){
                Global.USER_MODE = false;
                SPHelper.saveUser(userBean);
                startTargetActivity(MainActivity.class,true);
            }else {
                Global.USER_MODE = true;
                if (TextUtils.isEmpty(userBean.getPassword())){
                    SPHelper.saveUser(userBean);
                    Bundle bundle = new Bundle();
                    bundle.putInt("SelectUser",1);
                    startTargetActivity(bundle,MainActivity.class,true);
                }else {
                    Bundle bundle = new Bundle();
                    bundle.putParcelable("userInfo",userBean);
                    startTargetActivity(bundle,LoginActivity.class,true);
                }

            }
        });
    }

    private void initData() {
        mUserManager = UserManager.getInstance();
        List<UserBean> userBeanList = mUserManager.loadAllByDate();
        if (userBeanList != null && userBeanList.size() <= 0){
            startTargetActivity(UserEditActivity.class,true);
        }
        if (userBeanList != null && userBeanList.size() > 0){
            mAdapter.setData(userBeanList);
        }

    }

    private void initSearchView() {

        /**
         * 修改Searchview字体大小颜色，同时需要重新布局使Text居中
         */
        // 根据id-search_src_text获取TextView
        SearchView.SearchAutoComplete searchText = (SearchView.SearchAutoComplete) userSearchView.findViewById(R.id.search_src_text);
        //修改字体大小
        searchText.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 22);
        //重新布局，使其居中
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        lp.gravity = Gravity.CENTER_VERTICAL;
        searchText.setLayoutParams(lp);
        //修改字体颜色
        searchText.setTextColor(ContextCompat.getColor(this, R.color.black));
        searchText.setHintTextColor(ContextCompat.getColor(this, R.color.system_gray));
        /**
         * 修改SearchView左边图标
         */
        // 根据id-search_mag_icon获取ImageView
        ImageView searchButton = (ImageView) userSearchView.findViewById(R.id.search_mag_icon);
        //重新设置ImageView的宽高
        LinearLayout.LayoutParams lpimg = new LinearLayout.LayoutParams(40, 40);
        lpimg.gravity = Gravity.CENTER;
        lpimg.leftMargin = 10;
        searchButton.setLayoutParams(lpimg);
        searchButton.setBackgroundResource(R.mipmap.icon_search);
        searchButton.setImageResource(R.mipmap.searchblank);
        /**
         * 根据输入框输入值的改变来过滤搜索
         */
        // 设置搜索文本监听
        userSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            // 当点击搜索按钮时触发该方法
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (!TextUtils.isEmpty(newText)){
                    mAdapter.setData(mUserManager.loadByNameLike(newText));
                }else {
                    mAdapter.setData(mUserManager.loadAllByDate());
                }
                return false;
            }
        });
    }

    //给菜单项添加事件
    @Override
    public boolean onContextItemSelected(MenuItem item) {
        userBean = mAdapter.getData().get(mAdapter.getPosition());
        switch (item.getItemId()) {
            case R.id.item_delete:
                if (userBean.getId() == 0 && userBean.getCaseHistoryNo().equals("123456")){
                    ToastUtils.showShort("系统用户不能删除！");
                }else {
                    deleteDialog();
                }
                break;
            case R.id.item_update:
                if (userBean.getId() == 0 && userBean.getCaseHistoryNo().equals("123456")){
                    ToastUtils.showShort("系统用户不能操作！");
                }else {
                    SPHelper.saveUser(userBean);
                    Intent intent = new Intent(this,UserEditActivity.class);
                    intent.putExtra("Mode",1);
                    startActivity(intent);
                }
                break;
            case R.id.item_query:
                if (userBean.getId() == 0 && userBean.getCaseHistoryNo().equals("123456")){
                    ToastUtils.showShort("系统用户不能操作！");
                }else {
                    SPHelper.saveUser(userBean);
                    Intent intent1 = new Intent(this,UserEditActivity.class);
                    intent1.putExtra("Mode",2);
                    startActivity(intent1);
                }
                break;
            default:
                break;
        }
        return super.onContextItemSelected(item);
    }
    protected void deleteDialog() {
        RxDialogSureCancel dialog = new RxDialogSureCancel(this);
        dialog.setContent("是否删除用户？");
        dialog.setSureListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mUserManager.deleteById(userBean.getUserId());
                mAdapter.setData(mUserManager.loadAllByDate());
                dialog.dismiss();
            }
        });
        dialog.show();
    }
    @Override
    protected int getLayoutResId() {
        return R.layout.activity_user;
    }
    @Override
    protected void onResume() {
        initData();
//        mAdapter.setData(mUserManager.loadAll());
        super.onResume();
    }

    @OnClick({R.id.iv_back, R.id.add})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_back:
                finish();
                break;
            case R.id.add:
                if (Global.ReleaseVersion != Global.ClientVersion){
                    startTargetActivity(RegisterActivity.class,true);
                }
                break;
        }
    }
    private void getHospitalInfo(String macAddress){
        OkHttpUtils.getAsync(Api.getHospitalByMac + macAddress, true, new OkHttpUtils.DataCallBack() {
            @Override
            public void requestFailure(Request request, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void requestSuccess(String result) throws Exception {
                Log.e("User Activity", "获取医院信息: " + result);
                JSONObject toJsonObj= new JSONObject(result);
                int code = toJsonObj.getInt("code");
                if (code == 0){
                    DeviceBindHospitalBean bean = new Gson().fromJson(result,DeviceBindHospitalBean.class);
                    SPHelper.saveHosipitalAddress(bean.getData().getProvince() + bean.getData().getCity() + bean.getData().getArea());
                    SPHelper.saveHosipitalName(bean.getData().getName());
                }
            }
        });
    }
    private void getScanUser(){
        OkHttpUtils.getAsync(Api.getPlatformQrScanUser + "?macAddress=" + codeBean.getMacAddress() , true, new OkHttpUtils.DataCallBack() {
            @Override
            public void requestFailure(Request request, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void requestSuccess(String result) throws Exception {
                Log.e("User Activity", "获取扫码用户返回结果: " + result);
                JSONObject toJsonObj= new JSONObject(result);
                int code = toJsonObj.getInt("code");
                if (code == 0){
                    Type type = new TypeToken<List<UserBean>>(){}.getType();
                    List<UserBean> userBeans = new Gson().fromJson(toJsonObj.getString("data"),type);
                    for (UserBean userBean : userBeans){
                        userBean.setIsUpload(Global.UploadNetStatus);
                        if (UserManager.getInstance().isUniqueUserId(userBean.getUserId())){
                            UserManager.getInstance().insert(userBean,3);
                            isNewInsertUser = true;
                        }
                    }
                    if (isNewInsertUser){
                        initData();
                        isNewInsertUser = false;
                    }
                    if (userBeans.size() > 0){
                        resetScanUser();
                    }
                }else if (code == 401){
                    getToken(GetUser);
                }
            }
        });
    }
    private void resetScanUser(){
        OkHttpUtils.deleteAsyncToken(Api.clearPlatformQrScanUser  +  codeBean.getMacAddress() , new OkHttpUtils.DataCallBack() {
            @Override
            public void requestFailure(Request request, IOException e) {
                e.printStackTrace();

            }

            @Override
            public void requestSuccess(String result) throws Exception {
                Log.e("User Activity", "重置扫码用户返回结果: " + result);
                JSONObject toJsonObj= new JSONObject(result);
                int code = toJsonObj.getInt("code");
                if (code == 0){

                }

            }
        });
    }

    private void getToken(int flag){
        OkHttpUtils.getAsyncToken(Api.tokenUrl + "?grant_type=client_credentials" , true, new OkHttpUtils.DataCallBack() {
            @Override
            public void requestFailure(Request request, IOException e) {
                e.printStackTrace();

            }
            @Override
            public void requestSuccess(String result) throws Exception {
                Log.e("MainActivity", "requestSuccess: " + result);
                TokenBean tokenBean = new Gson().fromJson(result,TokenBean.class);
                if (tokenBean.getCode() == 0){
                    SPHelper.saveToken(tokenBean.getData().getAccess_token());
                    if (flag == GetUser){
                        getScanUser();
                    } else if (flag == GetHosipital) {
                        getHospitalInfo(codeBean.getMacAddress());
                    }

                }


            }
        });
    }
}
