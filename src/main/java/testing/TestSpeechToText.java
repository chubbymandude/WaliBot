package testing;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;

import speech.SpeechConverter;

/*
 * Test STT functionality with an example audio file
 */
public class TestSpeechToText 
{
	public static void main(String[] args)
	{
		//Redirect output and error
		try
		{
			//Output
            FileOutputStream fileStreamOut = new FileOutputStream("output.txt");
            PrintStream outputStream = new PrintStream(fileStreamOut);
            System.setOut(outputStream);
            
            //Error
            FileOutputStream fileStreamErr = new FileOutputStream("error.txt");
            PrintStream errorStream = new PrintStream(fileStreamErr);
            System.setErr(errorStream);
            
		}
		catch(FileNotFoundException e)
		{
			System.err.println("File was not found...");
		}
		String text = SpeechConverter.convertSpeechToText
		("/Users/abdurrafayatif/Downloads/Audio Files/harvard.wav");
		System.out.println(text);
	}
}
