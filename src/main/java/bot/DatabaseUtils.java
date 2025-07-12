package bot;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import speech.Speech;

/*
 * Class used to handle anything involving the underlying
 * database for the Masjid ChatBot
 */
public class DatabaseUtils 
{
	/*
	 * Sets up the connection between this Java server and
	 * the database used for Masjid data
	 */
	private static Connection getConnection() throws SQLException 
	{
        return DriverManager.getConnection(Database.URL.contents, 
        Database.USERNAME.contents, Database.PASSWORD.contents);
    }
	
	/*
	 * Performs a query on the database in order to find
	 * a relevant response to what the user is asking
	 */
	static String queryDatabase(String prompt)
	{
		//Get the embedding for the prompt and save it to the database
		String embedding = EmbeddingUtils.getEmbedding(prompt);
		//Error-handling
		if(embedding == null)
		{
			return Speech.ERROR.contents;
		}
		//Query the database
		try
		(
			Connection connection = getConnection();
			PreparedStatement statement = 
			connection.prepareStatement(Database.QUERY.contents);
		)
		{
			//Set prompt & embedding for use in query
			statement.setObject(1, embedding, java.sql.Types.OTHER);
			//Find appropriate answer
			ResultSet resultSet = statement.executeQuery();
			if(resultSet.next())
			{
				return resultSet.getString("answer");
			}
			
		}
		catch(SQLException e)
		{
			System.err.println("Error obtaining from database...");
			return Speech.ERROR.contents; //Currently experiencing errors...
		}
		return Speech.NO_DATA.contents;
	}

	//Saves the embedding to the database
	private static void saveEmbedding(String prompt) 
	throws SQLException
	{
	
		Connection connection = getConnection();
		PreparedStatement statement = 
		connection.prepareStatement(Database.SAVE_EMBEDDING.contents);

		statement.setString(1, prompt);
		statement.setObject
		(2, EmbeddingUtils.getEmbedding(prompt), java.sql.Types.OTHER);
		statement.executeUpdate();
	}
	
	//Gets the # of columns in the dataset table
	private static int numColumns() throws SQLException
	{

		Connection connection = getConnection();
		PreparedStatement statement = 
		connection.prepareStatement(Database.GET_NUM_ROWS.contents);

		ResultSet resultSet = statement.executeQuery();
		if(resultSet.next())
		{
			return resultSet.getInt("count");
		}
		return 0;
	}
	
	//Get prompt associated with an ID 
	private static String getPrompt(int id) throws SQLException
	{
		Connection connection = getConnection();
		PreparedStatement statement = 
		connection.prepareStatement(Database.GET_PROMPT.contents);

		statement.setInt(1, id);
		ResultSet resultSet = statement.executeQuery();
		if(resultSet.next())
		{
			return resultSet.getString("question");
		}
		return null;
	}
	
	/*
	 * Following MAIN method is used to update embeddings 
	 * for the database, so that they can be used
	 * when the user makes a query
	 */
	public static void main(String[] args)
	{
		try
		{
			int numCols = numColumns();
			//Loop through each row and and save embedding to each
			for(int row = 1; row <= numCols; row++)
			{
				saveEmbedding(getPrompt(row));
			}
			System.out.println("Embeddings have been saved...");
		}
		catch(SQLException e)
		{
			e.printStackTrace();
			System.err.println("Error saving all embeddings...");
		}
	}
}
