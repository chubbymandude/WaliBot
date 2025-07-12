package testing;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;

import speech.SpeechConverter;

/*
 * Test STT functionality with an example audio file
 */
public class TestSpeechToText 
{
	public static void main(String[] args)
	{
		//Redirect output/error
		try
		(
			//Output
            FileOutputStream fileStreamOut = new FileOutputStream("output.txt");
            PrintStream outputStream = new PrintStream(fileStreamOut);
            
            //Error
            FileOutputStream fileStreamErr = new FileOutputStream("error.txt");
            PrintStream errorStream = new PrintStream(fileStreamErr);
		)
		{
			System.setOut(outputStream);
			System.setErr(errorStream);
			String text = SpeechConverter.convertSpeechToText
			("/Users/abdurrafayatif/Downloads/Audio Files/harvard.wav");
			System.out.println(text);
		}
		catch(FileNotFoundException e)
		{
			System.err.println("File was not found...");
		}
		catch(IOException e)
		{
			System.err.println("Some I/O error occurred while redirecting...");
		}
	}
}
