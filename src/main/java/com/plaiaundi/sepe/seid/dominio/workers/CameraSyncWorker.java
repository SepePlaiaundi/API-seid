package com.plaiaundi.sepe.seid.dominio.workers;

import com.plaiaundi.sepe.seid.dominio.dao.CameraRepository;
import com.plaiaundi.sepe.seid.dominio.model.Camera;
import com.plaiaundi.sepe.seid.dominio.services.CameraService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.client.ResourceAccessException;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@Component
@Slf4j
public class CameraSyncWorker {

    @Autowired private CameraService cameraService;

    // Evento del worker
    // Ejecutar cada hora (3600000 ms), espera inicial 25s
    @Scheduled(
        fixedRate = 3600000, 
        initialDelay = 250000
    )
    public void sincronizarCamaras(
    ) {
        // Inicio de evento
        log.info("--- 🔄 INICIO WORKER: Sincronización de Cámaras ---");
        long inicio = System.currentTimeMillis();

        try 
        {
            // Funcion principal de sincronizacion
            List<Camera> camarasProcesadas = cameraService.syncAllCamerasFromAPI();
            log.info("✅ Sincronización finalizada. Cámaras procesadas: {}", camarasProcesadas.size());
        }

        // Error de conexión a la api
        catch (ResourceAccessException e) 
        {
            log.error("❌ Error de conexion a la api, reintentando en 1000ms");
            try {
                Thread.sleep(1000);
            } catch (InterruptedException ex) {
                throw new RuntimeException(ex);
            }
            log.info("✅ Reintentando conexion");
            sincronizarCamaras();

        // Cualquier otro error desconocido
        } catch (Exception e) {
            log.error("❌ Error crítico en el worker de cámaras", e);
        }

        // Fin de evento
        long fin = System.currentTimeMillis();
        log.info("--- ⏱️ Tiempo total ejecución: {} ms ---", (fin - inicio));
    }
}