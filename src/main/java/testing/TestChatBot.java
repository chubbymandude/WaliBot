package testing;

import java.util.Scanner;
import bot.ChatBot;

// use to test ChatBot without needing to call phone (check for bugs or bad answers to questions)
public class TestChatBot 
{
	public static void main(String[] args)
	{
		ChatBot bot = new ChatBot(); 
		Scanner sc = new Scanner(System.in);
		String input;
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
		bot.clearHistory(); 
		System.out.println("Program terminating...");
		sc.close();
	}
}