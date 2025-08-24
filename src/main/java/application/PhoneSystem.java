package application;

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
import com.twilio.twiml.voice.Pause;
import com.twilio.twiml.voice.Record;

import bot.ChatBot;
import bot.Database.NoDataException;

// utility functions for serving the phone line
@Service
public class PhoneSystem
{
	// exception that is thrown when there is a problem with the phone line
	// this is thrown in any I/O error and any other exception type that occurs during the call
	public static class PhoneException extends RuntimeException
	{
		private static final long serialVersionUID = 1L;
		
		public PhoneException()
		{
			super("Sorry, I cannot answer any questions at the moment. Allah-hafiz.");
		}
	}

	/*
	private static final String STARTUP = 
		"Assalammualaikum! Welcome to the Masjid Al-Wali Chat Service. " +
		"Ask any question related to the Masjid here!" +
		"You are allowed at most 5 questions per call.";
	*/
	private static final String STARTUP = "hello";
	private static final String END = 
		"Thank you for using the Masjid Al-Wali Chat Service. Allah-hafiz.";
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
		// use a thread to perform some work that can be done while the startup message plays
		Thread thread = new Thread(() ->
		{
			conversations.put(request.getParameter("CallSid"), new Conversation());
		});
		thread.start();
		// send the startup message
		response.setContentType("application/xml");
		return new VoiceResponse.Builder()
			.say(new Say.Builder(STARTUP).build())
			.record(buildRecord())
			.build()
			.toXml();
	}
	
	// runs after each prompt is made
	public String messageLoop(HttpServletRequest request, HttpServletResponse response)
	{
		// obtain recordingst from twilio API
		String sid = request.getParameter("CallSid");
		response.setContentType("application/xml");
		String recURL = request.getParameter("RecordingUrl");
		
		// algorithm for getting voice response and obtaining a relevant answer out of it
		String recording = downloadRecording(recURL, sid); 
		String prompt = SpeechConverter.convertSpeechToText(recording);
		Conversation currentConversation = conversations.get(sid);
		String answer = currentConversation.bot.getAnswerTo(prompt);
		conversations.get(sid).numAnswers += (answer == NoDataException.MESSAGE) ? 0 : 1;
		
		// determine if answer limit has been exceeded and build voice response accordingly
		if(currentConversation.numAnswers >= MAX_ANSWERS)
		{
			currentConversation.bot.close();
			conversations.remove(sid);
			return new VoiceResponse.Builder()	
				.say(new Say.Builder(answer).build())        
				.pause(new Pause.Builder().length(1).build()) 
				.say(new Say.Builder(END).build())
				.hangup(new Hangup.Builder().build())
				.build()
				.toXml();
		}
		else
		{
			return new VoiceResponse.Builder()	
				.say(new Say.Builder(answer).build())
				.record(buildRecord())
				.build()
				.toXml();
		}
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
	private String downloadRecording(String recURL, String sid)
	{
		try
		{
			// set up HTTP connection with auccount SID and authentication token
			HttpURLConnection conn = (HttpURLConnection) new URI(recURL).toURL().openConnection();
	        String auth = System.getenv("ACCOUNT_SID") + ":" + System.getenv("AUTH_TOKEN");
	        String encodedAuth = Base64.getEncoder().encodeToString(auth.getBytes());
	        conn.setRequestProperty("Authorization", "Basic " + encodedAuth);
	        conn.connect();
	        // create file for output
	        File outputFile = new File(sid);
	        outputFile.createNewFile();
			// create streams
			InputStream input = conn.getInputStream();
	        OutputStream output = new FileOutputStream(outputFile);
	        // write into stream the recording
			byte[] buffer = new byte[4096];
			int numBytes;
			while((numBytes = input.read(buffer)) != -1) 
			{
                output.write(buffer, 0, numBytes);
            }
			input.close();
			output.close();
			return outputFile.getAbsolutePath();
		}
		catch(URISyntaxException | IOException e) { throw new PhoneException(); }
	}
}