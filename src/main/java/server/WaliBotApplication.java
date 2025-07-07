package server;

import static spark.Spark.*;

import bot.*;
import speech.*;

/*
 * Following class runs the server for this application via Spark
 */
public class WaliBotApplication 
{

	@SuppressWarnings("unused")
	public static void main(String[] args) 
	{
		port(4567); //This port is used for accessing this server
		
		//Create a new ChatBot() object that will run until the user exits
		ChatBot bot = new ChatBot();
		
		/*
		 * Setup recording so twilio can obtain questions
		 * Send STARTUP message
		 */
		post("/twilio/call", (request, response) -> 
		{
		    response.type("text/xml");

		    return startRecording();
		});
		
		//Obtain user prompts from the call
		post("/twilio/process-recording", (request, response) -> 
		{
            response.type("text/xml");

            //Obtain URL for voice recording
            String promptAudioURL = request.queryParams("RecordingUrl");
            
            //Error-handling for URL record
            if(promptAudioURL == null) 
            {
                return "<Response><Say>" + Speech.GET_FAIL + "</Say></Response>";
            }

            //Convert file for use in Vosk STT
            String promptRecording = promptAudioURL + ".wav";

            //Get answer from ChatGPT
            String prompt = SpeechConverter.convertSpeechToText(promptRecording);
            String answer = bot.getAnswerTo(prompt);
            
            return null;
        });
    }
	
	//XML code for starting a recording is started via this method
	public static String startRecording()
	{
		return "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" 
			+ "<Response><Say voice=\"alice\">" + Speech.STARTUP.contents
			+ "</Say>  <Record action=\"/twilio/process-recording\" method="
			+ "\"POST\" maxLength=\"10\"/></Response>";
	}
	
	//XML code for getting a response from the ChatBot
	public static String sayResponse(String response)
	{
		return "<Response>\n"
			+ "<Say voice=\"alice\"> " + response + "</Say>\n"
			+ "</Response>";
	}
}
