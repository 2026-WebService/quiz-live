package com.seohamin.quiz_live;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class QuizLiveApplication {

	public static void main(String[] args) {
		SpringApplication.run(QuizLiveApplication.class, args);
	}

}
