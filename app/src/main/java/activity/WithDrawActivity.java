package activity;

import android.os.Bundle;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.RadioButton;
import android.widget.RelativeLayout;

import com.chuanqi.yz.R;

public class WithDrawActivity extends BaseActivity {
    private final  int WXIN_PAY=1;
    private final  int ALI_PAY=2;
    private final  int PHONE_PAY=3;
    private final  int INTO_JQZ=4;
    private int payType;//支付方式
    private RadioButton mRbIntoJqz;
    private RadioButton mRbPhone;
    private RadioButton mRbAlipay;
    private RadioButton mRbWxin;
    private RelativeLayout mRtlComplite;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_withdraw);
        initview();
        initRadioButton();
        initevent();
    }


    private void initview() {
        mRbWxin = (RadioButton) findViewById(R.id.rb_wxpay);
        mRbAlipay = (RadioButton) findViewById(R.id.rb_alipay);
        mRbPhone = (RadioButton) findViewById(R.id.rb_phone);
        mRbIntoJqz = (RadioButton) findViewById(R.id.rb_into_jqz);
        mRtlComplite = (RelativeLayout) findViewById(R.id.rtl_complite);
    }
    /**
     * 初始化RadioButton
     */
    private void initRadioButton() {
        mRbWxin.setChecked(true);
        mRbWxin.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    mRbAlipay.setChecked(false);
                    mRbIntoJqz.setChecked(false);
                    mRbPhone.setChecked(false);
                }
            }
        });
        mRbAlipay.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    mRbIntoJqz.setChecked(false);
                    mRbWxin.setChecked(false);
                    mRbPhone.setChecked(false);
                }
            }
        });
        mRbPhone.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    mRbIntoJqz.setChecked(false);
                    mRbWxin.setChecked(false);
                    mRbAlipay.setChecked(false);
                }
            }
        });
        mRbIntoJqz.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    mRbPhone.setChecked(false);
                    mRbWxin.setChecked(false);
                    mRbAlipay.setChecked(false);
                }
            }
        });
    }

    private void initevent() {
        mRtlComplite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mRbWxin.isChecked()){
                    showTip("微信支付");
                }else if(mRbAlipay.isChecked()){
                    showTip("支付宝支付");
                }else if(mRbPhone.isChecked()){
                    showTip("充值话费");
                }else if(mRbIntoJqz.isChecked()){
                    showTip("转入聚钱庄");
                }
            }
        });
    }
}