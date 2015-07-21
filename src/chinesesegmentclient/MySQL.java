/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package chinesesegmentclient;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 *
 * @author zoo88115
 */
public class MySQL {

    private Connection con = null; //Database objects 
    //連接object 
    private Statement stat = null;
    //執行,傳入之sql為完整字串 
    private ResultSet rs = null;
    //結果集 
    private PreparedStatement pst = null;
    //執行,傳入之sql為預儲之字申,需要傳入變數之位置 
    //先利用?來做標示 
    //下面目前用不到
//    private String dropdbSQL = "DROP TABLE User "; 
//  
//    private String createdbSQL = "CREATE TABLE User (" + 
//      "    id     INTEGER " + 
//      "  , name    VARCHAR(20) " + 
//      "  , passwd  VARCHAR(20))"; 

    public MySQL(String address, String db, String account, String password) {
        String dbAddress = "jdbc:mysql://" + address + "/" + db + "?useUnicode=true&characterEncoding=Big5";
        try {
            Class.forName("com.mysql.jdbc.Driver");
            //註冊driver 
            con = DriverManager.getConnection(dbAddress, account, password);
            //取得connection

            //jdbc:mysql://localhost/test?useUnicode=true&characterEncoding=Big5
            //localhost是主機名,test是database名
            //useUnicode=true&characterEncoding=Big5使用的編碼 
        } catch (ClassNotFoundException e) {
            System.out.println("DriverClassNotFound :" + e.toString());
        }//有可能會產生sqlexception 
        catch (SQLException x) {
            System.out.println("Exception :" + x.toString());
        }
    }
    //新增資料 
    //可以看看PrepareStatement的使用方式 

    private void insertTable(JSONObject j) throws Exception {
        try {
            JSONArray jsonArray=j.getJSONArray("insert");
            for (int l = 0; l < jsonArray.length(); l++) {
                String table = j.getString("table");//新增至哪個資料表
                JSONArray columns = j.getJSONArray("columns");//欄位
                JSONArray values = jsonArray.getJSONArray(l);//值
                if(columns.length()!=values.length())//如果值跟欄位不相符 執行下一筆
                    continue;
                String insertdbSQL = "insert into ";
                insertdbSQL = insertdbSQL + table + "(";
                int i;
                for (i = 0; i < columns.length() - 1; i++) {
                    insertdbSQL = insertdbSQL + columns.get(i).toString() + ",";
                }
                insertdbSQL = insertdbSQL + columns.get(i).toString() + ") VALUES (";
                for (i = 0; i < values.length() - 1; i++) {
                    insertdbSQL = insertdbSQL + "'" + values.get(i).toString() + "'" + ",";
                }
                insertdbSQL = insertdbSQL + "'" + values.get(i).toString() + "'" + ")";
                System.out.println(insertdbSQL);
                pst = con.prepareStatement(insertdbSQL);
                pst.executeUpdate();
            }
        } catch (SQLException e) {
            System.out.println("InsertDB Exception :" + e.toString());
        } finally {
            Close();
        }
    }

    //查詢資料 
    //可以看看回傳結果集及取得資料方式 
    private JSONObject SelectTable(JSONObject jsonObject)throws Exception {
        try {
            String selectSQL=jsonObject.getString("sqlCommand");
            JSONArray keys=jsonObject.getJSONArray("keys");
            
            JSONArray jsonArray = new JSONArray();
            JSONObject result = new JSONObject();
            stat = con.createStatement();
            rs = stat.executeQuery(selectSQL);
            while (rs.next()) {
                HashMap<String, String> hashMap = new HashMap<String, String>();
                for (int i = 0; i < keys.length(); i++) {
                    hashMap.put(keys.getString(i), rs.getString(keys.getString(i)));
                }
                jsonArray.put(hashMap);
            }
            try {
                result.put("RESULT", jsonArray);
                return result;
            } catch (Exception e) {
                return null;
            }
        } catch (SQLException e) {
            System.out.println("DropDB Exception :" + e.toString());
            return null;
        } finally {
            Close();
        }
    }
    //完整使用完資料庫後,記得要關閉所有Object 
    //否則在等待Timeout時,可能會有Connection poor的狀況 

    private void Close() {
        try {
            if (rs != null) {
                rs.close();
                rs = null;
            }
            if (stat != null) {
                stat.close();
                stat = null;
            }
            if (pst != null) {
                pst.close();
                pst = null;
            }
        } catch (SQLException e) {
            System.out.println("Close Exception :" + e.toString());
        }
    }

    public JSONObject decode(JSONObject jsonObject) throws Exception {
        try {
            if (jsonObject.getString("title").equals("INSERT") == true) {
                insertTable(jsonObject);
            } else if (jsonObject.getString("title").equals("SELECT") == true) {
                return SelectTable(jsonObject);
            }
            return null;
        } catch (Exception e) {
            return null;
        }
    }
}