package speech;

// special messages used by the ChatBot that don't come from the database
public enum Speech 
{
	ERROR("Sorry, I currently cannot answer any questions. Allah-hafiz."), // for bugs
	GET_FAIL("Sorry, I was not able to get your response. Please try again."), // chatbot issues
	NO_DATA("Sorry, I cannot answer this question. Please try again."), // database issues
	STARTUP("Assalam-mu-alaikum! Welcome to the Masjid Al-Wali Chat Service. "
		+ "Ask any question related to the Masjid here!"
		+ "You are allowed at most 5 questions per call."),
	END("Thank you for using the Masjid Al-Wali Chat Service. Allah-hafiz.");
	
	private final String contents; 
	
	Speech(String contents)
	{
		this.contents = contents;
	}
	
	public String get()
	{
		return this.contents;
	}
}
