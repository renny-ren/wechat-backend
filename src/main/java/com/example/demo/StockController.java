package com.example.demo;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class StockController {
	@RequestMapping("/view/{id}")
	public Stock viewStock(@PathVariable int id) {
		Stock s1 = new Stock();
		s1.name = "zgsy";
		s1.price = 11.88f;
		return s1;
	}
}
