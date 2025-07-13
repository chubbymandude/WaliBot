package speech;

import java.io.IOException;
import java.io.InputStream;
import org.vosk.*;

/*
 * This class is the base class for conversion of
 * Speech to Text, fundamental in the
 * functionality of the voice ChatBot
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
}