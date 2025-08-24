package bot;

import okhttp3.*;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.ArrayList;

/*
 * Used for creating the Embedding vector which
 * is used in PostgreSQL queries to create a functional
 * ChatBot that can answer any variation of a question
 */
public class Embedding 
{
	// queries used when saving embeddings (not relied on in application)
	private static final String SAVE_EMBEDDING = 
		"INSERT INTO dataset (question, embedding)\n" +
		"VALUES (?, ?)\n" +
		"ON CONFLICT (question) DO UPDATE\n" +
		"SET embedding = EXCLUDED.embedding";
	private static final String GET_PROMPT = 
		"SELECT question\n" +
		"FROM dataset\n" +
		"WHERE id = ?";
	private static final String GET_NUM_ROWS = "SELECT COUNT(1) FROM dataset";
	
	// gets embedding for use in query
	public static String getEmbedding(String prompt) throws IOException
	{
		JSONObject body = new JSONObject();
        body.put("input", prompt);
        body.put("model", "text-embedding-3-small");
        
        // build the request to OpenAI for obtaining embedding vector for prompt
        Request request = new Request.Builder()
			.url("https://api.openai.com/v1/embeddings")
			.addHeader("Authorization", "Bearer " + System.getenv("GPT_KEY"))		
			.addHeader("Content-Type", "application/json")
			.post(RequestBody.create(body.toString(), MediaType.parse("application/json")))
			.build();
        Response response = new OkHttpClient().newCall(request).execute();
        JSONObject object = new JSONObject(response.body().string());
        JSONArray array = object.getJSONArray("data").getJSONObject(0).getJSONArray("embedding");
        
        // convert to Java List type so it can be converted easily to a String for database use
        List<Float> embeddingList = new ArrayList<>();
        for(int index = 0; index < array.length(); index++) 
        { 
        	Float currValue = array.getFloat(index);
        	embeddingList.add(currValue); 
        }
        return embeddingList.toString();
	}
	
	// saves the embedding for the specified prompt to the database 
	private static void saveEmbedding(String prompt) throws SQLException, IOException
	{
		Connection connection = Database.getConnection();
		PreparedStatement statement = connection.prepareStatement(SAVE_EMBEDDING);
		statement.setString(1, prompt);
		statement.setObject(2, getEmbedding(prompt), java.sql.Types.OTHER);
		statement.executeUpdate();
	}
	
	// need to obtain # of rows for indexing
	private static int numRows() throws SQLException
	{
		Connection connection = Database.getConnection();
		PreparedStatement statement = connection.prepareStatement(GET_NUM_ROWS);
		ResultSet resultSet = statement.executeQuery();
		resultSet.next();
		return resultSet.getInt("count");
	}
	
	// get prompt associated with an ID 
	private static String getPrompt(int id) throws SQLException
	{
		Connection connection = Database.getConnection();
		PreparedStatement statement = connection.prepareStatement(GET_PROMPT);
		statement.setInt(1, id);
		ResultSet resultSet = statement.executeQuery();
		resultSet.next();
		return resultSet.getString("question");
	}
	
	// utilize this method to update the embeddings when updating database
	public static void main(String[] args)
	{
		try
		{
			int numCols = numRows();
			for(int row = 1; row <= numCols; row++) 
			{ 
				saveEmbedding(getPrompt(row)); 
			}
			System.out.println("Embeddings have been saved...");
		}
		catch(SQLException | IOException e) { e.printStackTrace(); }
	}
}