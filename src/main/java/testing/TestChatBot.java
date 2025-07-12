package testing;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Scanner;

import bot.ChatBot;

/*
 * Test ChatBot via the main method
 */
public class TestChatBot 
{
	public static void main(String[] args)
	{
		//Redirect error
		try
		(
			FileOutputStream fileStreamErr = new FileOutputStream("error.txt");
            PrintStream errorStream = new PrintStream(fileStreamErr);
		)
		{
			System.setErr(errorStream);
		}
		catch(FileNotFoundException e)
		{
			System.err.println("File was not found...");
		}
		catch(IOException e)
		{
			System.err.println("Some I/O error occurred while redirecting...");
		}
		ChatBot bot = new ChatBot(); 
		Scanner sc = new Scanner(System.in);
		String input;
		//Loop until "quit" inputted
		while(true)
		{
			System.out.print("Enter a prompt: ");
			input = sc.nextLine();
			
			if(input.equals("quit")) 
			{
				break;
			}
			
			System.out.println("Response...");
			System.out.println(bot.getAnswerTo(input));
		}
		bot.clearHistory(); //History gets cleared once the user is done
		System.out.println("Program terminating...");
		sc.close();
	}
}