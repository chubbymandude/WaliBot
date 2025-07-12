package bot;

/*
 * Database Constants and Used Queries
 */
public enum Database 
{
	//Link to the database
	URL("jdbc:postgresql://localhost:5432/Masjid"), 
	//Username + Password
	USERNAME("abdurrafayatif"), PASSWORD("S1p2ongeBob*"),
	//Query to be used in ChatBot
	QUERY("SELECT question, answer\n"
		+ "FROM dataset\n"
		+ "ORDER BY embedding <#> ? \n"
		+ "LIMIT 1;"),
	//Query for saving embeddings to the database
	SAVE_EMBEDDING("INSERT INTO dataset (question, embedding)\n"
		+ "VALUES (?, ?)\n"
		+ "ON CONFLICT (question) DO UPDATE\n"
		+ "SET embedding = EXCLUDED.embedding"),
	//Obtain the prompt in order to create and save its embedding
	GET_PROMPT("SELECT question\n"
			+ "FROM dataset\n"
			+ "WHERE id = ?"),
	//Gets row-count of table
	GET_NUM_ROWS("SELECT COUNT(1) FROM dataset");
	
	final String contents;
	
	Database(String contents)
	{
		this.contents = contents;
	}
}
