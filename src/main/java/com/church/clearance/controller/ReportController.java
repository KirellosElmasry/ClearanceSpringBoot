package com.church.clearance.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ReportController {

	@GetMapping("/report")
	public ResponseEntity<byte[]> generateReport(){
		
		
		return null;
	}
}
