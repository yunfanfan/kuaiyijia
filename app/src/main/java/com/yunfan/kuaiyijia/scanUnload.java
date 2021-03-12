package com.yunfan.kuaiyijia;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.yzq.zxinglibrary.android.CaptureActivity;
import com.yzq.zxinglibrary.common.Constant;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * 扫码卸货
 * 司机点击扫码卸货，扫描订单上的二维码，进行绑定，同时更改订单状态
 * 修改
 */
public class scanUnload extends Activity {

    private static final int REQUEST_CODE_SCAN = 0;
    private static final String TAG = "scanUnload";
    private Button mBtScanUnload;
    private String mResultScan = null;
    private boolean bIsOrderNum;
    private Handler mHandler = new Handler() {
        private int mUpdateResult;
        @Override
        public void handleMessage(Message msg) {

            switch(msg.what){
                case 1 :
                    bIsOrderNum = msg.getData().getBoolean("isOrderNum");
                    if (!bIsOrderNum) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(scanUnload.this);
                        builder.setTitle("提醒！")
                                .setMessage("无此订单码，请重新扫描..")
                                .setPositiveButton("确认", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog,
                                                        int which) {
                                    }
                                })
                                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog,
                                                        int which) {
                                    }
                                })
                                .show();

                    }
                    else {
                        //是订单码，开始更新数据库对应字段状态值
                        updateOrderStatus();
                    }
                    break;
                case 2 :
                    //接收修改订单状态后的结果
                    mUpdateResult = msg.getData().getInt("updateResulat");
                    Log.i("lgq","id: "+ mUpdateResult);
                    if (mUpdateResult == 1) {
                        Log.d(TAG, "run: 返回值为1");
                        Toast.makeText(scanUnload.this, "修改订单状态成功..", Toast.LENGTH_SHORT).show();
                    }
                    else {
                        Toast.makeText(scanUnload.this, "修改订单状态失败..", Toast.LENGTH_SHORT).show();
                    }
                    break;

            }
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.scanunload);

        mBtScanUnload = findViewById(R.id.bt_scanUnload);

        mBtScanUnload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //先扫码，判断是否是订单码
                scan(v);
                if (mResultScan != null) {
                    //进入数据库查询是否是订单码
                    //注意：此处表名等数据库相关数据名称为参考keg_logistic库中数据
                    //     之后需做相应修改。

                    String tabName = "orders";
                    String tabTopName = "order_number";
                    String value = mResultScan;
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            Message msgIsOrderNum = new Message();
                            ResultSet rs = database.SelectFromData("*", tabName, tabTopName, value);
                            try {
                                msgIsOrderNum.what = 1;
                                Bundle bundleIsOrderNum = new Bundle();
                                bundleIsOrderNum.putBoolean("isOrderNum", rs.isBeforeFirst());
                                msgIsOrderNum.setData(bundleIsOrderNum);
                                mHandler.sendMessage(msgIsOrderNum);

                            } catch (SQLException e) {
                                e.printStackTrace();
                            }
                        }
                    }).start();
                    //database.closeConnect();
                }
            }
        });
    }
    //如果是订单码，就要修改订单状态
    public void updateOrderStatus() {
        String TabName = "orders";
        String ID_name = "order_status";
        int ID_value = 5;

        String[] columns = {"order_number"};
        String[] values = {mResultScan};
        new Thread(new Runnable() {
            @Override
            public void run() {
                int result = database.updateForData(TabName, ID_name, ID_value, columns, values);
                Message msgResultOfUpdate = new Message();
                msgResultOfUpdate.what = 2;
                Bundle bundle = new Bundle();
                bundle.putInt("updateResult", result);
                msgResultOfUpdate.setData(bundle);
                mHandler.sendMessage(msgResultOfUpdate);
            }
        }).start();
        //database.closeConnect();

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
