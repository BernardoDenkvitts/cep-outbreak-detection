package com.tcc.epidemiologia;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.retry.annotation.EnableRetry;
import org.springframework.scheduling.annotation.EnableAsync;


@SpringBootApplication
@EnableAsync
@EnableRetry
public class EpidemiologiaApplication implements CommandLineRunner {

	public static void main(String[] args) {
		SpringApplication.run(EpidemiologiaApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {

	}
}
