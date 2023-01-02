package com.church.clearance.service;

import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.church.clearance.dao.ClearanceDao;
import com.church.clearance.dao.UsersDao;
import com.church.clearance.entities.Child;
import com.church.clearance.entities.Clearance;
import com.church.clearance.entities.Users;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.engine.export.JRPdfExporter;
import net.sf.jasperreports.engine.util.JRSaver;
import net.sf.jasperreports.export.SimpleExporterInput;
import net.sf.jasperreports.export.SimpleOutputStreamExporterOutput;
import net.sf.jasperreports.export.SimplePdfExporterConfiguration;
import net.sf.jasperreports.export.SimplePdfReportConfiguration;

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
			InputStream employeeReportStream = getClass().getResourceAsStream("/reports/clearanceReport.jrxml");

			JasperReport jasperReport = JasperCompileManager.compileReport(employeeReportStream);

			JRSaver.saveObject(jasperReport, "./src/main/resources/reports/clearanceReport.jasper");

			JRBeanCollectionDataSource dataSource = new JRBeanCollectionDataSource(clearance);

			Map<String, Object> parameters = new HashMap<>();
			parameters.put("title", "Clearance Form");
			// parameters.put("userName", "kirellos");

			JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, dataSource);

			exportPDF(jasperPrint, response);

		} catch (JRException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void exportReport(JasperPrint jasperPrint) {
		JRPdfExporter exporter = new JRPdfExporter();

		exporter.setExporterInput(new SimpleExporterInput(jasperPrint));
		exporter.setExporterOutput(
				new SimpleOutputStreamExporterOutput("./src/main/resources/reports/clearanceReport.pdf"));

		SimplePdfReportConfiguration reportConfig = new SimplePdfReportConfiguration();
		reportConfig.setSizePageToContent(true);
		reportConfig.setForceLineBreakPolicy(false);

		SimplePdfExporterConfiguration exportConfig = new SimplePdfExporterConfiguration();
		exportConfig.setMetadataAuthor("baeldung");
		exportConfig.setEncrypted(true);
		exportConfig.setAllowedPermissionsHint("PRINTING");

		exporter.setConfiguration(reportConfig);
		exporter.setConfiguration(exportConfig);

		try {
			exporter.exportReport();
		} catch (JRException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private void exportPDF(JasperPrint jasperPrint, HttpServletResponse response)  {
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
