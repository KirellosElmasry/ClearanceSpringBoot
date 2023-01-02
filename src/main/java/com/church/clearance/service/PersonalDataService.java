package com.church.clearance.service;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Base64;
import java.util.Optional;

import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import com.church.clearance.config.HelperMethods;
import com.church.clearance.dao.PersonalDataDao;
import com.church.clearance.entities.PersonalData;
import com.church.clearance.model.PersonalDataRequest;
import com.church.clearance.model.ResultReturn;

@Service
public class PersonalDataService {

	@Autowired
	PersonalDataDao personalDataDao;

	@Autowired
	HelperMethods helper;

	public ResultReturn getPeronalData(String eid) {

		ResultReturn result = new ResultReturn();

		Optional<PersonalData> personal = personalDataDao.findById(eid);

		if (helper.validateEid(eid) != null) {

			return helper.validateEid(eid);
		}

		if (!personal.isPresent() ) {

			result.getRes().put("code", 200);
			result.getRes().put("msg", "welcome you can insert your data and create new clearance");

			return result;
		} else if (personal.get().getClearances() != null && personal.get().getClearances().size() > 0 && personal.get().getClearances()
				.get(personal.get().getClearances().size() - 1).getStatus().equalsIgnoreCase("Active")) {

			result.getRes().put("code", 201);
			result.getRes().put("msg", "welcome your clearance Already Exist And Active");

			return result;
		} else if (personal.get().getClearances() != null && personal.get().getClearances().size() > 0 && personal.get().getClearances()
				.get(personal.get().getClearances().size() - 1).getStatus().equalsIgnoreCase("Cancel")) {

			result.getRes().put("code", 202);
			result.getRes().put("msg", "welcome your prev clearance Already canceled, can create another");

			return result;
		} else if (personal != null && personal.get().getClearances().isEmpty()
				|| (personal.get().getClearances().size() > 0 && personal.get().getClearances()
						.get(personal.get().getClearances().size() - 1).getStatus().equalsIgnoreCase("Draft"))) {
			
			System.out.println(personal.get().getClearances());
			
			if(!personal.get().getClearances().isEmpty())
				result.getRes().put("clearances",personal.get().getClearances()
						.get(personal.get().getClearances().size() - 1));
			else				
				result.getRes().put("personalData", personal);
			
			result.getRes().put("code", 203);
			result.getRes().put("msg", "welcome you can create new clearance");
		} else {
			result.getRes().put("code", 4000);
			result.getRes().put("msg", "someThing wrong");
		}
		return result;
	}

	public ResultReturn addPeronalData(PersonalDataRequest personalDataRequest) {

		ResultReturn result = new ResultReturn();

		Optional<PersonalData> person = personalDataDao.findById(personalDataRequest.getEmirateId());
		
		if (!person.isPresent()) {
			
			person = Optional.of( new PersonalData());
			person.get().setBaptism(personalDataRequest.getBaptism());
			person.get().setBaptismPlace(personalDataRequest.getBaptismPlace());
			person.get().setBirthDate(personalDataRequest.getBirthDate());
			person.get().setBirthLocation(personalDataRequest.getBirthLocation());
			person.get().setEducation(personalDataRequest.getEducation());
			person.get().setEducationDate(personalDataRequest.getEducationDate());
			person.get().setEmirateId(personalDataRequest.getEmirateId());
			person.get().setName(personalDataRequest.getName());
			person.get().setPic(personalDataRequest.getPic());
			person.get().setStatus("Draft");
		} else if (person.get().getStatus().equals("Draft")) {
			person.get().setBaptism(personalDataRequest.getBaptism());
			person.get().setBaptismPlace(personalDataRequest.getBaptismPlace());
			person.get().setBirthDate(personalDataRequest.getBirthDate());
			person.get().setBirthLocation(personalDataRequest.getBirthLocation());
			person.get().setEducation(personalDataRequest.getEducation());
			person.get().setEducationDate(personalDataRequest.getEducationDate());
			person.get().setEmirateId(personalDataRequest.getEmirateId());
			person.get().setName(personalDataRequest.getName());
			person.get().setPic(personalDataRequest.getPic());
			person.get().setStatus("Draft");
		} else {
			result.getRes().put("code", 4000);

			result.getRes().put("PersonalData", "Already Exist");

			return result;
		}

		person.of(personalDataDao.save(person.get()));

		try {
			Resource f = helper.getFileAsResource("PersonalPhoto" + File.separator + personalDataRequest.getPic());

			byte[] fileContent = FileUtils.readFileToByteArray(f.getFile());
			String encodedString = Base64.getEncoder().encodeToString(fileContent);
			person.get().setPic(encodedString);

		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		result.getRes().put("code", 200);

		result.getRes().put("PersonalData", person);
		return result;

	}

}
