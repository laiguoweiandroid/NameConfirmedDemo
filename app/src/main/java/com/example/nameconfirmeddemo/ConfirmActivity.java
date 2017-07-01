package com.example.nameconfirmeddemo;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.nameconfirmeddemo.util.CustomProgressDialog;
import com.example.nameconfirmeddemo.util.DataUtil;
import com.example.nameconfirmeddemo.util.ToastUtil;
import com.show.api.ShowApiRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.util.Timer;
import java.util.TimerTask;

import timber.log.Timber;

public class ConfirmActivity extends AppCompatActivity implements View.OnClickListener {
    private EditText etPhone,etCode,etIdNumber,etName;
    private Button btnGetCode;
    private Button signBtn;
    private FrameLayout mBack;
    private TimerTask timerTask;
    private Timer timer;
    private int timess;
    private Context mcontext;
    private CustomProgressDialog mProgressbar;
    private static final String appid="41431";//要替换成自己的
    private static final String secret="fab178f610284d8fbe5b9dfacdc6c233";//要替换成自己的
    private static final String host="http://route.showapi.com/1389-1";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirm);
        mcontext=this;
        initViews();
        setListener();
    }

    /**
     * 初始化控件
     */
    private void initViews() {
        etPhone = (EditText) findViewById(R.id.edt_phone);
        etCode = (EditText) findViewById(R.id.edt_write_code);
        btnGetCode = (Button) findViewById(R.id.btn);
        signBtn = (Button) findViewById(R.id.btn_sign);
        etIdNumber = (EditText) findViewById(R.id.id_card_num);
        etName = (EditText) findViewById(R.id.et_name);
        mBack = (FrameLayout) findViewById(R.id.iv_back);
    }

    /**
     * 设置监听
     */
    private void setListener() {
        btnGetCode.setOnClickListener(this);
        signBtn.setOnClickListener(this);
        mBack.setOnClickListener(this);

    }
    /**
     * 控件点击事件
     * @param view
     */
    @Override
    public void onClick(View view) {
        Timber.v("============onClick==============="+view);
        if(view!=null){
            switch (view.getId()){
                case R.id.btn:
                    startTimer();
                    doGetCode();
                    break;
                case R.id.btn_sign:
                    stopTimer();
                    doConfirm();
                    break;
                case R.id.iv_back:
                    onBackPressed();
                    break;
            }
        }
    }

    /**
     * 获取验证码请求
     */
    private void doGetCode() {
        btnGetCode.setEnabled(false);
        String phone = etPhone.getText().toString();
        Timber.v("============phone==============="+phone);
        if(TextUtils.isEmpty(phone)){
            Toast.makeText(ConfirmActivity.this, "手机号码不能为空", Toast.LENGTH_SHORT).show();
            return;
        }
        //请求获取验证码

    }

    /**
     * 提交信息进行验证
     */
    private void doConfirm() {
        String  name = etName.getText().toString();
        String  mIdCardNum = etIdNumber.getText().toString();
        String phoneNum = etPhone.getText().toString();
        String mCodeNum = etCode.getText().toString();
        Timber.v("==============name="+name+",mIdCardNum="+mIdCardNum+",phoneNum=="+phoneNum+",mCodeNum=="+mCodeNum);
        if(TextUtils.isEmpty(mCodeNum)){
            Toast.makeText(ConfirmActivity.this, "验证码不能为空", Toast.LENGTH_SHORT).show();
            return;
        }
        if(TextUtils.isEmpty(name)){
            Toast.makeText(ConfirmActivity.this, "姓名不能为空", Toast.LENGTH_SHORT).show();
            return;
        }
        if(TextUtils.isEmpty(phoneNum)){
            Toast.makeText(ConfirmActivity.this, "手机号码不能为空", Toast.LENGTH_SHORT).show();
            return;
        }
//        try {
//            String IDCardValidate= DataUtil.IDCardValidate(mIdCardNum);
//            if(!TextUtils.isEmpty(IDCardValidate)){
//                Toast.makeText(this,IDCardValidate,Toast.LENGTH_SHORT).show();
//                return;
//            }
            showProgressBar();
            doConfirmCode(phoneNum,mCodeNum);
            doConfirmRequest(mIdCardNum,name,phoneNum);

//        } catch (ParseException e) {
//            e.printStackTrace();
//        }


    }

    private void showProgressBar() {
        if(mProgressbar==null){
            mProgressbar = new CustomProgressDialog(this,"正在实名认证中...",R.drawable.loading_center3);
        }
        mProgressbar.setCancelable(false);
        mProgressbar.show();
    }
    private void hideProgressBar(){
        if(mProgressbar!=null){
            mProgressbar.dismiss();
        }
    }

    /**
     * 对手机号的短信验证
     * @param phoneNum 手机号码
     * @param mCodeNum  验证码
     */
    private void doConfirmCode(String phoneNum, String mCodeNum) {
        Timber.v("===============doConfirmCode==================");

        //对手机号和验证码进行验证

    }
  private Handler mHandler = new Handler(){
      @Override
      public void handleMessage(Message msg) {
          super.handleMessage(msg);
          Timber.v("===============handleMessage=================="+msg.what);
          String msgStr = (String) msg.obj;
          if(msg.what==0){
              //实名认证成功
          }
          else {
             // 实名认证失败
          }
          hideProgressBar();
          ToastUtil.showToast(mcontext,msgStr);
      }
  };
    /**
     * 认证请求
     * @param idCardNum 身份证号
     * @param name 姓名
     */
    private void doConfirmRequest(final String idCardNum, final String name, final String phone) {
        Timber.v("===============doConfirmRequest==================");
        new Thread(){
            @Override
            public void run() {
                Looper.prepare();
                String res = new ShowApiRequest(host, appid, secret)
                        .addTextPara("idCard", idCardNum)
                        .addTextPara("name", name)
                        .addTextPara("phone", phone)
                        .addTextPara("needBelongArea", "false")
                        .post();
                if(!TextUtils.isEmpty(res)){
                    getResultByMsg(res);
                }
                Looper.loop();
            }
        }.start();

    }

    /**
     * 根据认证结果做相应的操作
     * @param res
     */
    private void getResultByMsg(String res) {
        Timber.v("===============getResultByMsg=================="+res);
        String code="1";
        String msg ="";
        try {
            JSONObject json = new JSONObject(res);
            JSONObject bodyJson = json.getJSONObject("showapi_res_body");
            if(bodyJson!=null){
                code=bodyJson.getString("code");
                msg= bodyJson.getString("msg");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Message message = Message.obtain();
        message.what=Integer.parseInt(code);
        message.obj = msg;
        mHandler.handleMessage(message);
    }

    /**
     * 开始验证倒计时
     */
    private void startTimer(){
        Timber.v("===============startTimer()==================");
        timess = (60*1000/1000);
        btnGetCode.setText(String.valueOf(timess+"s"));
        if(timerTask==null){
            timerTask = new TimerTask() {
                @Override
                public void run() {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            timess--;
                            if(timess<=0){
                                stopTimer();
                                return;
                            }
                            btnGetCode.setText(String.valueOf(timess+"s"));
                        }
                    });
                }
            };
        }
        if(timer==null){
            timer = new Timer();
        }
        timer.schedule(timerTask, 1000, 1000);
    }

    /**
     * 结束验证倒计时
     */
    private void stopTimer(){
        Timber.v("===============startTimer()==================");
        if(timer!=null){
            timer.cancel();
            timer=null;
        }
        if(timerTask!=null){
            timerTask.cancel();
            timerTask=null;
        }
        btnGetCode.setText("重新获取");
        btnGetCode.setEnabled(true);
    }

}
