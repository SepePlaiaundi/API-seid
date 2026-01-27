package com.plaiaundi.sepe.seid.infrastructure;

import com.plaiaundi.sepe.seid.dominio.model.Incidence;
import com.plaiaundi.sepe.seid.dominio.services.IncidenceService;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.client.ResourceAccessException;

import java.util.List;

@Component
@Slf4j
public class IncidenceSyncWorker {

    @Autowired private IncidenceService incidenceService;

    // Evento del worker
    // Ejecutar cada 10 min (600000 ms), espera inicial 25s
    @Scheduled(
        fixedRate = 600000, 
        initialDelay = 5000
    )
    public void sincronizarCamaras(
    ) {
        // Inicio de evento
        log.info("--- 🔄 INICIO WORKER: Sincronización de Incidencias ---");
        long inicio = System.currentTimeMillis();

        try 
        {
            // Funcion principal de sincronizacion
            List<Incidence> incidenciasProcesadas = incidenceService.syncAllIncidencesFromAPI();
            log.info("✅ Sincronización finalizada. Incidencias procesadas: {}", incidenciasProcesadas.size());
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
            log.error("❌ Error crítico en el worker de incidencias", e);
        }

        // Fin de evento
        long fin = System.currentTimeMillis();
        log.info("--- ⏱️ Tiempo total ejecución: {} ms ---", (fin - inicio));
    }
}