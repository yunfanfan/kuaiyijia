package com.yunfan.kuaiyijia;
import android.content.Context;
import android.util.Log;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.BlockingDeque;


@RunWith(AndroidJUnit4.class)

public class TestDatabase {
    private static final String TAG = "TestDatabase";

    @Test
    public void testInsert() {
        String tabName = "PUB_VEHICLE";
        String tabTopName = "(V_ID, V_NO, HC_LENGHT, HC_WIDTH, HC_HEIGHT, STATUS)";
        //String value = "('2', '渝A66667', '2', '1', '1', '1')";
        String value = "(?,?,?,?,?,?)";
        database.insertIntoData(tabName, tabTopName, value);
        database.closeConnect();

    }
    @Test
    public void testTestSelect() throws SQLException {
        String tabName = "PUB_VEHICLE";
        String tabTopName = "V_NO";
        String value = "渝A66666";
        ResultSet rs = database.SelectFromData("*", tabName, tabTopName, value);
        while (rs.next()) {
            String v_id = rs.getString("V_ID");
            Log.d(TAG, "testTestSelect: " + v_id);
        }

        database.closeConnect();

    }
    @Test
    public void testSelect() throws SQLException {
        String tabName = "PUB_VEHICLE";
        String tabTopName = "V_NO";
        String value = "渝A66667";
        ResultSet rs = database.SelectFromData("*", tabName, tabTopName, value);
        Log.d(TAG, "testSelect: "+rs.isBeforeFirst());

        database.closeConnect();

    }
    @Test
    public void testDelete() {
        String tabName = "PUB_VEHICLE";
        String tabTopName = "V_ID";
        String value = "1";
        database.deleteFromData(tabName, tabTopName, value);
        database.closeConnect();

    }
    @Test
    public void testSetInsert() {
        int mYear;
        int mMonth;
        int mDay;
        Calendar calendar = Calendar.getInstance();
        mYear = calendar.get(Calendar.YEAR);
        mMonth = calendar.get(Calendar.MONTH)+1;
        mDay = calendar.get(Calendar.DAY_OF_MONTH);
        String ti_rq = mYear + "-" + mMonth + "-" + mDay;
        Date date = new Date();
        Timestamp timeStamp = new Timestamp(date.getTime());
        String sql = "insert into XS_TRAN_INFO(TI_ID, C_ID, HYBID, TI_RQ, L_ID, B_ID, V_ID, EM_ID, STIME, ETIME, STATUS, RESULT, RESULTMSG) values(?,?,?,?,?,?,?,?,?,?,?,?,?)";
        Connection conn = database.getSQLConnection();
        try {
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1,2);
            ps.setInt(2,1);
            ps.setInt(3,1);
            ps.setString(4,ti_rq);
            ps.setInt(5,1);
            ps.setInt(6,1);
            //车辆id
            ps.setInt(7,1);
            //司机id
            ps.setInt(8,1);
            ps.setTimestamp(9,timeStamp);
            ps.setTimestamp(10,timeStamp);
            ps.setInt(11,0);
            ps.setInt(12,0);
            ps.setString(13, "ok");
            ps.executeQuery();
            System.out.println("添加成功！");
            conn.close();

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }
}
