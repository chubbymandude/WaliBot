package speech;

import java.io.IOException;
import java.io.InputStream;
import org.vosk.*;

/*
 * This class is used to perform various functionality
 * that is required by the PhoneSystem in order
 * to for the ChatBot to work with it
 */
public class SpeechConverter
{
	/*
	 * Converts speech obtained from an audio file 
	 * to a String of text that can be used
	 * as a prompt for the ChatBot
	 * 
	 * This is done using the Vosk library
	 */
	public static String convertSpeechToText(String speech)
	{
		LibVosk.setLogLevel(LogLevel.DEBUG); 
		
		try
		(
			Model model = new Model
			("src/main/resources/vosk-model-small-en-us-0.15");
				
			Recognizer recognizer = new Recognizer(model, 16000);
		)
		{ 
			InputStream inputStream = SpeechConverterUtils.setStream(speech);
			//Check if the input stream initialization was successful
			if(inputStream == null)
			{
				return Speech.ERROR.contents;
			}
			
			//Read from stream and store in a StringBuilder
			byte[] buffer = new byte[4096]; 
			int numBytes;
			StringBuilder text = new StringBuilder(); 
			
			while((numBytes = inputStream.read(buffer)) >= 0)
			{
				if(recognizer.acceptWaveForm(buffer, numBytes))
				{
					text.append(SpeechConverterUtils
					.getTextFromJSON(recognizer.getResult())).append(" ");
				}
			}
			
			text.append(SpeechConverterUtils.
			getTextFromJSON(recognizer.getFinalResult()));
			
			return text.toString().trim();
		}
		catch(IOException e)
		{
			System.err.println("I/O exception while creating model...");
			/*
			 * Print message that the Bot is not working right now
			 */
			return Speech.ERROR.contents;
		}
	}

	/*
	 * After the ChatBot has processed the prompt, its
	 * response can be converted to speech via
	 * this method which converts text to speech
	 */
	public static String convertTextToSpeech(String text) 
	{
		return null;
	}
	
}