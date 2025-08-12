package bot;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import stack.Stack; 

// instance class which performs ChatBot logic
public class ChatBot implements AutoCloseable
{
	private Stack<String> prompts;
	private Connection connection;
	private PreparedStatement botStatement;
	
	public ChatBot()
	{
		prompts = new Stack<>();
		// form database connection and prepared statement 
		// this is done as these can be setup before the startup message
		try
		{
			connection = Database.getConnection();
			botStatement = connection.prepareStatement(Queries.GET_ANSWER.get());
		}
		catch(SQLException e) { e.printStackTrace(); }
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
			String context = prompts.peek() + " . " +  prompt;
			return Database.queryDatabase(context, botStatement);
		}
		return Database.queryDatabase(prompt, botStatement);
	}
	
	// used for any cleanup needed after phone is closed
	@Override
	public void close() 
	{
		prompts.clear();
		// close database resources
		try
		{
			botStatement.close();
			connection.close();
		}
		catch(SQLException e) { e.printStackTrace(); }
	}
}