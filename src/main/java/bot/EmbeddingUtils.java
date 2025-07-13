package bot;

import okhttp3.*;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.List;
import java.util.ArrayList;

/*
 * This class is used for creating the Embedding vector
 * which will be used in PostgreSQL queries to create
 * a functionality ChatBot that can be able to obtain
 * an answer based on any variation on input
 */
public class EmbeddingUtils 
{
	//Gets embedding for use in query
	static String getEmbedding(String prompt)
	{
		OkHttpClient client = new OkHttpClient();
		
		//Sets up input and model for embedding
		JSONObject body = new JSONObject();
        body.put("input", prompt);
        body.put("model", "text-embedding-3-small");
        
        //Builds JSON array which represents the embedding array
        JSONArray array = buildArray(buildResponse(client, buildRequest(body)));
        
        //Error handling
        if(array == null)
        {
        	return null;
        }
        
        //Loop through JSON Array, add elements to List
        List<Float> embeddingList = new ArrayList<>();
        for(int index = 0; index < array.length(); index++) 
        {
            embeddingList.add(array.getFloat(index));
        }
        
        //Save and convert list to a String with specified method
		return convertEmbeddingToString(embeddingList);
	}
	
	//Converts embedding to String format for use in query
	private static String convertEmbeddingToString(List<Float> embeddingList)
	{
		StringBuilder data = new StringBuilder("[");
		//Loop through embedding list
		for(int index = 0; index < embeddingList.size(); index++)
		{
			data.append(embeddingList.get(index));
			//Only add comma if any element besides last element
			if(index != embeddingList.size() - 1)
			{
				data.append(", ");
			}
		}
		return data.toString() + "]";
	}
	
	//Builds the POST Request to OpenAI
	private static Request buildRequest(JSONObject body)
	{
		return new Request.Builder()
			.url(OpenAI.EMBEDDING_LINK.contents)
			.addHeader("Authorization", "Bearer " + OpenAI.KEY.contents)
			.addHeader("Content-Type", "application/json")
			.post(getRequestBody(body))
			.build();
	}
	
	//Builds the RequestBody for OpenAI
	private static RequestBody getRequestBody(JSONObject body)
	{
		return RequestBody.create
		(body.toString(), MediaType.parse("application/json"));
	}
	
	//Builds the Response for the client
	private static Response buildResponse(OkHttpClient client, Request request)
	{
		try
		{
			return client.newCall(request).execute();
		}
		catch(IOException e)
		{
			System.err.println("I/O error occurred while getting response...");
			return null;
		}
	}
	
	//Builds array for use in embedding
	private static JSONArray buildArray(Response response)
	{
		//Check if response was null, shouldn't continue if so
		if(response == null)
		{
			return null;
		}
		//Error-handling for building array
		try
		{
			//Obtain body for JSON
			JSONObject body = new JSONObject(response.body().string());
			//Build the array based on the body above
			return body.getJSONArray("data")
			.getJSONObject(0).getJSONArray("embedding");
		}
		catch(IOException e)
		{
			System.err.println("I/O error occurred while building array...");
			return null;
		}
	}
	
}
