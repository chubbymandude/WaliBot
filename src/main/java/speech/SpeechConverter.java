package speech;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;

import org.json.JSONException;
import org.json.JSONObject;
import org.vosk.*;

// uses Vosk model to perform speech to text when the user's voice recording is obtained
public class SpeechConverter
{
	private static Model MODEL;
	
	static
	{
		try
		{
			MODEL = new Model("src/main/resources/vosk-model-small-en-us-0.15");
		}
		catch(IOException e)
		{
			System.err.println("I/O exception while creating model...");
		}
	}
	
	// Speech-To-Text via Vosk for more effective and efficient processing
	public static String convertSpeechToText(String speech)
	{
		LibVosk.setLogLevel(LogLevel.DEBUG); 
		
		try
		(
			Recognizer recognizer = new Recognizer(MODEL, 16000);
		)
		{ 
			InputStream inputStream = setStream(speech);
			
			if(inputStream == null)
			{
				return null;
			}
			
			// write bytes from input stream into textual form
			byte[] buffer = new byte[4096]; 
			int numBytes;
			StringBuilder text = new StringBuilder(); 
			
			while((numBytes = inputStream.read(buffer)) >= 0)
			{
				if(recognizer.acceptWaveForm(buffer, numBytes))
				{
					text.append(getTextFromJSON(recognizer.getResult())).append(" ");
				}
			}
			
			text.append(getTextFromJSON(recognizer.getFinalResult()));
			
			return text.toString().trim();
		}
		catch(IOException e)
		{
			System.err.println("I/O exception while creating model...");
			return null;
		}
	}
	
	// creates stream for specified speech file
	static AudioInputStream setStream(String speech) 
	{
		AudioInputStream audioInput;
		try
		{
			audioInput = AudioSystem.getAudioInputStream(new File(speech));
			
			AudioFormat format = new AudioFormat
			(AudioFormat.Encoding.PCM_SIGNED, 16000, 16, 1, 2, 16000, false);
			
			audioInput = AudioSystem.getAudioInputStream(format, audioInput);
		}
		catch(UnsupportedAudioFileException e)
		{
			System.err.println("Audio file is unsupported...");
			return null;
		}
		catch(IOException e)
		{
			System.err.println("I/O Exception while getting stream...");
			return null;
		}
		
		return audioInput;
	}
	
	// JSON string value conversion to use for speech
	static String getTextFromJSON(String jsonText)
	{
		try
		{
			JSONObject jsonObject = new JSONObject(jsonText);
			return jsonObject.getString("text");
		}
		catch(JSONException e)
		{
			System.err.println("Error converting JSON to text...");
			return null;
		}
	}
}