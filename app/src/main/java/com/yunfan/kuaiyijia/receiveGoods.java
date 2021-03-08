package com.yunfan.kuaiyijia;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import androidx.annotation.Nullable;

import com.yzq.zxinglibrary.android.CaptureActivity;
import com.yzq.zxinglibrary.common.Constant;

import java.sql.ResultSet;
import java.sql.SQLException;

public class receiveGoods extends Activity {

    private static final int REQUEST_CODE_SCAN = 0;
    private Button mBtReceiveGoods;
    private String mResultScan;
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch(msg.what){
                case 1 :
                    //接收订单查询结果
                    //mV_id = msg.getData().getString("mV_id");//接受msg传递过来的参数
                    Log.i("lgq","id: ");
                    //查询成功，接单，修改状态为接单
                    //然后进入选择运价页面，新建一个activity，跳转
                    //下一个运价界面有个按钮，扫描商品条码，每扫完一个，就选择对应运价。
                    break;
            }
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.receivegoods);

        mBtReceiveGoods = findViewById(R.id.bt_receivegoods);

        mBtReceiveGoods.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                scan(v);
                //扫码之后要查询结果是否是订单二维码
                //如果是，就执行数据库操作，接单
                //如果不是，就提示无此订单二维码。
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        String tabName = "";
                        String tabTopName = "";
                        String value = mResultScan;

                        Message msg = new Message();
                        ResultSet rs = database.SelectFromData("*", tabName, tabTopName, value);
                        try {
                            msg.what = 1;
                            Bundle bundle = new Bundle();

                            while (rs.next()) {
                                //返回一个值，代表查询成功
                                bundle.putString("", rs.getString(""));
                                msg.setData(bundle);
                            }
                        } catch (SQLException e) {
                            e.printStackTrace();
                        }
                        mHandler.sendMessage(msg);
                    }
                }).start();
            }
        });
    }
    public void scan(View view) {
        Intent intent = new Intent(new Intent(this, CaptureActivity.class));
        startActivityForResult(intent, REQUEST_CODE_SCAN);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // 扫描二维码/条码回传
        if (requestCode == REQUEST_CODE_SCAN && resultCode == RESULT_OK) {
            if (data != null) {

                mResultScan = data.getStringExtra(Constant.CODED_CONTENT);

            }
        }
    }
}
