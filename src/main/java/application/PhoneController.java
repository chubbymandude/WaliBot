package application;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.twilio.twiml.VoiceResponse;
import com.twilio.twiml.voice.Hangup;
import com.twilio.twiml.voice.Say;

import application.PhoneSystem.PhoneException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

// class that has various methods for handling get / post requests
@RestController
public class PhoneController 
{
	private PhoneSystem phone;
	
	@Autowired
	public PhoneController(PhoneSystem phone)
	{	
		this.phone = phone;
	}
	
	@GetMapping("/voice")
	public String handleStartup(HttpServletRequest request, HttpServletResponse response)
	{
		return phone.startupMessage(request, response);
	}
	
	@PostMapping("/process")
	public String handleLoop(HttpServletRequest request, HttpServletResponse response)
	{
		try
		{
			return phone.messageLoop(request, response);
		}
		catch(PhoneException e) // if the program is not working for any reason immediately hang up
		{
			e.printStackTrace();
			return new VoiceResponse.Builder()
				.say(new Say.Builder(e.getMessage()).build())
				.hangup(new Hangup.Builder().build())
				.build()
				.toXml();
		}
	}
}
