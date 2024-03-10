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

/**
 * Clase encargada de generar los reportes con JasperReports.
 */
public class GenerarReporte {
    // Variables de entrada para ambos tipos de reporte.
    public static String comprobante = "/generarFacturas/tpv.jrxml";
    public static String factura = "/generarFacturas/factura.jrxml";

    /**
     * Método que genera el comprobante de una mesa en concreto cuando se la cobra.
     * Los reportes son guardados en la carpeta "comprobantes" con formato pdf.
     * Estos también son visualizados instantáneamente en la pantalla tras su generación.
     * @param map Datos que serán pasados por parámetro al reporte.
     * @param con Conexión con la base de datos.
     * @param idFactura Id de la factura para leer los datos de la base de datos.
     */
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

    /**
     * Método que genera el la factura diaria de un día concreto.
     * Los reportes son guardados en la carpeta "facturasDiarias" con formato pdf.
     * Estos también son visualizados instantáneamente en la pantalla tras su generación.
     * @param map Datos que serán pasados por parámetro al reporte.
     * @param con Conexión con la base de datos.
     * @param fecha Fecha de la que se va a recoger los datos de las facturas.
     */
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
