package com.mycorp.arithmeticcalculator;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.PropertySource;

@SpringBootApplication
@PropertySource({"classpath:application.properties"})
public class ArithmeticCalculatorApplication {

	public static void main(String[] args) {
		SpringApplication.run(ArithmeticCalculatorApplication.class, args);
	}

}
