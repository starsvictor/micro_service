package com.zl.bf.security.manager.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {

	@RequestMapping("/user")
	public String getUser(){
		return "user....";
	}
}
