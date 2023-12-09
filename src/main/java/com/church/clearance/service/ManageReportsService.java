package com.church.clearance.service;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.church.clearance.dao.ClearanceDao;
import com.church.clearance.dao.UsersDao;
import com.church.clearance.entities.Clearance;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.engine.util.JRSaver;

@Service
public class ManageReportsService {

	@Autowired
	UsersDao usersDao;

	@Autowired
	ClearanceDao clearanceDao;

	public void generateClearanceForm(String refNo, HttpServletResponse response) {

		Clearance clearance = clearanceDao.findByRefNo(refNo);

		List<Clearance> clearances = new ArrayList<>();
		clearances.add(clearance);

		prepareReport(clearances, response);
	}

	private void prepareReport(List<Clearance> clearance, HttpServletResponse response) {
		try {
			InputStream employeeReportStream = getClass().getResourceAsStream("/reports/ClearanceForm.jrxml");
			System.out.println("employeeReportStream "+employeeReportStream.available());
			
			JasperReport jasperReport = JasperCompileManager.compileReport(employeeReportStream);
			System.out.println("jasperReport "+jasperReport.getName() + "  get current location: "+ System.getProperty("user.dir"));
			
			JRSaver.saveObject(jasperReport, "./src/main/resources/reports/ClearanceForm.jasper");
			System.out.println("After saveObject ");
			
			JRBeanCollectionDataSource dataSource = new JRBeanCollectionDataSource(clearance);

			String kindOfMarriage = "";
			Integer childsNumber = 0;

			if (!clearance.isEmpty() && clearance.get(0).getPreviousMarriages() != null
					&& !clearance.get(0).getPreviousMarriages().isEmpty()) {

				if (clearance.get(0).getPreviousMarriages().get(0).getKindOfMarriage() != null)
					kindOfMarriage = clearance.get(0).getPreviousMarriages().get(0).getKindOfMarriage();

			}

			if (clearance.get(0).getChilds() != null)
				childsNumber = clearance.get(0).getChilds().size();

			Map<String, Object> parameters = new HashMap<>();
			parameters.put("title", "Clearance Form");
			parameters.put("kindOfMarriage", kindOfMarriage);
			parameters.put("childsNumber", childsNumber);

			if (clearance.get(0).getMilitaryService() != null) {
				if (clearance.get(0).getMilitaryService().equals("0"))
					clearance.get(0).setMilitaryService("Finished");
				else if (clearance.get(0).getMilitaryService().equals("1"))
					clearance.get(0).setMilitaryService("postponed");
				else if (clearance.get(0).getMilitaryService().equals("2"))
					clearance.get(0).setMilitaryService("Exemption");
				else if (clearance.get(0).getMilitaryService().equals("3"))
					clearance.get(0).setMilitaryService("Inappropriate");
				else
					clearance.get(0).setMilitaryService("");
			}
			
			JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, dataSource);

			exportPDF(jasperPrint, response);
			System.out.println("after exporting pdf");
		} catch (JRException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void exportPDF(JasperPrint jasperPrint, HttpServletResponse response) {
		try {
			JasperExportManager.exportReportToPdfStream(jasperPrint, response.getOutputStream());
			response.setContentType("application/pdf");
			response.addHeader("Content-Disposition", "inline; filename=jasper.pdf;");

		} catch (JRException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
}
