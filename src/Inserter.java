import java.sql.*;

public class Inserter {
    // JDBC驱动名以及数据库URL
    static final String JDBC_DRIVER = "com.mysql.cj.jdbc.Driver";
    static final String DB_URL =
            "jdbc:mysql://localhost:3306/innovation?useSSL=false&serverTimezone=Asia/Shanghai&allowPublicKeyRetrieval=true";

    // 数据库用户名及密码
    static final String USERNAME = "inserter";
    static final String PASSWORD = "a#8Mz1uTe^ERTP2S";

    // 数据库插入函数
    public static Boolean input_mysql(String content, String address, String time, Integer cla){
        // 初始化MySQL驱动程序
        try{
            Class.forName(JDBC_DRIVER);
        }
        catch (ClassNotFoundException e) {
            System.err.println("加载MySQL驱动时发生错误，请检查驱动jar包是否正确导入。");
            System.err.println("错误详细信息：" + e.toString());
            return false;
        }

        // 创建连接
        Connection conn;
        try{
            conn = DriverManager.getConnection(DB_URL, USERNAME, PASSWORD);
        }
        catch(SQLException e){
            System.err.println("与数据库建立连接时发生错误，请检查程序与数据库状态是否正确设置。");
            System.err.println("错误详细信息：" + e.toString());
            return false;
        }

        // 准备SQL语句
        PreparedStatement pstm;
        try{
            pstm = conn.prepareStatement(
                    "INSERT INTO messages(msg_content, msg_id, msg_address, msg_time, msg_class)" +
                            "VALUES(?, NULL, ?, ?, ?)");
        }
        catch(SQLException e){
            System.err.println("准备SQL语句时发生错误，请检查SQL语句是否正确编写。");
            System.err.println("错误详细信息：" + e.toString());
            try{
                conn.close();
            }
            catch(SQLException ec){
                System.err.println("关闭Connection时发生错误，请检查程序和服务器的运行情况。");
                System.err.println("错误详细信息：" + e.toString());
            }
            return false;
        }

        // 将字符串类型的时间转化为一个Timestamp类型的时间戳
        Timestamp ts;
        try{
            ts = Timestamp.valueOf(time);
        }
        catch(IllegalArgumentException e){
            System.err.println("传入的时间参数具有错误的格式，请确保参数格式为yyyy-mm-dd hh:mm:ss。");
            try{
                pstm.close();
                conn.close();
            }
            catch(SQLException ec){
                System.err.println("关闭PreparedStatement和Connection时发生错误，请检查程序和服务器的运行情况。");
                System.err.println("错误详细信息：" + e.toString());
            }
            return false;
        }

        // 设置语句参数
        try{
            pstm.setString(1, content);
            pstm.setString(2, address);
            pstm.setTimestamp(3, ts);
            pstm.setInt(4, cla);
        }
        catch(SQLException e){
            System.err.println("设置语句参数时发生错误，请检查SQL语句以及参数是否正确设置。");
            System.err.println("错误详细信息：" + e.toString());
            try{
                pstm.close();
                conn.close();
            }
            catch(SQLException ec){
                System.err.println("关闭PreparedStatement和Connection时发生错误，请检查程序和服务器的运行情况。");
                System.err.println("错误详细信息：" + e.toString());
            }
            return false;
        }

        // 执行语句，插入数据
        try{
            pstm.executeUpdate();
        }
        catch(SQLException e){
            if(!e.getSQLState().equals("45000")){
                System.err.println("INSERT语句执行时发生错误，请检查程序和服务器的运行情况。");
                System.err.println("错误详细信息：" + e.toString());
            }
            try{
                pstm.close();
                conn.close();
            }
            catch(SQLException ec){
                System.err.println("关闭PreparedStatement和Connection时发生错误，请检查程序和服务器的运行情况。");
                System.err.println("错误详细信息：" + e.toString());
            }
            return false;
        }

        // 回收资源，返回执行结果
        try{
            pstm.close();
            conn.close();
        }
        catch(SQLException e){
            System.err.println("关闭PreparedStatement和Connection时发生错误，请检查程序和服务器的运行情况。");
            System.err.println("错误详细信息：" + e.toString());
            return true;
        }
        return true;
    }

}
