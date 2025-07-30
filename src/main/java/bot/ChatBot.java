package bot;

import stack.Stack; 

// instance class which performs ChatBot logic
public class ChatBot
{
	private Stack<String> prompts;
	
	public ChatBot()
	{
		prompts = new Stack<>();
	}
	
	// called by the application to obtain a response for the user's prompt
	public String getAnswerTo(String prompt)
	{
        prompts.push(prompt);
		return getData(prompt);
	}
	
	// pulls data from database and considers previous prompt if there is any
	public String getData(String prompt) 
	{
		if(!prompts.isEmpty())
		{
			String context = prompts.peek() + ". " +  prompt;
			return Database.queryDatabase(context);
		}
		return Database.queryDatabase(prompt) ;
	}
	
	// used for any cleanup needed after phone is closed
	public void clearHistory() 
	{
		prompts.clear();
		Database.closeDatabaseResources();
	}
}