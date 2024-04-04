package com.mycorp.arithmeticcalculator;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableScheduling;

@Configuration
@EnableScheduling
@ComponentScan({ "com.mycorp.arithmeticcalculator.task" })
public class SpringTaskConfig {

}