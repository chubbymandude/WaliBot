package bot;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import bot.Database.NoDataException;

// instance class which performs ChatBot logic
public class ChatBot implements AutoCloseable
{
	// following query is used to obtain a prompt based on the embedding vector of prompt
	// the query returns one result, and returns the most accurate out of the result set
	private static final String GET_ANSWER = 
		"SELECT question, answer, 1 - (embedding <#> ?) AS accuracy\n" + 
		"FROM dataset\n" + 
		"ORDER BY embedding <#> ? \n" + 
		"LIMIT 1;";
	
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
			botStatement = connection.prepareStatement(GET_ANSWER);
		}
		catch(SQLException e) { e.printStackTrace(); }
	}
	
	// called by the application to obtain a response for the user's prompt
	// if answer failed to be obtained, application sends relevant response
	public String getAnswerTo(String prompt)
	{
		// query the prompt and add context if it exists
		String context = prompts.isEmpty() ? prompt : prompts.peek() + " . " + prompt;
		try
		{
			String answer = Database.queryDatabase(context, botStatement);
			prompts.push(prompt);
			return answer;
		}
		catch(NoDataException e) { return e.getMessage(); }
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