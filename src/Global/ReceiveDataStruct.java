package Global;

public class ReceiveDataStruct
{
	private String mess=null;
	
	public ReceiveDataStruct(String str)
	{
		this.mess=str;
	}

	public String getMess() {
		return mess;
	}

	public void setMess(String mess) {
		this.mess = mess;
	}
}
