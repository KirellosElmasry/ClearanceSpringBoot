package com.church.clearance.service;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
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
import net.sf.jasperreports.engine.JRExporterParameter;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.engine.export.ooxml.JRDocxExporter;
import net.sf.jasperreports.engine.export.ooxml.JRDocxExporterParameter;
import net.sf.jasperreports.engine.util.JRLoader;

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

//			InputStream employeeReportStream = getClass().getResourceAsStream("/reports/ClearanceForm.jrxml");
//			System.out.println("employeeReportStream "+employeeReportStream.available());

//			JasperDesign jasperDesign = JRXmlLoader.load(employeeReportStream);

			InputStream jasperStream = getClass().getResourceAsStream("/reports/ClearanceForm.jasper");

			JasperReport jasperReport = (JasperReport) JRLoader.loadObject(jasperStream);
//			jasperReport.getDefaultStyle().setFontName("DejaVu Sans");

//			JasperReport jasperReport = JasperCompileManager.compileReport(jasperDesign);

//			System.out.println("jasperReport "+jasperReport.getName() + "  get current location: "+ System.getProperty("user.dir"));

			// JRSaver.saveObject(jasperReport, "/reports/ClearanceForm.jasper"); // to run
			// from jar absolute path
			// JRSaver.saveObject(jasperReport,new File("ClearanceForm.jasper")); // to run
			// from eclipse relative path

			System.out.println("*************** After saveObject ");

			JRBeanCollectionDataSource dataSource = new JRBeanCollectionDataSource(clearance);
			System.out.println("*************** After JRBeanCollectionDataSource ");

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
			System.out.println("*************** before jasperPrint ");
			JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, dataSource);
			System.out.println("*************** After jasperPrint ");
			// exportPDF(jasperPrint, response);
			exportWord(jasperPrint, response);
			System.out.println("after exporting pdf");
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

	private void exportWord(JasperPrint jasperPrint, HttpServletResponse response) {
		try {
			response.setContentType("application/vnd.openxmlformats-officedocument.wordprocessingml.document");
			response.setHeader("Content-Disposition", "attachment; filename=clearance_report.docx");

			OutputStream outputStream = response.getOutputStream(); // Get the output stream from response

			JRDocxExporter docxExporter = new JRDocxExporter();
			docxExporter.setParameter(JRExporterParameter.JASPER_PRINT, jasperPrint);
			docxExporter.setParameter(JRExporterParameter.OUTPUT_STREAM, outputStream);
	        docxExporter.setParameter(JRDocxExporterParameter.FLEXIBLE_ROW_HEIGHT, Boolean.TRUE); // Ensure flexible row height
	        docxExporter.setParameter(JRDocxExporterParameter.CHARACTER_ENCODING, "UTF-8"); // Set UTF-8 encoding

			docxExporter.exportReport(); // Export the report

			outputStream.flush(); // Flush the output stream
			outputStream.close(); // Close the output stream

		} catch (JRException e) {
			// Handle JRException
			e.printStackTrace();
		} catch (IOException e) {
			// Handle IOException
			e.printStackTrace();
		}
	}

}
