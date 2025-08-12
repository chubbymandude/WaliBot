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
import com.twilio.twiml.voice.Pause;
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
		// use a thread to perform some work that can be done while the startup message plays
		Thread thread = new Thread(() ->
		{
			conversations.put(request.getParameter("CallSid"), new Conversation());
		});
		thread.start();
		// send the startup message
		response.setContentType("application/xml");
		return new VoiceResponse.Builder()
			.say(new Say.Builder(Speech.STARTUP.get()).build())
			.record(buildRecord())
			.build()
			.toXml();
	}
	
	// runs after each prompt is made
	public String messageLoop(HttpServletRequest request, HttpServletResponse response)
	{
		String sid = request.getParameter("CallSid");
		// obtain recording from twilio API
		response.setContentType("application/xml");
		String recURL = request.getParameter("RecordingUrl");
		
		// algorithm for getting voice response and obtaining a relevant answer out of it
		String recording = downloadRecording(recURL, sid); 
		String prompt = SpeechConverter.convertSpeechToText(recording);
		Conversation currentConversation = conversations.get(sid);
		String answer = currentConversation.bot.getAnswerTo(prompt);
		
		// delete the file of the user's recording after usage
		File file = new File(recURL + ".wav");
		file.delete();
		
		// if ChatBot wasn't able to get a response it doesn't count toward the # of answers
		if(answer != Speech.NO_DATA.get()) 
		{
			conversations.get(sid).numAnswers++;
		}
		// determine if answer limit has been exceeded and build voice response accordingly
		if(currentConversation.numAnswers >= MAX_ANSWERS)
		{
			currentConversation.bot.close();
			conversations.remove(sid);
			return new VoiceResponse.Builder()	
				.say(new Say.Builder(answer).build())        
				.pause(new Pause.Builder().length(1).build()) 
				.say(new Say.Builder(Speech.END.get()).build())
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
		catch(URISyntaxException | IOException e) { e.printStackTrace(); }
		// create output file for recording in current directory so it can easily be obtained
		File outputFile = new File(sid);
		// write the recording into the file so it can be converted to text later
		try
		{
			// create new file for output
			outputFile.createNewFile();
			// create streams
			InputStream input = connection.getInputStream();
	        OutputStream output = new FileOutputStream(outputFile);
	        // write into stream the recording
			byte[] buffer = new byte[4096];
			int numBytes;
			while((numBytes = input.read(buffer)) != -1) 
			{
                output.write(buffer, 0, numBytes);
            }
			output.close();
		}
		catch(IOException e) { e.printStackTrace(); }
		// need to return path so it can be used for speech conversion
		return outputFile.getAbsolutePath();
	}
}