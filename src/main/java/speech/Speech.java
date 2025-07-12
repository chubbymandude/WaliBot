package speech;

/*
 * Following enum consists of constant Strings that can be
 * used for any special circumstances relating to 
 */
public enum Speech 
{
	//If the Bot is buggy or is undergoing maintenance this message prints
	ERROR("Sorry, I currently cannot answer any questions. "
	+ "Try again another time."),
	//If the Bot fails to catch a message
	GET_FAIL("Sorry, I was not able to get your response. Please try again."),
	//Start-Up message for ChatBot
	STARTUP("Welcome to the Masjid Al-Wali Chat Service. "
	+ "Ask any question related to the Masjid here!"), 
	//Used when the question cannot be answered based on the dataset
	NO_DATA("Sorry, I cannot answer this question. Please try again.");
	
	public final String contents; 
	
	Speech(String contents)
	{
		this.contents = contents;
	}
}
