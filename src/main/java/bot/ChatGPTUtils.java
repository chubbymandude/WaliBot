package bot;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.*;

/*
 * Utility class for using ChatGPT; 
 * sets up usage of ChatGPT to make the ChatBot
 * easier to code up and debug
 * 
 * Only for use in the ChatBot class
 */
public class ChatGPTUtils 
{
	/*
	 * Sets up the connection between Java and OpenAI
	 * via a POST request
	 * 
	 * Performs various exception-handling involving
	 * Input/Output, URLs, and making POST requests
	 * and prints stack trace for debugging
	 */
	static HttpURLConnection setConnection(HttpURLConnection urlConnection) 
	{
		try
		{
			//Create a URL with the URL constant
			URI uri = new URI(OpenAI.URL_LINK.contents);
			URL url = uri.toURL();
			//Set up the URL connection via HttpURLConnection for output
			urlConnection = (HttpURLConnection) url.openConnection();
			urlConnection.setRequestMethod("POST");
			urlConnection.setRequestProperty
			("Authorization", "Bearer " + OpenAI.KEY.contents);
			urlConnection.setRequestProperty("Content-Type", "application/json");
			urlConnection.setDoOutput(true);
		}
		catch(URISyntaxException e)
		{
			System.err.println("Error occurred creating URI...");
			e.printStackTrace();
		}
		catch(MalformedURLException e)
		{
			System.err.println("Error occurred creating URL...");
			e.printStackTrace();
		}
		catch(ProtocolException e)
		{
			System.err.println("Error occurring trying to set POST request...");
			e.printStackTrace();
		}
		catch(IOException e)
		{
			System.err.println("Some Input/Output error "
			+ "occurred while setting the connection...");
			e.printStackTrace();
		}
		return urlConnection;
	}
	
	/*
	 * Used to setup the MessageBody for the ChatGPT prompt, 
	 * so it does not need to be written multiple times
	 */
	private static String getMessageBody(String prompt)
	{
		return "{\"model\": \"" + 
		OpenAI.MODEL.contents + "\", \"messages\":" + 
		" [{\"role\": \"user\", \"content\": \"" + prompt + "\"}]}";
	}
	
	/*
	 * Sends the prompt to ChatGPT via OutputStreamWriter
	 */
	static void sendPrompt(HttpURLConnection urlConnection, String prompt)
	{
		
		try(OutputStreamWriter writer = 
		new OutputStreamWriter(urlConnection.getOutputStream()))
		{
		    writer.write(getMessageBody(prompt));
		    writer.flush();
		    writer.close();
		}
		catch(IOException e)
		{
			System.err.println("Some Input/Output error occurred "
			+ "while sending the prompt to ChatGPT...");
			e.printStackTrace();
		}
	}
	
	/*
	 * Obtains output from ChatGPT based on what is in the
	 * OutputStream and stores it into a StringBuffer
	 * which is then condensed and converted to a String
	 * 
	 * This is done using a BufferedReader which reads line by line
	 * and stores each line into he StringBuffer
	 */
	static String getOutput(HttpURLConnection urlConnection)
	{
		try(BufferedReader in = new BufferedReader
	    (new InputStreamReader(urlConnection.getInputStream())))
		{
			//Obtain output from ChatGPT 
            String inputLine;
            StringBuffer response = new StringBuffer();
            while((inputLine = in.readLine()) != null) 
            {
                response.append(inputLine);
            }
            in.close();
            
            //Condense output so it only consists of the necessary details
            int startMarker = response.indexOf("content") + 11;
            int endMarker = response.indexOf("\"", startMarker); 
            
            return response.substring(startMarker, endMarker);
		}
		catch(IOException e)
		{
			System.err.println("Some Input/Output error occurred "
			+ "while obtaining response from ChatGPT...");
			e.printStackTrace();
		}
		return null; //If above fails still return something
	}
}	