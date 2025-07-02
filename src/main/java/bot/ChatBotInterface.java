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
	 * This method should also make use of the prompt history by the user, 
	 * so ChatGPT can create a somewhat more personalized response
	 * based on how the user's prompts usually are 
	 * 
	 * @param data --> data obtained from the method getData()
	 */
	public String getFormalAnswer(String prompt);
	
	/*
	 * Obtains data necessary for ChatGPT to make a response
	 * based on data that is provided about Masjid Al-Wali
	 */
	public String getData();
	
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
	 * If the user does not want their prompt history
	 * with ChatGPT anymore, this method can be used
	 * to clear out responses.txt so there is nothing
	 * in that file
	 */
	public void clearHistory();
}