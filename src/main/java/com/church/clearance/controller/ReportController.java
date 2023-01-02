package com.church.clearance.controller;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.church.clearance.service.ManageReportsService;

@RestController
public class ReportController {

	@Autowired
	ManageReportsService manageReportsService;

	@CrossOrigin
	@RequestMapping(value = "/api/v1/generateClearanceForm", method = RequestMethod.GET)
	public void generateClearanceForm(@RequestParam String refNo, HttpServletResponse response) {

		manageReportsService.generateClearanceForm(refNo, response);

	}
}
