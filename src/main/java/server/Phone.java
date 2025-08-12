package server;

public enum Phone 
{
	ACCOUNT_SID(System.getenv("ACCOUNT_SID")), 
	AUTH_TOKEN(System.getenv("AUTH_TOKEN"));
	
	private final String contents;
	
	Phone(String contents)
	{
		this.contents = contents;
	}
	
	public String get()
	{
		return this.contents;
	}
}
