package com.yunfan.kuaiyijia;

import android.util.Log;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class database {
    private static final String TAG = "database";
    private static String user = "sa";
    private static String password = "root123";
    private static String DatabaseName = "CQU";
    private static String IP = "172.20.53.32";
    /**
     * 连接字符串
     */
    private static String connectDB = "jdbc:jtds:sqlserver://" + IP + ":1433/" + DatabaseName + ";useunicode=true;characterEncoding=UTF-8";

    private static Connection conn = null;
    private static Statement stmt = null;

    /**
     * 链接数据库
     *
     * @return
     */
    private static Connection getSQLConnection() {
        Connection con = null;
        try {
            //加载驱动换成这个
            Class.forName("net.sourceforge.jtds.jdbc.Driver");
            //连接数据库对象
            con = DriverManager.getConnection(connectDB, user,
                    password);
        } catch (Exception e) {
        }
        return con;
    }

    /**
     * 向服务器数据库插入数据
     * @tabName 要插入的表名
     * @tabTopName 要插入的字段名字符串，例如（name,password,age）
     * @values 与tabTopName 中 字段名一一对应的值。一次插入多跳数据，可以用逗号隔开。例如（"张三"，"zhangsan","24"），（"李四"，"lisi","26"）
     */
    public static int insertIntoData(String tabName, String tabTopName, String values) {
        int i = 0;
        try {
            if (conn == null) {
                conn = getSQLConnection();
                stmt = conn.createStatement();
            }
            if (conn == null || stmt == null) {
                return i;
            }
            String sql = "insert into " + tabName + tabTopName + " values " + values;
            i = stmt.executeUpdate(sql);
        } catch (SQLException e) {
            e.printStackTrace();
            Log.i("mtj", "同步数据库表【" + tabName + "】失败。");
        }
        return i;
    }
    public static ResultSet SelectFromData(String first, String tabName, String tabTopName, String values) {
        //int i = 0;
        ResultSet rs = null;
        try {
            if (conn == null) {
                conn = getSQLConnection();
                stmt = conn.createStatement();
            }
            if (conn == null || stmt == null) {
            }
            String sql = "SELECT " + first + " FROM " + tabName + " WHERE " + tabTopName + " = '" + values + "'";
            Log.d(TAG, "sql" + sql);
            rs = stmt.executeQuery(sql);
        } catch (SQLException e) {
            e.printStackTrace();
            Log.i("mtj", "查询数据表【" + tabName + "】失败。");
        }
        return rs;
    }
    public static int deleteFromData(String tabName, String tabTopName, String values) {
        int i = 0;
        try {
            if (conn == null) {
                conn = getSQLConnection();
                stmt = conn.createStatement();
            }
            if (conn == null || stmt == null) {
                return i;
            }
            String sql = "DELETE FROM " + tabName + " WHERE " + tabTopName + " = '" + values + "'";
            i = stmt.executeUpdate(sql);
        } catch (SQLException e) {
            e.printStackTrace();
            Log.i("mtj", "同步数据库表【" + tabName + "】失败。");
        }
        return i;
    }
    /**
     * 关闭数据库链接
     */
    public static void closeConnect() {
        if (stmt != null) {
            try {
                stmt.close();
                stmt = null;
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        if (conn != null) {
            try {
                conn.close();
                conn = null;
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

}


