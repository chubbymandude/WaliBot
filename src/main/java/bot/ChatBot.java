package bot;

import java.io.*; 
import java.net.*;
import java.util.Scanner;

/*
 * Used for simulating the ChatBot for Masjid
 * 
 * See ChatBotInterface for see extra information about methods, 
 * such as parameter information and a detailed description
 * of what the method does at the high level
 * 
 * NOTE 7/12/2025:
 * Currently the ChatBot isn't able to actually "remember" previous
 * responses, will try to work on this functionality later
 */
public class ChatBot implements ChatBotInterface
{
	/*
	 * Constructor
	 */
	public ChatBot()
	{
		File file = new File("responses.txt");
		//Error-handle for creating a new file
		try 
		{
			file.createNewFile();
		} 
		catch(IOException e) 
		{
			System.err.println("Error creating responses file...");
		}
	}
	
	/*
	 * Uses the methods getData() and getFormalAnswer() to
	 * get an response that answers a prompt
	 * 
	 * Also saves responses so if the user has more than one
	 * question in a single session they can get it answered
	 * by the ChatBot
	 */
	@Override
	public String getAnswerTo(String prompt)
	{
		HttpURLConnection urlConnection = null;
		urlConnection = ChatGPTUtils.setConnection(urlConnection);
		
        ChatGPTUtils.sendPrompt(urlConnection, getFormalAnswer(prompt));
        
        String output = ChatGPTUtils.getOutput(urlConnection);
		saveResponse(prompt, output);
		
		return output;
	}

	/*
	 * What this method does: 
	 * --> Makes answer to prompt more formalized
	 * --> Makes answer to prompt concise 
	 * --> Consists prior prompt-response history
	 */
	@Override
	public String getFormalAnswer(String prompt) 
	{
		return "Write the given answer to the prompt in a formal, "
		+ "concise way... Prompt: " + prompt + "|Answer: " + getData(prompt);
	}

	/*
	 * Obtains data from database in order to obtain a relevant
	 * answer to a particular prompt
	 */
	@Override
	public String getData(String prompt) 
	{
		return DatabaseUtils.queryDatabase(prompt);
	}

	/*
	 * Uses a BufferedWriter to save new prompts/answers
	 * to responses.txt to allow ChatBot to remember
	 * previous responses.  
	 */
	@Override
	public void saveResponse(String prompt, String answer) 
	{
		try
		(
			BufferedWriter writeToResponses = new BufferedWriter
			(new FileWriter("responses.txt", true));
		)
		{
			writeToResponses.write(prompt + "|" + answer);
			writeToResponses.newLine();
		}
		catch(IOException e)
		{
			System.err.println
			("Some I/O error occurred while saving responses..");
		}
	}

	/*
	 * Uses Scanner class to load in responses for use by the
	 * ChatBot to remember previous responses
	 */
	@Override
	public String loadResponses() 
	{
		String data = "";
		//Use the Scanner class to append String data
		try(Scanner scanner = new Scanner(new File("responses.txt")))
		{		
			//Loop until end of file is reached
			while(scanner.hasNextLine())
			{
				String currLine = scanner.nextLine();
				String[] parts = currLine.split("\\|"); 
				data += "Question: " + parts[0] 
				+ " Answer: " + parts[1] + "       ";
			}
		}
		catch(IOException e)
		{
			System.err.println("I/O error occurred while getting responses...");
		}
		return data;
	}
	
	//Clears history by calling delete method of File and clearing HashMap
	@Override
	public void clearHistory() 
	{
		File file = new File("responses.txt"); 
		file.delete();
	}
}