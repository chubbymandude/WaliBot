package bot;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

// handles anything involving the underlying database for the Masjid ChatBot
public class Database 
{
	// an exception class which is thrown when no answer is found for a given prompt
	// this can be thrown for either SQLException or IOException
	public static class NoDataException extends RuntimeException
	{
		private static final long serialVersionUID = 1L;
		public static final String MESSAGE = 
			"Sorry, I cannot answer this question. Please try again.";
		
		NoDataException()
		{
			super(MESSAGE);
		}
	}
	
	// used to rule out results which don't actually match
	private static final double ACCURACY_THRESHOLD = 1.3;
	
	// database information
	private static final String URL = System.getenv("DB_URL");
	private static final String USERNAME = System.getenv("DB_USERNAME");
	private static final String PASSWORD = System.getenv("DB_PASSWORD");
	
	// general purpose method which is used whenever I need to form a connection with database
	static Connection getConnection() throws SQLException 
	{
        return DriverManager.getConnection(URL, USERNAME, PASSWORD);
    }
	
	// this method is specifically used by the ChatBot to obtain a relevant answer from database
	static String queryDatabase(String prompt, PreparedStatement botStatement)
	{
		try
		{
			String embedding = Embedding.getEmbedding(prompt);
			botStatement.setObject(1, embedding, java.sql.Types.OTHER); // VECTOR
			botStatement.setObject(2, embedding, java.sql.Types.OTHER);
			ResultSet resultSet = botStatement.executeQuery();

			if(resultSet.next())
			{
				// indicates to user that there is no data for what the user prompted for
				if(resultSet.getDouble("accuracy") < ACCURACY_THRESHOLD)
				{
					throw new NoDataException();
				}
				return resultSet.getString("answer");
			}
		}
		catch(SQLException | IOException e){ e.printStackTrace(); }
		// if the above did not succeed there was no data and exception is thrown
		throw new NoDataException();
	}
}