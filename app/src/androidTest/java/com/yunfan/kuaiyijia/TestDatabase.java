package com.yunfan.kuaiyijia;
import android.content.Context;
import android.util.Log;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.concurrent.BlockingDeque;


@RunWith(AndroidJUnit4.class)

public class TestDatabase {
    private static final String TAG = "TestDatabase";

    @Test
    public void testInsert() {
        String tabName = "PUB_VEHICLE";
        String tabTopName = "(V_ID, V_NO, HC_LENGHT, HC_WIDTH, HC_HEIGHT, STATUS)";
        String value = "('2', '渝A66667', '2', '1', '1', '1')";
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
    public void testSelect() {
        String tabName = "PUB_VEHICLE";
        String tabTopName = "V_NO";
        String value = "渝A66666";
        database.SelectFromData("*", tabName, tabTopName, value);

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
}
