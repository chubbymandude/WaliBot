package bot;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

// this class is for my use in order to set embeddings stored in the database upon an update
public class SaveEmbeddings 
{
	private static void saveEmbedding(String prompt) throws SQLException
	{
		Connection connection = Database.getConnection();
		PreparedStatement statement = connection.prepareStatement(Queries.SAVE_EMBEDDING.contents);

		statement.setString(1, prompt);
		statement.setObject(2, Embedding.getEmbedding(prompt), java.sql.Types.OTHER);
		statement.executeUpdate();
	}
	
	private static int numColumns() throws SQLException
	{
		Connection connection = Database.getConnection();
		PreparedStatement statement = connection.prepareStatement(Queries.GET_NUM_ROWS.contents);

		ResultSet resultSet = statement.executeQuery();
		if(resultSet.next())
		{
			return resultSet.getInt("count");
		}
		return 0;
	}
	
	// get prompt associated with an ID 
	private static String getPrompt(int id) throws SQLException
	{
		Connection connection = Database.getConnection();
		PreparedStatement statement = connection.prepareStatement(Queries.GET_PROMPT.contents);

		statement.setInt(1, id);
		ResultSet resultSet = statement.executeQuery();
		if(resultSet.next())
		{
			return resultSet.getString("question");
		}
		return null;
	}
	
	// utilize this method to update the embeddings when updating database
	public static void main(String[] args)
	{
		try
		{
			int numCols = numColumns();
			for(int row = 1; row <= numCols; row++)
			{
				saveEmbedding(getPrompt(row));
			}
			System.out.println("Embeddings have been saved...");
		}
		catch(SQLException e)
		{
			System.err.println("Error saving all embeddings...");
			e.printStackTrace();
		}
	}
}
