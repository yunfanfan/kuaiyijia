package com.yunfan.kuaiyijia;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;


import org.w3c.dom.Text;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class searchCarNo extends Activity {

    private static final String TAG = "zhuangchema";
    private EditText mCarId;
    private Button mBtnSearch;
    private TextView mInfoCarId;
    private TextView mInfoCarType;
    private TextView mInfoCarCapacity;
    private TextView mInfoCarDimension;
    private String mV_id;
    private String mV_no;
    private String mV_lenght;
    private String mV_width;
    private String mV_height;
    private Button mBtnCarLoadCode;

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch(msg.what){
                case 1 :
                    mV_id = msg.getData().getString("mV_id");//接受msg传递过来的参数
                    mV_no = msg.getData().getString("mV_no");//接受msg传递过来的参数
                    mV_lenght = msg.getData().getString("mV_lenght");//接受msg传递过来的参数
                    mV_width = msg.getData().getString("mV_width");//接受msg传递过来的参数
                    mV_height = msg.getData().getString("mV_height");//接受msg传递过来的参数
                    Log.i("lgq","id: "+mV_no);
                    mInfoCarId.setText("车牌号：" + mV_no);
                    mInfoCarDimension.setText("货箱长宽高:" + mV_lenght + "m × "+ mV_width + "m × "+ mV_height + "m");
                    break;
            }
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.zhuangchema);

        Intent i = new Intent(this, getCarLoadCode.class);

        initView();

        //搜索车牌号
        mBtnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    handlerSearch(v);
                } catch (SQLException throwables) {
                    throwables.printStackTrace();
                }
            }
        });

        //生成装车码
        mBtnCarLoadCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "准备生成装车码...");
                i.putExtra("V_NO",mV_no);
                startActivity(i);
                Log.d(TAG, "开始跳转页面...");
            }
        });
    }

    private void initView() {
        mCarId = this.findViewById(R.id.carId);
        mBtnSearch = this.findViewById(R.id.search);
        mInfoCarId = this.findViewById(R.id.info_carId);
        mInfoCarType = this.findViewById(R.id.info_carType);
        mInfoCarCapacity = this.findViewById(R.id.info_carCapacity);
        mInfoCarDimension = this.findViewById(R.id.info_carDimension);
        mBtnCarLoadCode = this.findViewById(R.id.btn_carLoadCode);
    }

    private void handlerSearch(View v) throws SQLException {
        String carId = mCarId.getText().toString();
        if (TextUtils.isEmpty(carId)) {
            Toast.makeText(this, "车牌号不可以为空..", Toast.LENGTH_SHORT).show();
            return;
        }
        String tabName = "PUB_VEHICLE";
        String tabTopName = "V_NO";
        //value值之后从输入框中得到
        //String value = carId;
        String value = "渝A66666";
        new Thread(new Runnable() {
            @Override
            public void run() {
                Message msg = new Message();
                ResultSet rs = database.SelectFromData("*", tabName, tabTopName, value);
                try {
                    msg.what = 1;
                    Bundle bundle = new Bundle();
                    while (rs.next()) {
                        bundle.putString("mV_id", rs.getString("V_ID"));
                        bundle.putString("mV_no", rs.getString("V_NO"));
                        bundle.putString("mV_lenght", rs.getString("HC_LENGHT"));
                        bundle.putString("mV_width", rs.getString("HC_WIDTH"));
                        bundle.putString("mV_height", rs.getString("HC_HEIGHT"));
                        msg.setData(bundle);
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                mHandler.sendMessage(msg);
            }
        }).start();
        database.closeConnect();
    }
}
