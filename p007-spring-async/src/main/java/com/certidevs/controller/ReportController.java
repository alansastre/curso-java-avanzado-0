package com.certidevs.controller;

import com.certidevs.dto.ReportCreationRequest;
import com.certidevs.service.ReportService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@AllArgsConstructor
@RestController
@RequestMapping("/reports")
public class ReportController {


    private ReportService reportService;

    /**
     * Endpoint para generar el reporte consolidado.
     * Llama a un metodo asíncrono y devuelve una respuesta inmediatamente sin esperar a que acabe.
     * Ventaja:
     * No bloqueas el hilo de la request, para poder seguir atentiendo otras peticiones
     * Le pasas el trabajo pesado a un hilo que estará en el pool de hilos del Executor configuración AsyncConfig
     * No bloqueas el navegador, no hay peligro de cortes por ejemplo de que nginx corte la conexión si detecta que el backend tarda mucho en responder
     */
    @PostMapping("/consolidated")
    public ResponseEntity<String> createConsolidatedReport(@RequestBody ReportCreationRequest request) {
        reportService.generateConsolidatedReportForCompany(request); // Lanza proceso asíncrono
        // se envía por email cuando termine
        // alternativa: si hay una conexión websocket se podrá enviar al websocket un aviso de finalización
        // indicando la ruta de descarga, así sale por pantalla un toast con el aviso
        return ResponseEntity.ok("Petición recibida, reporte en proceso. Se enviará a " + request.email());
    }

    /**
     * Endpoint para descargar el reporte generado.
     */
    @GetMapping("/download")
    public ResponseEntity<?> downloadReport(@RequestParam String file) {
        // Lógica para descargar el archivo
        // Por simplicidad, solo devolvemos un mensaje
        return ResponseEntity.ok("Descargando archivo: " + file);
    }
}