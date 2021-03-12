package com.yunfan.kuaiyijia;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.SQLException;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.yzq.zxinglibrary.android.CaptureActivity;
import com.yzq.zxinglibrary.common.Constant;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.Date;
import java.util.jar.JarFile;

//扫码装车
public class scanCarLoad extends Activity {

    private static final int REQUEST_CODE_SCAN = 1;
    private static final String TAG = "scanCarLoad";
    private Button mBtScan;
    private String mResultScan = null;
    private Timestamp mTimeStamp;
    private int mColumnNum;
    private boolean mFlag;
    private int mUpdateResult;
    
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case 1 :
                    //接受msg传递过来的参数
                    mColumnNum = msg.getData().getInt("columnNum");
                    Log.i("lgq","id: "+ mColumnNum);
                    if (mColumnNum == 1) {
                        Log.d(TAG, "run: 返回值为1");
                        Toast.makeText(scanCarLoad.this, "绑定成功..", Toast.LENGTH_SHORT).show();
                        //成功后，5.依次扫描每个商品条形码，绑定搬运人员和货物
                        //5.1
                        bindPorterCargoInsert(getWindow().getDecorView());
                    }
                    else {
                        Toast.makeText(scanCarLoad.this, "绑定失败..", Toast.LENGTH_SHORT).show();
                    }
                    break;
                case 2:
                    boolean isNullResultSet = msg.getData().getBoolean("isNullResultSet");
                    Log.i(TAG, "handleMessage: isNullResultSet" + isNullResultSet);
                    //如果查询结果为空就要重新输入车牌
                    if (!isNullResultSet) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(scanCarLoad.this);
                        builder.setTitle("提醒！")
                                .setMessage("无此装车码，请重新扫码..")
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
                    //是的话，4.就去执行搬运人员和车辆的绑定操作
                    else {
                        bindCarPorter();
                    }
                case 3 :
                    mColumnNum = msg.getData().getInt("columnNum");//接受msg传递过来的参数
                    Log.i("lgq","id: "+ mColumnNum);
                    if (mColumnNum == 1) {
                        Log.d(TAG, "run: 返回值为1");
                        Toast.makeText(scanCarLoad.this, "添加成功..", Toast.LENGTH_SHORT).show();
                    }
                    else {
                        Toast.makeText(scanCarLoad.this, "添加失败..", Toast.LENGTH_SHORT).show();
                    }
                    break;
                case 4 :
                    //接受msg传递过来的参数
                    mUpdateResult = msg.getData().getInt("updateResult");
                    Log.i("lgq","id: "+ mUpdateResult);
                    if (mUpdateResult == 1) {
                        Log.d(TAG, "run: 返回值为1");
                        Toast.makeText(scanCarLoad.this, "更改状态成功..", Toast.LENGTH_SHORT).show();
                    }
                    else {
                        Toast.makeText(scanCarLoad.this, "更改状态失败..", Toast.LENGTH_SHORT).show();
                    }
                    break;

            }
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.scancarload);
        Date date = new Date();
        mTimeStamp = new Timestamp(date.getTime());

        mBtScan = findViewById(R.id.bt_scanCarLoad);
        mBtScan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                scan(v);//1. 扫码装车码
            }
        });
    }


    public void scan(View view) {
        Intent intent = new Intent(new Intent(this, CaptureActivity.class));
        startActivityForResult(intent, REQUEST_CODE_SCAN);
        while (mResultScan != null) {
            bindDialog(view);//2. 判断是否绑定当前车辆
        }
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

    public void bindDialog(View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("提醒！")
                .setMessage("是否绑定当前车辆？")
                .setPositiveButton("确认", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog,
                                        int which) {
                        //3. 去数据库查询是否是装车码
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                Message msgCarInfo = new Message();
                                Message msgCarIsNull = new Message();
                                String tabName = "PUB_VEHICLE";
                                String tabTopName = "V_NO";
                                String value = mResultScan;
                                ResultSet rs = database.SelectFromData("*", tabName, tabTopName, value);
                                try {
                                    //对本次查询判空，传递消息出去
                                    msgCarIsNull.what = 2;
                                    Bundle bCarIsNull = new Bundle();
                                    bCarIsNull.putBoolean("isNullResultSet",rs.isBeforeFirst());
                                    msgCarIsNull.setData(bCarIsNull);
                                    mHandler.sendMessage(msgCarIsNull);

                                } catch (SQLException | java.sql.SQLException e) {
                                    e.printStackTrace();
                                }
                                mHandler.sendMessage(msgCarInfo);
                            }
                        }).start();
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
    public void bindCarPorter() {
        String sql = "insert into PUB_CARPORTER(id, V_NO, PORTER_ID) values(?,?,?)";

        new Thread(new Runnable() {
            @Override
            public void run() {
                Message msg = new Message();
                msg.what = 1;
                Bundle bundle = new Bundle();
                Log.d(TAG, "run: 进入线程");
                Connection conn = database.getSQLConnection();
                try {
                    PreparedStatement ps = conn.prepareStatement(sql);
                    ps.setInt(1,1);
                    ps.setString(2,mResultScan);
                    ps.setInt(3,1);
                    bundle.putInt("columnNum", ps.executeUpdate());
                    msg.setData(bundle);
                    Log.d(TAG, "run: sql执行完毕");
                    conn.close();
                } catch (SQLException | java.sql.SQLException throwables) {
                    throwables.printStackTrace();
                }
                mHandler.sendMessage(msg);
            }
        }).start();

    }
    public void bindPorterCargoInsert(View v) {
        mFlag = true;
        while(mFlag) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("提醒！")
                    .setMessage("是否开始扫描商品条码？")
                    .setPositiveButton("确认", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog,
                                            int which) {
                            scan(v);
                            //5.1 先把商品条码insert在`order_huowu_code`中
                            new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    String sql = "insert into order_huowu_code(id, code, create_date) values(?,?,?)";
                                    Message msg = new Message();
                                    msg.what = 3;
                                    Bundle bundle = new Bundle();
                                    Connection conn = database.getSQLConnection();
                                    try {
                                        PreparedStatement ps = conn.prepareStatement(sql);
                                        ps.setInt(1,1);
                                        ps.setString(2,mResultScan);
                                        ps.setTimestamp(3,mTimeStamp);
                                        bundle.putInt("columnNum", ps.executeUpdate());
                                        msg.setData(bundle);
                                        Log.d(TAG, "run: sql执行完毕");
                                        conn.close();
                                    } catch (SQLException | java.sql.SQLException throwables) {
                                        throwables.printStackTrace();
                                    }
                                    mHandler.sendMessage(msg);
                                }
                            }).start();
                        }
                    })
                    .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog,
                                            int which) {
                            mFlag = false;
                        }
                    })
                    .show();

        }
    }
    public void bindPorterCargoUpdate(View v) {
        //修改数据库代码
        String TabName = "order_huowus";
        String ID_name = "PORTER_ID";
        int ID_value = 1;

        String[] columns = {"code_path"};
        String[] values = {mResultScan};
        new Thread(new Runnable() {
            @Override
            public void run() {
                int result = database.updateForData(TabName, ID_name, ID_value, columns, values);
                Message msg = new Message();
                msg.what = 4;
                Bundle bundle = new Bundle();
                bundle.putInt("updateResult", result);
                msg.setData(bundle);
                mHandler.sendMessage(msg);

            }
        }).start();

    }
}
