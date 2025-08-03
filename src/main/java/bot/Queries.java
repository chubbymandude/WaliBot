package bot;

public enum Queries 
{
	URL(System.getenv("DB_URL")), 
	USERNAME(System.getenv("DB_USERNAME")), PASSWORD(System.getenv("DB_PASSWORD")),
	GET_ANSWER("SELECT question, answer, 1 - (embedding <#> ?) AS accuracy\n"
		+ "FROM dataset\n"
		+ "ORDER BY embedding <#> ? \n"
		+ "LIMIT 1;"),
	SAVE_EMBEDDING("INSERT INTO dataset (question, embedding)\n"
		+ "VALUES (?, ?)\n"
		+ "ON CONFLICT (question) DO UPDATE\n"
		+ "SET embedding = EXCLUDED.embedding"),
	GET_PROMPT("SELECT question\n"
		+ "FROM dataset\n"
		+ "WHERE id = ?"),
	GET_NUM_ROWS("SELECT COUNT(1) FROM dataset");
	
    private final String contents;
	
	Queries(String contents)
	{
		this.contents = contents;
	}
	
	String get()
	{
		return contents;
	}
}
