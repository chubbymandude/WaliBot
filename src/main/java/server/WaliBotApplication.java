package server;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

// Following class runs the server for this application via Spark
// change dir: cd /Users/abdurrafayatif/Downloads/WaliBotFiles/WaliBotMaven\ 2
// run with: ngrok http --domain=assured-equally-viper.ngrok-free.app 8080
@SpringBootApplication
@ComponentScan({"server", "phone"})
public class WaliBotApplication 
{
	public static void main(String[] args)
	{
		SpringApplication.run(WaliBotApplication.class, args);
    }
}
