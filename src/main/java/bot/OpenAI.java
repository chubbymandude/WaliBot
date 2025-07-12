package bot;

/*
 * Constant values for using OpenAI
 */
enum OpenAI 
{
	//URL that connects this application to OpenAI
	URL_LINK("https://api.openai.com/v1/chat/completions"), 
	//Specialized access key for this application
	KEY("NOT-AVAILABLE"), 
	//Model of ChatGPT used for this application
	MODEL("gpt-3.5-turbo"), 
	//For use in embedding
	EMBEDDING_LINK("https://api.openai.com/v1/embeddings");
	
	final String contents; 
	
	OpenAI(String contents)
	{
		this.contents = contents;
	}
}	
