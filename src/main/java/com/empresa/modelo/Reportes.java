package com.empresa.modelo;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.sql.Connection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import javax.faces.context.FacesContext;
import javax.servlet.ServletContext;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRParameter;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperPrintManager;
import net.sf.jasperreports.engine.export.HtmlExporter;
import net.sf.jasperreports.engine.export.JRCsvExporter;
import net.sf.jasperreports.engine.export.JRPdfExporter;
import net.sf.jasperreports.engine.export.JRXmlExporter;
import net.sf.jasperreports.engine.export.ooxml.JRDocxExporter;
import net.sf.jasperreports.engine.export.ooxml.JRXlsxExporter;
import net.sf.jasperreports.export.Exporter;
import net.sf.jasperreports.export.SimpleExporterInput;
import net.sf.jasperreports.export.SimpleHtmlExporterOutput;
import net.sf.jasperreports.export.SimpleOutputStreamExporterOutput;
import net.sf.jasperreports.export.SimpleWriterExporterOutput;
import org.jfree.chart.encoders.SunPNGEncoderAdapter;

public class Reportes {

    private Connection conn;
    JasperPrint jasperPrint;
    HttpServletResponse httpServletResponse;

    public Reportes() {
        ConexionPool c = new ConexionPool();
        c.conectar();
        conn = c.getConexion();
    }

    public void generarReporte(Map parametros, String tipo, String dir, String nombre) throws JRException, IOException {
        ServletContext sc = (ServletContext) FacesContext.getCurrentInstance().getExternalContext().getContext();
        String reportePath = sc.getRealPath(dir);
// Map<String, Object> parametros = new HashMap<>();
//        Map parameterMap = new HashMap();
//        Set s = parametros.keySet();
//        for (Iterator it = s.iterator(); it.hasNext();) {
//            String key = it.next().toString();
//            parameterMap.put(key, parametros.get(key));
//        }
        parametros.put(JRParameter.REPORT_LOCALE, new Locale("es", "ES"));
        jasperPrint = JasperFillManager.fillReport(reportePath,
                parametros, conn);
        if (tipo.equals("pdf")) {
            httpServletResponse = (HttpServletResponse) FacesContext.getCurrentInstance().getExternalContext().getResponse();
            httpServletResponse.setContentType("application/pdf");
            httpServletResponse.setHeader("Content-Disposition",
                    "inline;filename=" + nombre + "." + tipo);
            ServletOutputStream servletOutputStream
                    = httpServletResponse.getOutputStream();
            JasperExportManager.exportReportToPdfStream(jasperPrint,
                    servletOutputStream);
            FacesContext.getCurrentInstance().responseComplete();
        } else if (tipo.equals("docx")) {
            httpServletResponse = (HttpServletResponse) FacesContext.getCurrentInstance().getExternalContext().getResponse();
            httpServletResponse.setContentType("application/doc");
            httpServletResponse.setHeader("Content-Disposition",
                    "inline;filename=" + nombre + "." + tipo);
            ServletOutputStream servletOutputStream
                    = httpServletResponse.getOutputStream();
            JRDocxExporter docxExporter = new JRDocxExporter();
            docxExporter.setExporterInput(new SimpleExporterInput(jasperPrint));
            docxExporter.setExporterOutput(new SimpleOutputStreamExporterOutput(servletOutputStream));
            docxExporter.exportReport();
            FacesContext.getCurrentInstance().responseComplete();
        } else if (tipo.equals("xlsx")) {
            httpServletResponse = (HttpServletResponse) FacesContext.getCurrentInstance().getExternalContext().getResponse();
            httpServletResponse.setContentType("application/xls");
            httpServletResponse.setHeader("Content-Disposition",
                    "inline;filename=" + nombre + "." + tipo);
            ServletOutputStream servletOutputStream
                    = httpServletResponse.getOutputStream();
            JRXlsxExporter docxExporter = new JRXlsxExporter();
            docxExporter.setExporterInput(new SimpleExporterInput(jasperPrint));
            docxExporter.setExporterOutput(new SimpleOutputStreamExporterOutput(servletOutputStream));
            docxExporter.exportReport();
            FacesContext.getCurrentInstance().responseComplete();
        } else if (tipo.equals("csv")) {
            httpServletResponse = (HttpServletResponse) FacesContext.getCurrentInstance().getExternalContext().getResponse();
            httpServletResponse.setContentType("application/csv");
            httpServletResponse.setHeader("Content-Disposition",
                    "inline;filename=" + nombre + "." + tipo);
            ServletOutputStream servletOutputStream
                    = httpServletResponse.getOutputStream();
            JRCsvExporter docxExporter = new JRCsvExporter();
            docxExporter.setExporterInput(new SimpleExporterInput(jasperPrint));
            docxExporter.setExporterOutput(new SimpleWriterExporterOutput(servletOutputStream));
            docxExporter.exportReport();
            FacesContext.getCurrentInstance().responseComplete();
        } else if (tipo.equals("html")) {
            httpServletResponse = (HttpServletResponse) FacesContext.getCurrentInstance().getExternalContext().getResponse();
            httpServletResponse.setContentType("application/html");
            httpServletResponse.setHeader("Content-Disposition",
                    "inline;filename=" + nombre + "." + tipo);
            ServletOutputStream servletOutputStream
                    = httpServletResponse.getOutputStream();
            HtmlExporter exporter = new HtmlExporter();
            exporter.setExporterInput(new SimpleExporterInput(jasperPrint));
            exporter.setExporterOutput(new SimpleHtmlExporterOutput(servletOutputStream));
            exporter.exportReport();
            FacesContext.getCurrentInstance().responseComplete();
        } else {
            httpServletResponse = (HttpServletResponse) FacesContext.getCurrentInstance().getExternalContext().getResponse();
            httpServletResponse.setContentType("Image/png");
            httpServletResponse.setHeader("Content-Disposition",
                    "inline;filename=" + nombre + "." + tipo);
            byte[] imageData = null;
            int pages = jasperPrint.getPages().size();
            for (int i = 0; i < pages; i++) {
                BufferedImage image = (BufferedImage) JasperPrintManager.printPageToImage(jasperPrint, i, 2.0f
                );
                imageData = new SunPNGEncoderAdapter().encode(image);
                if (imageData != null) {
                    httpServletResponse.setContentLength(imageData.length);
                    try (ServletOutputStream servletOutputStream
                            = httpServletResponse.getOutputStream()) {
                        servletOutputStream.write(imageData, 0, imageData.length);
                        servletOutputStream.flush();
                    }
                }
            }
        }
    }
}
