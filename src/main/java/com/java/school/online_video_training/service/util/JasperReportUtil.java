package com.java.school.online_video_training.service.util;

import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

@Component
public class JasperReportUtil {

	public byte[] generateReport(List<?> data, String reportName, Map<String, Object> params) throws Exception {

	    InputStream reportStream = new ClassPathResource("reports/" + reportName + ".jrxml").getInputStream();

	    JasperReport jasperReport = JasperCompileManager.compileReport(reportStream);

	    JRBeanCollectionDataSource dataSource = new JRBeanCollectionDataSource(data);

	    JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, params, dataSource);

	    return JasperExportManager.exportReportToPdf(jasperPrint);
	}
}