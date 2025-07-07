package speech;

import java.io.*;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;

import org.json.JSONException;
import org.json.JSONObject;

/*
 * Utility class for both STT and TTS; 
 * used for complicated functionality
 * that may warrant their own methods
 */
public class SpeechConverterUtils 
{
	//Creates stream for specified speech file
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
	
	/*
	 * Obtains JSON String while converting speech to text
	 * in order to obtain String representation of audio
	 */
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
			return Speech.ERROR.contents;
		}
	}

}
