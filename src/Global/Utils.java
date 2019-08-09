package Global;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.text.SimpleDateFormat;

public class Utils
{
	//全局静态类的静态成员
	public static Utils utils=new Utils();
	
	Connection conn = null;
	
	//默认构造函数
	Utils()
	{
		try
	    {
			// 打开链接
			SendSystemMessage("开始连接数据库");
	    	// 注册 JDBC 驱动
		    Class.forName("com.mysql.cj.jdbc.Driver");
			conn = DriverManager.getConnection(MySqlPara.global_mp.SQLAddress,MySqlPara.global_mp.SQLAccount,MySqlPara.global_mp.SQLPassword);
			if(!conn.isClosed())
				SendSystemMessage("连接数据库成功！");
			else
				SendSystemMessage("连接数据库失败！");
		}
	    catch (Exception e)
	    {
	    	HandleException(e);
		}
	}
	
	//获取当前系统时间
	SimpleDateFormat df=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	public String GetCurrentTime()
	{
        return df.format(GetTimeByLong());
	}
	
	//获取Long类型的时间，距离1970年1月1日起的毫秒数
	public long GetTimeByLong()
	{
		return System.currentTimeMillis();
	}
	
	//写入originmessage
	public void WriteOriginMessage(String mess)
	{
		PreparedStatement pre=null;
		try{
			pre=conn.prepareStatement("insert into "+MySqlPara.global_mp.OriginalMessage_TableName+" values(?,?)");
			pre.setString(1, GetCurrentTime());
			pre.setString(2, mess);
			pre.executeUpdate();
			SendSystemMessage("原始消息："+mess);
		} catch (Exception e) {
			Utils.utils.HandleException(e);
		}
	}
	
	//写入controlmessage
	public void WriteControlMessage(ReceiveDataStruct ds)
	{
		PreparedStatement pre=null;
		try
		{
			pre=conn.prepareStatement("insert into "+MySqlPara.global_mp.ControlMessage_TableName+" values(?,?,?)");
			pre.setString(1, GetCurrentTime());
			pre.setString(2, ds.getMess());
			pre.setString(3, "0");
			pre.executeUpdate();
			SendSystemMessage("写入数据库"+MySqlPara.global_mp.DataMessage_TableName+"：控制信息："+ds.getMess());
		}
		catch (Exception e)
		{
			Utils.utils.HandleException(e);
		}
	}
	
	//解析从手机APP发来的字符串信息
	public boolean AnalyzeMessage(String str,ReceiveDataStruct ds)
	{
		if(!str.contains("control:"))
			return false;
		String mess=str.substring(str.indexOf("control:")+"control:".length());
		ds.setMess(mess);
		SendSystemMessage("消息解析完成！");
		return true;
	}
	
	//发送系统消息
	public void SendSystemMessage(String str)
	{
		System.out.println(GetCurrentTime());
		System.out.println(str);
		System.out.println();
	}
	
	//统一异常处理
	public void HandleException(Exception e)
	{
		SendSystemMessage("捕获异常："+e.toString().substring(0, e.toString().indexOf(":")));
	}
}
