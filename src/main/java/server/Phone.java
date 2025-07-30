package server;

public enum Phone 
{
	QUIT("quit"), PATH("recordings/question.wav"), 
	ACCOUNT_SID(System.getenv("ACCOUNT_SID")), 
	AUTH_TOKEN(System.getenv("AUTH_TOKEN"));
	
	public final String contents;
	
	Phone(String contents)
	{
		this.contents = contents;
	}
}
