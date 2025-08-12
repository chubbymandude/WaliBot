package server;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.twilio.twiml.VoiceResponse;
import com.twilio.twiml.voice.Hangup;
import com.twilio.twiml.voice.Say;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import speech.Speech;

// class that has various methods for handling get / post requests
@RestController
public class RequestHandling 
{
	private PhoneSystem phone;
	
	@Autowired
	public RequestHandling(PhoneSystem phone)
	{	
		this.phone = phone;
	}
	
	@GetMapping("/voice")
	public String handleStartup(HttpServletRequest request, HttpServletResponse response)
	{
		return phone.startupMessage(request, response);
	}
	
	@RequestMapping(value = "/process", method = RequestMethod.POST)
	public String handleLoop(HttpServletRequest request, HttpServletResponse response)
	{
		try
		{
			return phone.messageLoop(request, response);
		}
		catch(Exception e) // if the program is not working for any reason immediately hang up
		{
			e.printStackTrace();
			return new VoiceResponse.Builder()
				.say(new Say.Builder(Speech.ERROR.get()).build())
				.hangup(new Hangup.Builder().build())
				.build()
				.toXml();
		}
	}
}
