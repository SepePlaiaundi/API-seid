package com.plaiaundi.sepe.seid.infrastructure;

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

    @Autowired private ApiTrafico apiTrafico; // Tu cliente que baja el JSON gigante
    @Autowired
    private CameraService cameraService;

    // Se ejecuta cada 10 minutos
    // initialDelay = 5000 espera 5 segs al arrancar la app antes de la primera carga
    @Scheduled(fixedRate = 600000, initialDelay = 5000)
    public void sincronizarCamaras() {
        log.info("--- INICIO SINCRONIZACIÓN AUTOMÁTICA ---");
        long inicio = System.currentTimeMillis();

        // 1. Obtenemos la lista "sucia" de la API externa
        var listaCruda = apiTrafico.getAllCameras();

        // 2. Disparamos los hilos (El servicio ya guarda en BD internamente)
        List<CompletableFuture<Camera>> futuros = listaCruda.stream()
                .map(dto -> cameraService.parseFromOpenDataCamera(dto))
                .toList();


        // 3. Esperamos a que el worker termine todo el trabajo
        // Aunque es background, usamos join() para que el método 'sincronizarCamaras'
        // no termine hasta que todas las cámaras estén procesadas.
        CompletableFuture.allOf(futuros.toArray(new CompletableFuture[0])).join();

        long fin = System.currentTimeMillis();
        log.info("--- FIN SINCRONIZACIÓN. Tiempo: {} ms ---", (fin - inicio));
    }
}