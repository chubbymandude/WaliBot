package bot;

enum OpenAI 
{
	KEY(System.getenv("GPT_KEY")), 
	EMBEDDING_LINK("https://api.openai.com/v1/embeddings");
	
	final String contents; 
	
	OpenAI(String contents)
	{
		this.contents = contents;
	}
}	
