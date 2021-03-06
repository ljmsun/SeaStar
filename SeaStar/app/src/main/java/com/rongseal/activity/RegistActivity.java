package com.rongseal.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.rongseal.R;
import com.rongseal.bean.response.RegistResponse;
import com.rongseal.widget.ClearWriteEditText;
import com.rongseal.widget.dialog.LoadDialog;
import com.sd.core.network.http.HttpException;
import com.sd.core.utils.NToast;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by AMing on 15/11/10.
 * Company RongCloud
 */
public class RegistActivity extends BaseActivity implements View.OnClickListener {

    private static final int REGIST_CODE = 2015;

    private int REGIST_BACK = 1001;
    private ClearWriteEditText mEmail , mPassword ,mUserName;

    private Button mButton;

    protected Context mContext;

    private String sEmail;
    private String sPassword;
    private String sUserName;

    private TextView mTextView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.rp_regist_activity);
        mContext = this;
        setTitle(R.string.regist);
        initView();
    }

    private void initView() {
        mTextView = (TextView) findViewById(R.id.dev);
        mTextView.getPaint().setFlags(Paint. UNDERLINE_TEXT_FLAG);
        mTextView.getPaint().setAntiAlias(true);
        mTextView.setText(R.string.rongcloud_dev_agreement);
        mEmail = (ClearWriteEditText) findViewById(R.id.reg_email);
        mPassword = (ClearWriteEditText) findViewById(R.id.reg_password);
        mUserName = (ClearWriteEditText) findViewById(R.id.reg_username);
        mButton = (Button) findViewById(R.id.reg_button);
        mButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.reg_button:
                sEmail = mEmail.getText().toString().trim();
                sPassword = mPassword.getText().toString().trim();
                sUserName = mUserName.getText().toString().trim();


                if (TextUtils.isEmpty(sEmail)|TextUtils.isEmpty(sPassword)|TextUtils.isEmpty(sUserName)) {
                    Toast.makeText(this, R.string.regist_info_not_null, Toast.LENGTH_SHORT).show();
                    mEmail.setShakeAnimation();
                    mPassword.setShakeAnimation();
                    mUserName.setShakeAnimation();
                    return;
                }

                if (!isEmail(sEmail)) {
                    NToast.shortToast(mContext, getResources().getString(R.string.email_iserror));
                    mEmail.setShakeAnimation();
                    return;
                }
                    LoadDialog.show(mContext);
                    request(REGIST_CODE);
                break;
        }
    }

    @Override
    public Object doInBackground(int requestCode) throws HttpException {
        switch (requestCode) {
            case REGIST_CODE:
                return action.regist(sEmail,sPassword,sUserName,"13120241790");
        }
        return super.doInBackground(requestCode);
    }

    @Override
    public void onSuccess(int requestCode, Object result) {
        switch (requestCode){
            case REGIST_CODE:
                LoadDialog.dismiss(mContext);
                if (result != null) {
                    RegistResponse res = (RegistResponse)result;
                    switch (res.getCode()) {
                        case 200:
                            NToast.shortToast(mContext, getResources().getString(R.string.regist_success));
                            Intent data = new Intent();
                            data.putExtra("email", sEmail);
                            data.putExtra("password", sPassword);
                            setResult(REGIST_BACK, data);
                            RegistActivity.this.finish();
                            break;
                        case 101:
                            NToast.shortToast(mContext,getResources().getString(R.string.email_registed));
                            break;
                    }
                }
                break;
        }
    }

    @Override
    public void onFailure(int requestCode, int state, Object result) {
        switch (requestCode){
            case REGIST_CODE:
                NToast.shortToast(mContext, getResources().getString(R.string.regist_fail));
                LoadDialog.dismiss(mContext);
                break;
        }
    }



    /**
     * 邮箱格式是否正确
     *
     * @param email
     * @return
     */
    public boolean isEmail(String email) {

        if (TextUtils.isEmpty(email))
            return false;

        String expression = "^[\\w\\.-]+@([\\w\\-]+\\.)+[A-Z]{2,4}$";

        Pattern pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(email);

        if (matcher.matches())
            return true;
        else
            return false;

    }
}
