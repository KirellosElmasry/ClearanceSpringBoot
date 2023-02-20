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

			JasperReport jasperReport = JasperCompileManager.compileReport(employeeReportStream);

			JRSaver.saveObject(jasperReport, "./src/main/resources/reports/ClearanceForm.jasper");

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

			JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, dataSource);

			exportPDF(jasperPrint, response);

		} catch (JRException e) {
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
