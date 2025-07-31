package bot;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import speech.Speech;

// handles anything involving the underlying database for the Masjid ChatBot
public class Database 
{
	private static Connection connection;
	private static PreparedStatement chatBotStatement;
	private static final double ACCURACY_THRESHOLD = 1.2;
	
	// initialize the connection and chatBot statement upon entering class
	// this is in order to optimize speed of the application
	static 
	{
	    try 
	    {
	        connection = getConnection();
	        chatBotStatement = connection.prepareStatement(Queries.QUERY.get());
	    } 
	    catch(SQLException e) 
	    {
	        System.err.println("Error setting up connection and statement for ChatBot...");
	    }
	}
	
	// the above resources need to be closed due to being statically initialized
	static void closeDatabaseResources()
	{
		try
		{
			connection.close();
			chatBotStatement.close();
		}
		catch(SQLException e)
		{
			System.err.println("Error closing Database resources...");
		}
	}
	
	static Connection getConnection() throws SQLException 
	{
        return DriverManager.getConnection(
        	Queries.URL.get(), Queries.USERNAME.get(), Queries.PASSWORD.get());
    }
	
	static String queryDatabase(String prompt)
	{
		String embedding = Embedding.getEmbedding(prompt);
		if(embedding == null)
		{
			return Speech.GET_FAIL.get();
		}
		try
		{
			chatBotStatement.setObject(1, embedding, java.sql.Types.OTHER);
			chatBotStatement.setObject(2, embedding, java.sql.Types.OTHER);
			ResultSet resultSet = chatBotStatement.executeQuery();

			if(resultSet.next())
			{
				// indicates to user that there is no data for what the user prompted for
				if(resultSet.getDouble("accuracy") < ACCURACY_THRESHOLD)
				{
					return Speech.NO_DATA.get();
				}
				return resultSet.getString("answer");
			}
		}
		catch(SQLException e)
		{
			System.err.println("Error obtaining from database...");
			return Speech.GET_FAIL.get(); 
		}
		return Speech.NO_DATA.get();
	}
}