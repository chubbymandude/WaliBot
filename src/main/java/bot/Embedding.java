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
		JSONObject body = new JSONObject();
        body.put("input", prompt);
        body.put("model", OpenAI.EMBEDDING_MODEL.get());
        JSONArray array;
        
        // utilize a variety of utility methods to create a JSON array to convert 
        try
        {
        	array = buildArray(buildResponse(new OkHttpClient(), buildRequest(body)));
        }
        catch(IOException e)
        {
        	System.err.println("I/O exception obtaining embedding...");
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
	
	///////////////////////////////////////////////////////////////////////////
	// following set of methods are utility methods for creating JSON array //
	/////////////////////////////////////////////////////////////////////////
	
	private static RequestBody getRequestBody(JSONObject body)
	{
		return RequestBody.create(body.toString(), MediaType.parse("application/json"));
	}
	
	private static Request buildRequest(JSONObject body)
	{
		return new Request.Builder()
			.url(OpenAI.EMBEDDING_LINK.get())
			.addHeader("Authorization", "Bearer " + OpenAI.KEY.get())
			.addHeader("Content-Type", "application/json")
			.post(getRequestBody(body))
			.build();
	}
	
	private static Response buildResponse(OkHttpClient client, Request request) throws IOException
	{
		return client.newCall(request).execute();
	}
	
	private static JSONArray buildArray(Response response) throws IOException
	{
		if(response == null)
		{
			return null;
		}
		JSONObject body = new JSONObject(response.body().string());
		return body.getJSONArray("data").getJSONObject(0).getJSONArray("embedding");
	}
	
}
