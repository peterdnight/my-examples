package org.sample.bootdemo;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController("/api")
public class MyRestApi {
	
	@GetMapping("/hi")
	public String hi() {
		return "hi" ;
	}

}
