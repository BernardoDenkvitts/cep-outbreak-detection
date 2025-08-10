package com.tcc.epidemiologia;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.retry.annotation.EnableRetry;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;


@SpringBootApplication
@EnableAsync
@EnableRetry
@EnableScheduling
public class EpidemiologiaApplication {

	public static void main(String[] args) {
		SpringApplication.run(EpidemiologiaApplication.class, args);
	}
}
