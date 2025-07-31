package server;

import java.io.*;
import java.net.URI;
import java.net.URISyntaxException;

import org.springframework.stereotype.Service;

import java.net.HttpURLConnection;
import java.net.MalformedURLException;

import com.twilio.twiml.VoiceResponse;
import com.twilio.twiml.voice.Say;
import com.twilio.twiml.voice.Hangup;
import com.twilio.twiml.voice.Record;

import speech.*;
import bot.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

// utility functions for serving the phone line
@Service
public class PhoneSystem implements AutoCloseable
{
	private static final int MAX_ANSWERS = 5;
	private int numAnswers; 
	private ChatBot bot; 
	
	public PhoneSystem()
	{
		bot = new ChatBot();
		numAnswers = 0;
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
		response.setContentType("application/xml");
		String recUrl = request.getParameter("RecordingUrl");
		String recDuration = request.getParameter("RecordingDuration");
		
		// if the user hanged up the application should still clean up resources
		if(hangedUp(recUrl, recDuration))
		{
			close();
			return new VoiceResponse.Builder()
				.say(new Say.Builder(Speech.END.get()).build())
				.hangup(new Hangup.Builder().build())
				.build()
				.toXml();
		} 
		
		VoiceResponse.Builder voiceResponse = new VoiceResponse.Builder()
			.play(new com.twilio.twiml.voice.Play.Builder
			("https://api.twilio.com/cowbell.mp3").loop(1).build());
		String recording = downloadRecording(recUrl); 
		String prompt = SpeechConverter.convertSpeechToText(recording);
		String answer = bot.getAnswerTo(prompt);
		numAnswers++;
		
		if(numAnswers >= MAX_ANSWERS)
		{
			close();
			voiceResponse 
				.say(new Say.Builder(answer + " . " + Speech.END.get()).build())
				.hangup(new Hangup.Builder().build());
		}
		else
		{
			voiceResponse 
				.say(new Say.Builder(answer).build())
				.record(buildRecord());
		}
		// due to the possibly of \n in voice response must remove any instances of it
		return voiceResponse.build().toXml().replaceAll("\\n", "");
	}
	
	private boolean hangedUp(String recUrl, String recDur)
	{
		return recUrl == null || recUrl.isEmpty() || recDur == null || recDur.equals("0");
	}
	
	// allows the ChatBot to be able to take in multiple prompts
	private Record buildRecord()
	{
		return new Record.Builder()
				.action("/process")
			    .method(com.twilio.http.HttpMethod.POST)
			    .timeout(10)
			    .maxLength(30)
			    .playBeep(true)
			    .build();
	}

	// places recording in project directory so it can be used by Vosk model
	private String downloadRecording(String recURL)
	{
		HttpURLConnection connection;
		// set up HTTP connection with auccount SID and authentication token
		try
		{
			connection = (HttpURLConnection) new URI(recURL).toURL().openConnection();
	        String auth = Phone.ACCOUNT_SID.get() + ":" + Phone.AUTH_TOKEN.get();
	        String encodedAuth = java.util.Base64.getEncoder().encodeToString(auth.getBytes());
	        connection.setRequestProperty("Authorization", "Basic " + encodedAuth);
	        connection.connect();
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
		catch(IOException e)
		{
			System.err.println("Error trying to authenticate...");
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

	// cleanup actions after phone hangup
	@Override
	public void close()
	{
		bot.clearHistory();
		File recording = new File(Phone.PATH.get());
		recording.delete();
	}
}