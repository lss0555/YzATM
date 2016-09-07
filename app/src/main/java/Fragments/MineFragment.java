package Fragments;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.chuanqi.yz.R;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import Constance.constance;
import Manager.UpdateManagers;
import Utis.OkHttpUtil;
import Utis.Utis;
import Utis.UILUtils;
import Utis.GsonUtils;
import Utis.SharePre;
import Views.CircleImageView;
import activity.AboutUsActivity;
import activity.BindAliPayActivity;
import activity.BindWxAccountActivity;
import activity.BindPhoneActivity;
import activity.Red.LookRedRecordActivity;
import activity.UserInfoActivity;
import model.UserInfo;
import model.Yzm;

/**
 * A simple {@link Fragment} subclass.
 */
public class MineFragment extends BaseFragment implements View.OnClickListener{

    private AMapLocationClient locationClient = null;
    private AMapLocationClientOption locationOption = new AMapLocationClientOption();
    private UserInfo mUserInfo;
    private RelativeLayout mRtlPersonInfo;
    private CircleImageView mImgIcons;
    private TextView mTvId;
    private TextView mTvIsBindWx;
    private TextView mTvBindPhoneState;
    private String UserCity;
    private boolean IsBindPhone=false;
    private ProgressBar mProgress;
    private TextView mTvState;
    public MineFragment() {
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View layout=inflater.inflate(R.layout.fragment_mine,null);
        initview(layout);
        initLocation();
        getUserUdidAndCity();
        initUserInfo();
//        initBindAccound();
        return layout;
    }
    /**
     * 初始化定位
     */
    private void initLocation() {
        locationClient = new AMapLocationClient(getActivity());   //初始化client
        locationClient.setLocationOption(getDefaultOption()); //设置定位参数
        locationClient.setLocationListener(new AMapLocationListener() {
            @Override
            public void onLocationChanged(AMapLocation loc) {
                if (null != loc) {
                    if(!(loc.getProvince()+loc.getCity()).equals(SharePre.getCity(getActivity()))){
                        UpdateUserCity(loc.getProvince()+loc.getCity());
                    }
//                    showTip("地点"+loc.getProvince()+loc.getCity());
                    SharePre.saveCity(getActivity(),loc.getProvince()+loc.getCity());
                } else {
                    Toast.makeText(getActivity(),"定位失败",Toast.LENGTH_LONG).show();
                }
            }
        }); // 设置定位监听
        locationClient.setLocationOption(locationOption);   // 设置定位参数
        locationClient.startLocation();  // 启动定位
        /**
         * 定位监听
         */
//        AMapLocationListener locationListener =
    }
    /**
     * 默认的定位参数
     * @since 2.8.0
     */
    private AMapLocationClientOption getDefaultOption(){
        AMapLocationClientOption mOption = new AMapLocationClientOption();
        mOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);//可选，设置定位模式，可选的模式有高精度、仅设备、仅网络。默认为高精度模式
        mOption.setGpsFirst(false);//可选，设置是否gps优先，只在高精度模式下有效。默认关闭
        mOption.setHttpTimeOut(30000);//可选，设置网络请求超时时间。默认为30秒。在仅设备模式下无效
        mOption.setInterval(2000);//可选，设置定位间隔。默认为2秒
        mOption.setNeedAddress(true);//可选，设置是否返回逆地理地址信息。默认是ture
        mOption.setOnceLocation(false);//可选，设置是否单次定位。默认是false
        mOption.setOnceLocationLatest(false);//可选，设置是否等待wifi刷新，默认为false.如果设置为true,会自动变为单次定位，持续定位时不要使用
        return mOption;
    }
    /**
     * 初始化绑定信息
     */
    private Yzm yzm;
    private void initBindAccound() {
        if(!SharePre.getUserId(getActivity()).equals("")){
            HashMap<String,String> map=new HashMap<>();
            map.put("userid",SharePre.getUserId(getActivity()));
            OkHttpUtil.getInstance().Post(map, constance.URL.IS_BIND_PHONE,new OkHttpUtil.FinishListener() {
                @Override
                public void Successfully(boolean IsSuccess, String data, String Msg) {
//                    showTip(data.toString()+"用户UserId"+SharePre.getUserId(getActivity()));
                    Log.w("绑定状态",""+data.toString()+"用户UserId"+SharePre.getUserId(getActivity()));
                if(IsSuccess){
                    yzm = GsonUtils.parseJSON(data, Yzm.class);
                    if(yzm.getRun().equals("1")){
                        mTvBindPhoneState.setText("已绑定");
                    }
                }
                }
            });
        }
    }
    /**
     * 根据手机设备号指定一个用户ID
     */
    private void initUserInfo() {
            HashMap<String,String> map=new HashMap<>();
            map.put("udid", Utis.getIMEI(getActivity()));
            OkHttpUtil.getInstance().Post(map, constance.URL.USER_INFO, new OkHttpUtil.FinishListener() {
                @Override
                public void Successfully(boolean IsSuccess, String data, String Msg) {
//                    showTip("个人资料:"+data.toString());
                    Log.i("个人资料",""+data.toString());
                    if(IsSuccess){
                        mUserInfo= GsonUtils.parseJSON(data,UserInfo.class);
                        SharePre.saveUserId(getActivity(),mUserInfo.getId());
                        UILUtils.displayImage(mUserInfo.getHeadportrait(),mImgIcons);
                        mTvId.setText("ID:"+mUserInfo.getId());
                        initBindAccound();
                    } else {
                        Toast(data.toString());
                    }
                }
            });
    }
    /**
     * 初始化界面
     * @param layout
     */
    private void initview(View layout) {
        mTvBindPhoneState = (TextView) layout.findViewById(R.id.tv_bind_phone_state);
        mTvIsBindWx = (TextView) layout.findViewById(R.id.tv_isbind);
        mTvId = (TextView) layout.findViewById(R.id.tv_id);
        mImgIcons = (CircleImageView) layout.findViewById(R.id.img_icons);
        mRtlPersonInfo = (RelativeLayout) layout.findViewById(R.id.rtl_person_info);
        mRtlPersonInfo.setOnClickListener(this);
        layout.findViewById(R.id.rtl_bind_phone).setOnClickListener(this);
        layout.findViewById(R.id.rtl_alipay).setOnClickListener(this);
        layout.findViewById(R.id.rtl_about_ours).setOnClickListener(this);
        layout.findViewById(R.id.rtl_wx).setOnClickListener(this);
        layout.findViewById(R.id.rtl_update).setOnClickListener(this);
        layout.findViewById(R.id.rtl_message_tip).setOnClickListener(this);
        layout.findViewById(R.id.rtl_detail_get).setOnClickListener(this);
        layout.findViewById(R.id.rtl_opinion).setOnClickListener(this);
    }
    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.rtl_person_info:
                Intent intent=new Intent(getActivity(), UserInfoActivity.class);
                intent.putExtra("info",mUserInfo);
                startActivity(intent);
                break;
            case R.id.rtl_bind_phone:
                if(yzm.getRun().equals("1")){
                    Toast.makeText(getActivity(),"您已绑定过手机号码",Toast.LENGTH_SHORT).show();
                }else {
                    Intent intent_phone=new Intent(getActivity(), BindPhoneActivity.class);
                    startActivity(intent_phone);
                }
                break;
            case R.id.rtl_alipay:
                Intent intent_alipay=new Intent(getActivity(), BindAliPayActivity.class);
                startActivity(intent_alipay);
                break;
            case R.id.rtl_wx:
                Intent intent_wx=new Intent(getActivity(),BindWxAccountActivity.class);
                startActivity(intent_wx);
                break;
            case R.id.rtl_about_ours:
                Intent intent_ours=new Intent(getActivity(), AboutUsActivity.class);
                startActivity(intent_ours);
                break;
            case R.id.rtl_update://检查更新
                UpdateManagers mUpdateManager = new UpdateManagers(getActivity());
                mUpdateManager.setNotUpdateMessageShow(false);
                mUpdateManager.checkUpdateInfo();
                break;
            case R.id.rtl_message_tip://消息提醒
                Toast("待开放中...");
                break;
            case R.id.rtl_opinion://意见反馈
                Toast("待开放中...");
                break;
            case R.id.rtl_detail_get://明细收益
                Intent intent_record=new Intent(getActivity(),LookRedRecordActivity.class);
                startActivity(intent_record);
                break;
        }
    }
    /**
     * 获取应用的信息
     */
    private void getAppPackages() {
        PackageManager pm = getActivity().getPackageManager();
        List<PackageInfo> allApps = getAllApps(getActivity());
        HashMap<String,String> map=new HashMap<>();
        for (int i=0;i<allApps.size();i++){
            map.put("udid",Utis.getIMEI(getActivity())) ;
            map.put("userid",SharePre.getUserId(getActivity())) ;
            map.put("applyid",allApps.get(i).packageName) ;
            map.put("applyname",allApps.get(i).applicationInfo.loadLabel(pm).toString()) ;
            Calendar c = Calendar.getInstance();
            c.setTimeInMillis(allApps.get(i).lastUpdateTime);
            SimpleDateFormat matter1=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            map.put("dInstallTime",matter1.format(c.getTime())+"") ;
            OkHttpUtil.getInstance().Post(map, constance.URL.APP_INFO, new OkHttpUtil.FinishListener() {
                @Override
                public void Successfully(boolean IsSuccess, String data, String Msg) {
                    Log.w("app信息：",""+data.toString());
//					Toast.makeText(getApplicationContext(),data.toString(),Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
    /**
     * 查询手机内非系统应用
     * @param context
     * @return
     */
    public static List<PackageInfo> getAllApps(Context context) {
        List<PackageInfo> apps = new ArrayList<PackageInfo>();
        PackageManager pManager = context.getPackageManager();
        //获取手机内所有应用
        List<PackageInfo> paklist = pManager.getInstalledPackages(0);
        for (int i = 0; i < paklist.size(); i++) {
            PackageInfo pak = (PackageInfo) paklist.get(i);
            //判断是否为非系统预装的应用程序
            if ((pak.applicationInfo.flags & pak.applicationInfo.FLAG_SYSTEM) <= 0) {
                apps.add(pak);
            }
        }
        return apps;
    }
    /**
     * 停止定位
     * @since 2.8.0
     * @author hongming.wang
     */
    private void stopLocation(){
        // 停止定位
        locationClient.stopLocation();
    }

    /**
     * 销毁定位
     *
     * @since 2.8.0
     * @author hongming.wang
     *
     */
    private void destroyLocation(){
        if (null != locationClient) {
            /**
             * 如果AMapLocationClient是在当前Activity实例化的，
             * 在Activity的onDestroy中一定要执行AMapLocationClient的onDestroy
             */
            locationClient.onDestroy();
            locationClient = null;
            locationOption = null;
        }
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        destroyLocation();
    }

    /**
     * 获取用户的udid
     */
    private void getUserUdidAndCity() {
//        showTip("获取状态："+"是否传值"+SharePre.IsPostUdid(getActivity())+"城市："+SharePre.getCity(getActivity()));
        if(!SharePre.IsPostUdid(getActivity()) && SharePre.getCity(getActivity())!=null){
            HashMap<String,String> map=new HashMap<>();
            map.put("udid",Utis.getIMEI(getActivity()));
            map.put("region",SharePre.getCity(getActivity())+"");
            OkHttpUtil.getInstance().Post(map, constance.URL.USER_UDID, new OkHttpUtil.FinishListener() {
                @Override
                public void Successfully(boolean IsSuccess, String data, String Msg) {
                    if(IsSuccess){
                        SharePre.saveIsPostUdid(getActivity(),true);
                    }else {
                        Toast(data.toString());
                    }
//                    showTip("Udid"+data.toString());
                }
            });
        }
    }
    public  void  UpdateUserCity(final String city){
        HashMap<String,String> map=new HashMap<>();
        map.put("udid",""+Utis.getIMEI(getActivity()));
        map.put("region",""+city);
        OkHttpUtil.getInstance().Post(map, constance.URL.UPDATE_USER_CITY, new OkHttpUtil.FinishListener() {
            @Override
            public void Successfully(boolean IsSuccess, String data, String Msg) {
//                  showTip(city+"更换地区:"+data.toString());
            }
        });
    }
}
