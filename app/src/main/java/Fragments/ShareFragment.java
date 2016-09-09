package Fragments;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.chuanqi.yz.R;

import java.util.HashMap;

import Constance.constance;
import Mob.Share.OnekeyShare;
import Utis.GsonUtils;
import Utis.OkHttpUtil;
import Utis.SharePre;
import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.PlatformActionListener;
import cn.sharesdk.framework.ShareSDK;
import model.Result;

public class ShareFragment extends BaseFragment {
    private String platformType;
    public ShareFragment() {

    }
    public Handler mHandler=new Handler()
    {
        public void handleMessage(Message msg)
        {
            switch(msg.what)
            {
                case 1:
                    getShareMoney();
                    break;
                case 2:
                    break;
                case 3:
                    break;
                default:
                    break;
            }
            super.handleMessage(msg);
        }
    };
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View layout=inflater.inflate(R.layout.fragment_share,null);
        initview(layout);
        return layout;
    }
    private void initview(View layout) {
           layout.findViewById(R.id.tv_share).setOnClickListener(new View.OnClickListener() {
               @Override
               public void onClick(View view) {
                   new Thread(new Runnable() {
                       @Override
                       public void run() {
                           Share();
                       }
                   }).start();
               }
           });
    }
    public  void  Share(){
        ShareSDK.initSDK(getActivity());
        OnekeyShare oks = new OnekeyShare();
        oks.disableSSOWhenAuthorize();//关闭sso授权
        oks.setTitle("易赚ATM");  // titleUrl是标题的网络链接，仅在人人网和QQ空间使用
        oks.setTitleUrl("http://weibo.com/ttarticle/p/show?id=2309404017475523111097");
        oks.setText("易赚ATM,快来加入一起来赚吧！");  // text是分享文本，所有平台都需要这个字段
        // imagePath是图片的本地路径，Linked-In以外的平台都支持此参数
        oks.setImageUrl("http://bmob-cdn-4915.b0.upaiyun.com/2016/09/08/e52ab1af40b10546807df1d125c2bc70.jpg");
        oks.setUrl("http://weibo.com/ttarticle/p/show?id=2309404017475523111097"); // url仅在微信（包括好友和朋友圈）中使用
        oks.setComment("易赚有你才完美");// comment是我对这条分享的评论，仅在人人网和QQ空间使用
        oks.setSite(getString(R.string.app_name)); // site是分享此内容的网站名称，仅在QQ空间使用
        oks.setSiteUrl("http://weibo.com/ttarticle/p/show?id=2309404017475523111097");   // siteUrl是分享此内容的网站地址，仅在QQ空间使用
        oks.setCallback(new PlatformActionListener() {
            @Override
            public void onComplete(Platform platform, int i, HashMap<String, Object> hashMap) {
                Toast("分享成功");
                platformType=platform.getName();
                Message message=new Message();
                message.what=1;
                mHandler.sendMessage(message);
            }
            @Override
            public void onError(Platform platform, int i, Throwable throwable) {
                Log.e("分享状态","onError");
                Message message=new Message();
                message.what=2;
                mHandler.sendMessage(message);
            }
            @Override
            public void onCancel(Platform platform, int i) {
                Toast("分享取消");
                Message message=new Message();
                message.what=3;
                mHandler.sendMessage(message);
            }
        });
        oks.show(getActivity()); // 启动分享GUI
    }
    private void getShareMoney() {
        HashMap<String,String> map=new HashMap<String, String>();
        map.put("userid",""+ SharePre.getUserId(getActivity()));
        map.put("rwstyle",""+platformType);
        OkHttpUtil.getInstance().Post(map, constance.URL.SHARE_GET, new OkHttpUtil.FinishListener() {
            @Override
            public void Successfully(boolean IsSuccess, String data, String Msg) {
//                Toast(data.toString()+platformType);
                if(IsSuccess){
                    Result result = GsonUtils.parseJSON(data, Result.class);
                    if(result.getRun().equals("1")){
                        Toast("恭喜您获得0.3元");
                        Intent intent = new Intent();
                        intent.putExtra("update",true);
                        intent.setAction("update");   //
                        getActivity().sendBroadcast(intent);
                    }
                }else {
                    Toast(data.toString());
                }
            }
        });
    }
}
