package server;

//Constants related to the phone system
public enum Phone 
{
	QUIT("quit"), PATH("question.wav");
	
	final String contents;
	
	Phone(String contents)
	{
		this.contents = contents;
	}
}
