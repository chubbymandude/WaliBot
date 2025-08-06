package server;

import java.io.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.HttpURLConnection;

import java.util.concurrent.ConcurrentHashMap;
import java.util.Base64;
import org.springframework.stereotype.Service;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import com.twilio.http.HttpMethod;
import com.twilio.twiml.VoiceResponse;
import com.twilio.twiml.voice.Say;
import com.twilio.twiml.voice.Hangup;
import com.twilio.twiml.voice.Record;

import speech.*;
import bot.*;

// utility functions for serving the phone line
@Service
public class PhoneSystem
{
	private static final int MAX_ANSWERS = 5;
	
	// holds a ChatBot instance to handle multiple incoming calls
	private class Conversation
	{
		ChatBot bot;
		int numAnswers;
		
		Conversation()
		{
			bot = new ChatBot();
			numAnswers = 0;
		}
	}	

	// used to handle multiple call requests at the same time
	private final ConcurrentHashMap<String, Conversation> conversations;
	
	public PhoneSystem()
	{
		conversations = new ConcurrentHashMap<String, Conversation>();
	}
	
	// should run before user sends first prompt
	public String startupMessage(HttpServletRequest request, HttpServletResponse response)
	{
		response.setContentType("application/xml");
		return new VoiceResponse.Builder()
			.say(new Say.Builder(Speech.STARTUP.get()).build())
			.record(buildRecord())
			.build()
			.toXml();
	}
	
	public String messageLoop(HttpServletRequest request, HttpServletResponse response)
	{
		// setup ChatBot during first conversation
		String sid = request.getParameter("CallSid");
		conversations.putIfAbsent(sid, new Conversation());
		
		// obtain recording from twilio API
		response.setContentType("application/xml");
		String recUrl = request.getParameter("RecordingUrl");
		String recDuration = request.getParameter("RecordingDuration");
		
		// if the user hanged up the application should still clean up resources
		if(hangedUp(recUrl, recDuration))
		{
			cleanup(sid);
			return new VoiceResponse.Builder()	
				.say(new Say.Builder(Speech.END.get()).build())
				.hangup(new Hangup.Builder().build())
				.build()
				.toXml();
		} 
		
		// algorithm for getting voice response and obtaining a relevant answer out of it
		String recording = downloadRecording(recUrl); 
		String prompt = SpeechConverter.convertSpeechToText(recording);
		String answer = conversations.get(sid).bot.getAnswerTo(prompt);
		
		// if ChatBot wasn't able to get a response it doesn't count toward the # of answers
		if(answer != Speech.NO_DATA.get()) 
		{
			conversations.get(sid).numAnswers++;
		}
		// determine if answer limit has been exceeded and build voice response accordingly
		if(conversations.get(sid).numAnswers >= MAX_ANSWERS)
		{
			cleanup(sid);
			return new VoiceResponse.Builder()	
				.say(new Say.Builder(answer + Speech.END.get()).build())
				.hangup(new Hangup.Builder().build())
				.build()
				.toXml();
		}
		else
		{
			// indicate to the user how many valid questions they can get answered
			int numLeft = MAX_ANSWERS - conversations.get(sid).numAnswers;
			// include both the answer and the indication of # of messages left in voice response
			return new VoiceResponse.Builder()	
				.say(new Say.Builder(answer + " You have " + numLeft + " questions left.").build())
				.record(buildRecord())
				.build()
				.toXml();
		}
	}
	
	// utility method for determing if the user prematurely exited the call
	private boolean hangedUp(String recUrl, String recDur)
	{
		return recUrl == null || recUrl.isEmpty() || recDur == null || recDur.equals("0");
	}
	
	// allows the ChatBot to be able to take in multiple prompts
	private Record buildRecord()
	{
		return new Record.Builder()
				.action("/process")
			    .method(HttpMethod.POST)
			    .timeout(2)
			    .maxLength(20)
			    .playBeep(true)
			    .build();
	}

	// places recording in project directory so it can be used by Vosk model
	private String downloadRecording(String recURL)
	{
		HttpURLConnection connection = null;
		// set up HTTP connection with auccount SID and authentication token
		try
		{
			connection = (HttpURLConnection) new URI(recURL).toURL().openConnection();
	        String auth = Phone.ACCOUNT_SID.get() + ":" + Phone.AUTH_TOKEN.get();
	        String encodedAuth = Base64.getEncoder().encodeToString(auth.getBytes());
	        connection.setRequestProperty("Authorization", "Basic " + encodedAuth);
	        connection.connect();
		}
		catch(URISyntaxException | IOException e)
		{
			System.err.println("Could not obtain recording URL or a miscellaneous I/O error...");
			e.printStackTrace();
			return null;
		}
		
		// create output file for recording in current directory so it can easily be obtained
		File outputFile = new File("recordings", Phone.PATH.get());
		outputFile.getParentFile().mkdirs();
		
		// write the recording into the file so it can be converted to text later
		try
		(
			InputStream input = connection.getInputStream();
	        OutputStream output = new FileOutputStream(outputFile);
		)
		{
			byte[] buffer = new byte[4096];
			int numBytes;
			while((numBytes = input.read(buffer)) != -1) 
			{
                output.write(buffer, 0, numBytes);
            }
		}
		catch(IOException e)
		{
			System.err.println("I/O Exception creating streams...");
			e.printStackTrace();
			return null;
		}
		return outputFile.getAbsolutePath();
	}

	// cleanup actions for phone system
	public void cleanup(String sid)
	{
		conversations.get(sid).bot.clearHistory();
		conversations.remove(sid);
		
		File recording = new File(Phone.PATH.get());
		recording.delete();
	}
}