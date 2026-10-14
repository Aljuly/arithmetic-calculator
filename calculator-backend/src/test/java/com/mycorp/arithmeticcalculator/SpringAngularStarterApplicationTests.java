package com.mycorp.arithmeticcalculator;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import com.mycorp.springangularstarter.config.TestDbConfig;

@SpringBootTest(classes = {ArithmeticCalculatorApplication.class, TestDbConfig.class})
class SpringAngularStarterApplicationTests {

	@Test
	void contextLoads() {
	}

}
