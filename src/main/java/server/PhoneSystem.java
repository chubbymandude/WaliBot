package server;

import java.io.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.MalformedURLException;

import com.twilio.twiml.VoiceResponse;
import com.twilio.twiml.voice.Say;
import com.twilio.twiml.voice.Hangup;
import com.twilio.twiml.voice.Record;
import com.twilio.twiml.voice.Redirect;

import speech.*;
import bot.*;

/*
 * Following class provides various utility methods
 * for the functionality of the application
 */
public class PhoneSystem 
{
	private int numAnswers; //Amount of questions answered; doesn't exceed 10
	private ChatBot bot; //Used for obtaining answers to questions
	
	//Setup PhoneSystem 
	public PhoneSystem()
	{
		numAnswers = 0;
		bot = new ChatBot();
	}
	
	//Sends the startup message when the call starts
	String startupMessage(spark.Request request, spark.Response response)
	{
		response.type("application/xml");
		return new VoiceResponse.Builder()
			.say(new Say.Builder(Speech.STARTUP.contents).build())
			.record(new Record.Builder().action("/process").build())
			.build()
			.toXml();
	}
	
	//Loop the conversation 
	String messageLoop(spark.Request request, spark.Response response)
	{
		response.type("application/xml");
		//Check if user hanged up the call
		String recUrl = request.queryParams("Recording");
		String recDuration = request.queryParams("RecordingDuration");
		if(recUrl == null || recUrl.isEmpty() 
		|| recDuration == null || recDuration.equals("0"))
		{
			return new VoiceResponse.Builder()
				.say(new Say.Builder(Speech.HANGUP.contents).build())
				.hangup(new Hangup.Builder().build())
				.build()
				.toXml();
		}
		//Get TEXT from recording
		String prompt = SpeechConverter.convertSpeechToText
		(getRecording(recUrl));
		//Obtain VOICE response
		VoiceResponse.Builder voiceResponse = new VoiceResponse.Builder();
		//Check if text says "quit"
		if(prompt.toLowerCase().contains(Phone.QUIT.contents))
		{
			voiceResponse
				.say(new Say.Builder(Speech.END.contents).build())
				.hangup(new Hangup.Builder().build());
		}
		else if(numAnswers > 10) //Answer limit exceeded
		{
			voiceResponse
				.say(new Say.Builder(Speech.EXCEED.contents).build())
				.hangup(new Hangup.Builder().build());
		}
		else //Get answer and sent it through voice
		{
			voiceResponse
				.say(new Say.Builder(bot.getAnswerTo(prompt)).build())
				.redirect(new Redirect.Builder("/voice").build());
		}
		numAnswers++;
		return voiceResponse.build().toXml();
	}

	//Downloads recording from speech spoken by user
	private String getRecording(String recURL)
	{
		URL url;
		//Create URL via URI class
		try
		{
			url = new URI(recURL + ".wav").toURL();
		}
		catch(URISyntaxException e)
		{
			System.err.println("Error occurred creating URI...");
			return null;
		}
		catch(MalformedURLException e)
		{
			System.err.println("Recording URL was malformed...");
			return null;
		}
		//Use InputStream to obtain recording
		try
		(
			InputStream input = url.openStream();
	        OutputStream output = new FileOutputStream(Phone.PATH.contents);
		)
		{
			//Obtain file via buffering and writing from stream
			byte[] buffer = new byte[4096];
			int numBytes;
			while((numBytes = input.read(buffer)) != -1) 
			{
                output.write(buffer, 0, numBytes);
            }
		}
		catch(IOException e)
		{
			System.err.println("I/O Exception created streams...");
			return null;
		}
		return Phone.PATH.contents;
	}
}
