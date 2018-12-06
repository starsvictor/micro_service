package com.zl.bf.security.manager;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan("com.zl.bf.security")
public class ManagerAppliaction {
	
	public static void main(String[] args) {
		SpringApplication.run(ManagerAppliaction.class, args);
	}

}
