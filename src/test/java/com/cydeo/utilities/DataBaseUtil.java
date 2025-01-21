package com.cydeo.utilities;



import java.sql.*;
import java.util.LinkedHashMap;
import java.util.Map;

public class DataBaseUtil {

    private static Connection con;
    private static Statement stm;
    private static ResultSet rs;
    private static ResultSetMetaData rsmd;


    public static void connectToDatabase(String url, String user, String password) {

        try {
            con = DriverManager.getConnection(url, user, password);
            System.out.println("Connected to database");

        } catch (SQLException e) {
            System.out.println("Failed to connect database " + e.getMessage());

        }
    }

    public static void connectToDatabase() {
        String url = ConfigurationReader.getProperty("library2.db.url");
        String user = ConfigurationReader.getProperty("library2.db.username");
        String password = ConfigurationReader.getProperty("library2.db.password");

        connectToDatabase(url, user, password);
    }

    public static ResultSet executeQuery(String sql) {

        try {
            stm = con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            rs = stm.executeQuery(sql);
            rsmd = rs.getMetaData();

        } catch (SQLException e) {
            System.out.println("Failed to execute query " + e.getMessage());
        }

        return rs;
    }

    public static void closeConnection() {

        try{
        if (con != null) rs.close();
        if (stm != null) stm.close();
        if (con != null) con.close();
            } catch (SQLException e) {
                System.out.println("Failed to close connection " + e.getMessage());
            }
        }



public static int getColumnCount() {
        int coulumnCount = 0;

    try {
        coulumnCount=rsmd.getColumnCount();
    } catch (SQLException e) {
        System.out.println("Failed to get column count " + e.getMessage());
    }

    return coulumnCount;
}

public static Map<String,Object> getBookInfo(ResultSet rs) {

    Map<String, Object> bookMap = null;
    try {
        rs.next();

        bookMap = new LinkedHashMap<>();

        bookMap.put("id", rs.getString(1));
        bookMap.put("name", rs.getString(2));
        bookMap.put("isbn", rs.getString(3));
        bookMap.put("year", rs.getString(4));
        bookMap.put("author", rs.getString(5));
        bookMap.put("book_category_id", rs.getString(6));
        bookMap.put("description", rs.getString(7));
        bookMap.put("added_date", rs.getString(8));

    } catch (SQLException e) {
        System.out.println("Failed to get book info " + e.getMessage());
    }


    return bookMap;
}

public static Map<String,String> getDataTable(ResultSet rs) {

    Map<String,String> map = new LinkedHashMap<>();

   try {

       rs.next();

       for (int column = 1; column <= rs.getMetaData().getColumnCount(); column++) {
           for (int columnValue = 1; columnValue <= rs.getMetaData().getColumnCount(); columnValue++) {
               map.put(rs.getMetaData().getColumnName(columnValue), rs.getString(columnValue));
           }
       }
   }catch (SQLException e) {
       System.out.println("Failed to get data table " + e.getMessage());
   }

   return map;
   }




}
