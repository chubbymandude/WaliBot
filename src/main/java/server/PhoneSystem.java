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
	ChatBot bot; 
	
	public PhoneSystem()
	{
		numAnswers = 0;
		bot = new ChatBot();
	}
	
	// sends the startup message (runs before the user states their first question)
	public String startupMessage(HttpServletRequest request, HttpServletResponse response)
	{
		response.setContentType("application/xml");
		return new VoiceResponse.Builder()
			.say(new Say.Builder(Speech.STARTUP.contents).build())
			.record(buildRecord())
			.build()
			.toXml();
	}
	
	// loop the conversation 
	public String messageLoop(HttpServletRequest request, HttpServletResponse response)
	{
		response.setContentType("application/xml");
		// check if user hanged up the call or if the recording gave an error
		String recUrl = request.getParameter("RecordingUrl");
		String recDuration = request.getParameter("RecordingDuration");
		if(recUrl == null || recUrl.isEmpty() || recDuration == null || recDuration.equals("0"))
		{
			close();
			return new VoiceResponse.Builder()
				.say(new Say.Builder(Speech.HANGUP.contents).build())
				.hangup(new Hangup.Builder().build())
				.build()
				.toXml();
		} 
		String recording = downloadRecording(recUrl); 
		String prompt = SpeechConverter.convertSpeechToText(recording);
		VoiceResponse.Builder voiceResponse = new VoiceResponse.Builder();
		if(numAnswers >= MAX_ANSWERS) 
		{
			close();
			voiceResponse
				.say(new Say.Builder(Speech.EXCEED.contents).build())
				.hangup(new Hangup.Builder().build());
		}
		else 
		{
			voiceResponse 
					.play(new com.twilio.twiml.voice.Play.Builder
					("https://api.twilio.com/cowbell.mp3") 
			        .loop(1).build())
				.say(new Say.Builder(bot.getAnswerTo(prompt).replaceAll("\\n", "")).build())
				.record(buildRecord());
		}
		numAnswers++;
		return voiceResponse.build().toXml().replaceAll("\\n", "");
	}
	
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

	private String downloadRecording(String recURL)
	{
		HttpURLConnection connection;
		
		try
		{
			connection = (HttpURLConnection) new URI(recURL).toURL().openConnection();
	        String auth = Phone.ACCOUNT_SID.contents + ":" + Phone.AUTH_TOKEN.contents;
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
		
		File outputFile = new File("recordings", Phone.PATH.contents);
		outputFile.getParentFile().mkdirs();
		
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

	// for any cleanup actions after phone hangup
	@Override
	public void close()
	{
		bot.clearHistory();
		File recording = new File(Phone.PATH.contents);
		recording.delete();
	}
}
