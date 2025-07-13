package server;

import static spark.Spark.*;

/*
 * Following class runs the server for this application via Spark
 */
public class WaliBotApplication 
{
	public static void main(String[] args) 
	{
		port(4567);
		PhoneSystem phone = new PhoneSystem();
		
		get("/voice", (request, response) -> 
		{
			return phone.startupMessage(request, response);
		});
		
		post("/process", (request, response) ->
		{
			return phone.messageLoop(request, response);
		});
    }
}
