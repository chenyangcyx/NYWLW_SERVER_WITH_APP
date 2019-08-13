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
	
	StringBuilder send_mess=new StringBuilder();
	String sep=System.getProperty("line.separator");
	
	//默认构造函数
	Utils()
	{
		try
	    {
			// 打开链接
			RecordSystemMessage("开始连接数据库");
			SendSystemMessage();
	    	// 注册 JDBC 驱动
		    Class.forName("com.mysql.cj.jdbc.Driver");
			conn = DriverManager.getConnection(MySqlPara.global_mp.SQLAddress,MySqlPara.global_mp.SQLAccount,MySqlPara.global_mp.SQLPassword);
			if(!conn.isClosed())
				RecordSystemMessage("连接数据库成功！");
			else
				RecordSystemMessage("连接数据库失败！");
			SendSystemMessage();
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
		PreparedStatement pre;
		try{
			pre=conn.prepareStatement("insert into "+MySqlPara.global_mp.OriginalMessage_TableName+" values(?,?,?)");
			pre.setString(1, GetCurrentTime());
			pre.setString(2, String.valueOf(System.currentTimeMillis()));
			pre.setString(3, mess);
			pre.executeUpdate();
			pre.close();
			RecordSystemMessage("原始消息："+mess+sep);
		} catch (Exception e) {
			HandleException(e);
		}
	}
	
	//写入controlmessage
	public void WriteControlMessage(ReceiveDataStruct ds)
	{
		PreparedStatement pre;
		try
		{
			pre=conn.prepareStatement("insert into "+MySqlPara.global_mp.ControlMessage_TableName+" values(?,?,?,?)");
			pre.setString(1, GetCurrentTime());
			pre.setString(2, String.valueOf(System.currentTimeMillis()));
			pre.setString(3, ds.getMess());
			pre.setString(4, "0");
			pre.executeUpdate();
			pre.close();
			RecordSystemMessage("写入数据库"+MySqlPara.global_mp.DataMessage_TableName+"：控制信息："+ds.getMess());
			SendSystemMessage();
		}
		catch (Exception e)
		{
			HandleException(e);
		}
	}
	
	//解析从手机APP发来的字符串信息
	public boolean AnalyzeMessage(String str,ReceiveDataStruct ds)
	{
		if(!str.contains("control:"))
			return false;
		String mess=str.substring(str.indexOf("control:")+"control:".length());
		ds.setMess(mess);
		RecordSystemMessage("消息解析完成！");
		SendSystemMessage();
		return true;
	}
	
	//记录系统消息
	public void RecordSystemMessage(String str)
	{
		if(send_mess.length()==0)
			send_mess.append(GetCurrentTime()).append(sep);
		send_mess.append(str);
	}
	
	//发送系统消息
	public void SendSystemMessage()
	{
		System.out.println(send_mess);
		System.out.println();
		send_mess.delete(0, send_mess.length());
	}
	
	//统一异常处理
	public void HandleException(Exception e)
	{
		RecordSystemMessage("捕获异常："+e.toString().substring(0, e.toString().indexOf(":")));
		SendSystemMessage();
	}
}
