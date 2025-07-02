package testing;

import java.util.Scanner;

import bot.ChatBot;

/*
 * Test ChatBot via the main method
 */
public class TestChatBot 
{
	public static void main(String[] args)
	{
		ChatBot bot = new ChatBot(); 
		//Take in user input (for testing)
		Scanner sc = new Scanner(System.in);
		//User Input
		String input;
		//Loop until "quit" inputted
		while(true)
		{
			System.out.print("Enter a prompt: ");
			input = sc.nextLine();
			
			if(input.equals("quit")) //Force exit
			{
				break;
			}
			
			System.out.println("ChatGPT's response...");
			System.out.println(bot.getAnswerTo(input));
		}
		bot.clearHistory(); //History gets cleared once the user is done
		System.out.println("Program terminating...");
		sc.close();
	}
}