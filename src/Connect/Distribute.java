package Connect;

import java.net.Socket;
import java.util.ArrayList;
import Global.ConnectPara;
import Global.Utils;
import Handle.ReceiveMessage;

public class Distribute extends Thread
{
	ArrayList<ReceiveMessage> all_thread=new ArrayList<>();
	
	//对于Socket的处理
	public void Handle(Socket so)
	{
		//开启接收消息线程
		Utils.utils.SendSystemMessage("开启消息接收线程");
		ReceiveMessage t=new ReceiveMessage(so);
		all_thread.add(t);
		t.start();
	}
	
	@SuppressWarnings("deprecation")
	public void run()
	{
		while(true)
		{
			int i=0;
			try {
				sleep(60*1000);
			} catch (Exception e) {
				Utils.utils.HandleException(e);
			}
			for(i=0;i<all_thread.size();i++)
			{
				ReceiveMessage t=all_thread.get(i);
				if(Utils.utils.GetTimeByLong()-t.GetThreadTime()>=ConnectPara.global_cp.thread_timeout_time)
				{
					Utils.utils.SendSystemMessage("线程"+t.getName()+"超时，撤销该线程！剩余"+(all_thread.size()-1)+"个线程！");
					all_thread.remove(i);
					try {
						t.so.close();
						t.stop();
					} catch (Exception e) {
						Utils.utils.HandleException(e);
					}
				}
			}
		}
	}
}
