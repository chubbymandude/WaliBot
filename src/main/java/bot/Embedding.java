package bot;

import okhttp3.*;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.List;
import java.util.ArrayList;

/*
 * Used for creating the Embedding vector which
 * is used in PostgreSQL queries to create a functional
 * ChatBot that can answer any variation of a question
 */
public class Embedding 
{
	// gets embedding for use in query
	static String getEmbedding(String prompt)
	{
		OkHttpClient client = new OkHttpClient();
		
		JSONObject body = new JSONObject();
        body.put("input", prompt);
        body.put("model", "text-embedding-3-small");
        
        JSONArray array = buildArray(buildResponse(client, buildRequest(body)));
        
        if(array == null)
        {
        	return null;
        }
        
        List<Float> embeddingList = new ArrayList<>();
        for(int index = 0; index < array.length(); index++) 
        {
            embeddingList.add(array.getFloat(index));
        }
        
		return convertEmbeddingToString(embeddingList);
	}
	
	// converts embedding to String format for use in query
	private static String convertEmbeddingToString(List<Float> embeddingList)
	{
		StringBuilder data = new StringBuilder("[");
		for(int index = 0; index < embeddingList.size(); index++)
		{
			data.append(embeddingList.get(index));
			if(index != embeddingList.size() - 1)
			{
				data.append(", ");
			}
		}
		return data.toString() + "]";
	}
	
	private static RequestBody getRequestBody(JSONObject body)
	{
		return RequestBody.create(body.toString(), MediaType.parse("application/json"));
	}
	
	private static Request buildRequest(JSONObject body)
	{
		return new Request.Builder()
			.url(OpenAI.EMBEDDING_LINK.contents)
			.addHeader("Authorization", "Bearer " + OpenAI.KEY.contents)
			.addHeader("Content-Type", "application/json")
			.post(getRequestBody(body))
			.build();
	}
	
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
	
	private static JSONArray buildArray(Response response)
	{
		if(response == null)
		{
			return null;
		}
		try
		{
			JSONObject body = new JSONObject(response.body().string());
			return body.getJSONArray("data").getJSONObject(0).getJSONArray("embedding");
		}
		catch(IOException e)
		{
			System.err.println("I/O error occurred while building array...");
			return null;
		}
	}
	
}
