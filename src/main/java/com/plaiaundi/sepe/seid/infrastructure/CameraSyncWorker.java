package com.plaiaundi.sepe.seid.infrastructure;

import com.plaiaundi.sepe.seid.dominio.dao.CameraRepository;
import com.plaiaundi.sepe.seid.dominio.model.Camera;
import com.plaiaundi.sepe.seid.dominio.services.CameraService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@Component
@Slf4j // Si usas Lombok para logs, si no usa System.out
public class CameraSyncWorker {

    @Autowired private CameraService cameraService;
    @Autowired private CameraRepository cameraRepository;

    // Ejecutar cada hora (3600000 ms), espera inicial 5s
    @Scheduled(fixedRate = 3600000, initialDelay = 5000)
    public void sincronizarCamaras() {
        log.info("--- 🔄 INICIO WORKER: Sincronización de Cámaras ---");
        long inicio = System.currentTimeMillis();

        try {

            List<Camera> camarasProcesadas = cameraService.syncAllCamerasFromAPI();
            cameraRepository.saveAll(camarasProcesadas);

            log.info("✅ Sincronización finalizada. Cámaras procesadas: {}", camarasProcesadas.size());

        } catch (Exception e) {
            log.error("❌ Error crítico en el worker de cámaras", e);
        }

        long fin = System.currentTimeMillis();
        log.info("--- ⏱️ Tiempo total ejecución: {} ms ---", (fin - inicio));
    }
}