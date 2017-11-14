package com.example.demo;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/crime")
public class CrimeController {
	
	public String addCrime(Crime c) {
		return "{success: true}";
	}
}
