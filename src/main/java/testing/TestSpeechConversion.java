package testing;

import bot.ChatBot;
import speech.SpeechConverter;

// class used for testing effectiveness of speech converter
public class TestSpeechConversion 
{
	public static final String URL = "youth.wav";
	
	public static void main(String[] args)
	{
		String text = SpeechConverter.convertSpeechToText(URL);
		System.out.println(text);
		ChatBot bot = new ChatBot();
		String answer = bot.getAnswerTo(text);
		System.out.println(answer);
	}
}
