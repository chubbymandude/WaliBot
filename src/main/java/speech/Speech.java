package speech;

/*
 * Following enum consists of constant Strings that can be
 * used for any special circumstances relating to voice speech
 */
public enum Speech 
{
	//If the Bot is buggy or is undergoing maintenance this message prints
	ERROR("Sorry, I currently cannot answer any questions. "
	+ "Try again another time."),
	//If the Bot fails to catch a message
	GET_FAIL("Sorry, I was not able to get your response. Please try again."),
	//Used when the question cannot be answered based on the dataset
	NO_DATA("Sorry, I cannot answer this question. Please try again."),
	//Start-Up message for ChatBot
	STARTUP("Assalam-mu-alaikum! Welcome to the Masjid Al-Wali Chat Service. "
		+ "Ask any question related to the Masjid here! "
		+ "You are allowed at most 10 questions per call. "
		+ "Say the word quit when you are done."), 
	//Message sent when the user ends the call
	END("Thank you for using the Masjid Al-Wali Chat Service. Allah-hafiz."),
	//When # of answers exceeds 10
	EXCEED("Sorry, I cannot answer any more questions. Allah-hafiz."),
	//If the user hangs up without saying "quit"
	HANGUP("Sorry, I was not able to get your response. Allah-hafiz.");
	
	public final String contents; 
	
	Speech(String contents)
	{
		this.contents = contents;
	}
}
