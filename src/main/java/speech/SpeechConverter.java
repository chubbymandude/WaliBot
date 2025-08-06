package speech;

import java.io.File;
import java.io.IOException;

import bot.OpenAI;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

// uses Vosk model to perform speech to text when the user's voice recording is obtained
public class SpeechConverter
{
	// uses Whisper to convert an audio file to text in the form of a String
	public static String convertSpeechToText(String speech)
	{
		// setup client and audio configurations
		OkHttpClient client = new OkHttpClient();
		MediaType type = MediaType.parse("audio/mpeg");
		File audio = new File(speech);
		
		// make request body for Whisper API
		RequestBody requestBody = new MultipartBody.Builder().setType(MultipartBody.FORM)
			.addFormDataPart("file", audio.getName(), RequestBody.create(audio, type))
			.addFormDataPart("model", "whisper-1")
			.addFormDataPart("response_format", "text").build();
		
		// form POST request
		Request request = new Request.Builder()
		    .url("https://api.openai.com/v1/audio/transcriptions") 
		    .post(requestBody)
		    .addHeader("Authorization", "Bearer " + OpenAI.KEY.get())
		    .build();
		
		// obtain the text converted from the audio file
		try
		{
			Response response = client.newCall(request).execute();
			return response.body().string();
		}
		catch(IOException e)
		{
			System.err.println("I/O exception converting speech to text...");
			e.printStackTrace();
			return null;
		}
	}
}