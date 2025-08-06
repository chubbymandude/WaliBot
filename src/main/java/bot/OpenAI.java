package bot;

public enum OpenAI 
{
	KEY(System.getenv("GPT_KEY")), EMBEDDING_MODEL("text-embedding-3-small"),
	EMBEDDING_LINK("https://api.openai.com/v1/embeddings");
	
	private final String contents; 
	
	OpenAI(String contents)
	{
		this.contents = contents;
	}
	
	public String get()
	{
		return contents;
	}
}	
