package bot;

/*
 * Constant values for using OpenAI
 */
enum OpenAI 
{
	//URL that connects this application to OpenAI
	URL_LINK("https://api.openai.com/v1/chat/completions"), 
	//Specialized access key for this application
	KEY("sk-svcacct-F59p_iXxfWShN98Y9r_5_mJbO"
	+ "l6B6cttM84fCUqT1OrSIJ1yziAoOB1pYcAHB9doXjtNgqrWPJT3BlbkFJpjTxLui1pFQbjyV"
	+ "23bkC2RGMpaGQiWf6iA23s9RTYGLW3oQ4-J3nJc4AKJXYtHzjA3qHj727YA"), 
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