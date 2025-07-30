package tests;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;

import server.WaliBotApplication;

@SpringBootTest(classes = WaliBotApplication.class)
@ComponentScan(basePackages = {"server", "phone"})
class WaliBotApplicationTests
{
	@Test
	void contextLoads() 
	{
		
	}
}
