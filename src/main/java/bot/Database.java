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
	private static final double ACCURACY_THRESHOLD = 1.3;
	
	// general purpose method which is used whenever I need to form a connection with database
	static Connection getConnection() throws SQLException 
	{
        return DriverManager.getConnection(
        	Queries.URL.get(), Queries.USERNAME.get(), Queries.PASSWORD.get());
    }
	
	// this method is specifically used by the ChatBot to obtain a relevant answer from database
	static String queryDatabase(String prompt)
	{
		String embedding = Embedding.getEmbedding(prompt);
		if(embedding == null)
		{
			return Speech.GET_FAIL.get();
		}
		try
		(
			Connection connection = getConnection();
		    PreparedStatement botStatement = connection.prepareStatement(Queries.GET_ANSWER.get());
		)
		{
			botStatement.setObject(1, embedding, java.sql.Types.OTHER); // VECTOR
			botStatement.setObject(2, embedding, java.sql.Types.OTHER);
			ResultSet resultSet = botStatement.executeQuery();

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
			e.printStackTrace();
			return Speech.GET_FAIL.get(); 
		}
		return Speech.NO_DATA.get(); // this won't run, just here to satisfy method return type
	}
}