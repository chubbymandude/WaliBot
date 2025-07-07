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
 */
public class ChatBot implements ChatBotInterface
{
	private int numResponses; //Used to track how many prompts the user gave
	
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
		numResponses++;
		
		return output.toString();
	}

	/*
	 * What this method does: 
	 * --> Makes answer to prompt more formalized
	 * --> Makes answer to prompt concise 
	 * --> Makes answer mimic speaking style of answers in data.txt
	 * --> Provides data for use by ChatGPT
	 * --> Considers responses from the current user in its answer
	 */
	@Override
	public String getFormalAnswer(String prompt) 
	{
		return "Please provide a formal, short, and concise answer to the "
		+ "following prompt. Mimic the speaking style of the answers."
		+ " The last " + numResponses + " question-answers in the data "
		+ "are from the current user, so consider them in your answer. "
		+ "Prompt: " + prompt + "Data: " + getData();
	}

	/*
	 * Obtains data from text file so that ChatGPT 
	 * can parse it and provide an accurate result
	 */
	@Override
	public String getData() 
	{
		String data = "";
		//Use the Scanner class to append String data
		try(Scanner scanner = new Scanner(new File("data.txt")))
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
			System.err.println("Some I/O error occurred while getting data...");
		}
		return data;
	}

	/*
	 * Uses a BufferedWriter to save new prompts/answers
	 * to the ChatBot in the data.txt text file. 
	 * 
	 * Also saves prompts/responses to responses.txt
	 * so the ChatBot can remember previous user prompts + responses
	 */
	@Override
	public void saveResponse(String prompt, String answer) 
	{
		try
		(
			BufferedWriter writeToData = new BufferedWriter
			(new FileWriter("data.txt", true));
				
			BufferedWriter writeToResponses = new BufferedWriter
			(new FileWriter("responses.txt", true));
		)
		{
			writeToData.newLine();
			writeToData.write(prompt + "|" + answer);
			
			writeToResponses.write(prompt + "|" + answer);
			writeToResponses.newLine();
		}
		catch(IOException e)
		{
			System.err.println
			("Some I/O error occurred while saving responses..");
		}
	}

	//Clears history by calling delete method of File and clearing HashMap
	@Override
	public void clearHistory() 
	{
		File file = new File("responses.txt"); 
		file.delete();
	}
	
}