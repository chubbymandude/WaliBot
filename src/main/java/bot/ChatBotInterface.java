package bot;

/*
 * Required elements of the ChatBot
 */
public interface ChatBotInterface 
{	
	/*
	 * Obtains an answer to a prompt that is specialized to
	 * make specific answers related to Masjid Al-Wali
	 * 
	 * This is the method that is ultimately called 
	 * whenever the user sends a prompt, and makes
	 * use of the other methods in this interface
	 * for functionality
	 * 
	 * @param prompt --> The user's prompt to ChatGPT
	 */
	public String getAnswerTo(String prompt);
	
	/*
	 * Obtains a formal answer from ChatGPT based on the data collected
	 * from the getData() method.
	 * 
	 * Note that although formal, the answer should be concise, as
	 * the answer is being sent through a phone line. 
	 * 
	 * @param prompt --> prompt to base formal answer off of
	 */
	public String getFormalAnswer(String prompt);
	
	/*
	 * Obtains data necessary for ChatGPT to make a response
	 * based on data that is provided about Masjid Al-Wali
	 */
	public String getData(String prompt);
	
	/*
	 * 	Method used to save contents of all prompt/response 
	 * 	history. This is necessary in order to make
	 *  more personalized responses from the ChatBot to the user
	 *  
	 *  @param prompt --> The user's prompt
	 *  @param answer --> The ChatBot's answer to the prompt
	 */
	public void saveResponse(String prompt, String answer);
	
	/*
	 * Method used to obtain previous prompt-answer
	 * history during the current ChatBot session
	 * in order to make the ChatBot remember previous
	 * responses
	 */
	public String loadResponses();
	
	/*
	 * Used to clear responses.txt after the user
	 * has ended the call with the ChatBot
	 * (so it can be used by other users)
	 */
	public void clearHistory();
}