package org.example;

import java.io.File;
import java.io.InputStream;
import java.sql.Connection;
import java.util.Map;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.export.ooxml.JRDocxExporter;
import net.sf.jasperreports.export.SimpleDocxReportConfiguration;
import net.sf.jasperreports.export.SimpleExporterInput;
import net.sf.jasperreports.export.SimpleOutputStreamExporterOutput;
import net.sf.jasperreports.view.JasperViewer;

public class GenerarReporte {
    public static String comprobante = "/generarFacturas/tpv.jrxml";
    public static String factura = "/generarFacturas/factura.jrxml";

    public void generarComprobante(Map<String, Object> map, Connection con, int idFactura) {
        try {
            String salida = "src/main/resources/comprobantes/comprobante" + idFactura + ".pdf";
            InputStream reportFile = getClass().getResourceAsStream(comprobante);
            JasperReport jr = JasperCompileManager.compileReport(reportFile);

            JasperPrint jp = JasperFillManager.fillReport(jr, map, con);
            JRDocxExporter export = new JRDocxExporter();
            export.setExporterInput(new SimpleExporterInput(jp));
            export.setExporterOutput(new SimpleOutputStreamExporterOutput(new File(salida)));
            SimpleDocxReportConfiguration config = new SimpleDocxReportConfiguration();
            export.setConfiguration(config);
            export.exportReport();

            JasperViewer.viewReport(jp, false);

        } catch (JRException ex) {
            ex.printStackTrace();
        }
    }

    public void generarFacturaDiaria(Map<String, Object> map, Connection con, String fecha) {
        try {
            String salida = "src/main/resources/facturasDiarias/facturaDia" + fecha + ".pdf";
            InputStream reportFile = getClass().getResourceAsStream(factura);
            JasperReport jr = JasperCompileManager.compileReport(reportFile);

            JasperPrint jp = JasperFillManager.fillReport(jr, map, con);
            JRDocxExporter export = new JRDocxExporter();
            export.setExporterInput(new SimpleExporterInput(jp));
            export.setExporterOutput(new SimpleOutputStreamExporterOutput(new File(salida)));
            SimpleDocxReportConfiguration config = new SimpleDocxReportConfiguration();
            export.setConfiguration(config);
            export.exportReport();

            JasperViewer.viewReport(jp, false);
        } catch (JRException ex) {
            ex.printStackTrace();
        }
    }
}
