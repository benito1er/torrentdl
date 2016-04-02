package net.myonlinestuff.torrentdl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.core.env.Environment;
import org.springframework.scheduling.annotation.EnableScheduling;

import net.myonlinestuff.torrentdl.service.Parser;
import net.myonlinestuff.torrentdl.service.ShowIdentifier;



@SpringBootApplication
@EnableScheduling
public class Application implements CommandLineRunner {

	@Autowired
	Parser parser;

	@Autowired
	ShowIdentifier showIdentifier;

	@Autowired
	Environment env;

	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		
	}
}
