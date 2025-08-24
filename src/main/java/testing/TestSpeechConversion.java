package testing;

import application.SpeechConverter;
import bot.ChatBot;

// class used for testing effectiveness of speech converter
public class TestSpeechConversion 
{
	public static final String URL = "masjid.wav";
	
	public static void main(String[] args) 
	{
		long start = System.currentTimeMillis();
		String text = SpeechConverter.convertSpeechToText(URL);
		System.out.println(System.currentTimeMillis() - start + "ms");
		System.out.println(text);
		ChatBot bot = new ChatBot();
		String answer = bot.getAnswerTo(text);
		System.out.println(System.currentTimeMillis() - start + "ms");
		System.out.println(answer);
		bot.close();
	}
}
