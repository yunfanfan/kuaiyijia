package com.yunfan.kuaiyijia;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;


import com.yzq.zxinglibrary.android.CaptureActivity;
import com.yzq.zxinglibrary.common.Constant;
import com.yzq.zxinglibrary.encode.CodeCreator;

import java.util.HashMap;
import java.util.Map;

public class getCarLoadCode extends Activity {

    private static final String TAG = "getCarLoadCode";
    private static final int REQUEST_CODE_SCAN = 0;
    private String mV_no;
    private Button mBtn_ok;
    private TextView mTv_result;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.getcarloadcode);
        Intent intent = getIntent();
        mV_no = intent.getStringExtra("V_NO");
        Log.d(TAG, "车牌："+mV_no);
        Bitmap logo = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher);
        Bitmap bitmap = CodeCreator.createQRCode(mV_no, 400, 400, logo);
        ImageView iv = findViewById(R.id.iv_QR);
        iv.setImageBitmap(bitmap);

        mBtn_ok = findViewById(R.id.btn_ok);
        mTv_result = findViewById(R.id.tv_result);

        mBtn_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                go(v);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // 扫描二维码/条码回传
        if (requestCode == REQUEST_CODE_SCAN && resultCode == RESULT_OK) {
            if (data != null) {

                String content = data.getStringExtra(Constant.CODED_CONTENT);
                mTv_result.setText("扫描结果为：" + content);
            }
        }
    }

    public void go(View view) {
        Intent intent = new Intent(new Intent(this, CaptureActivity.class));
        startActivityForResult(intent, REQUEST_CODE_SCAN);
    }

}
