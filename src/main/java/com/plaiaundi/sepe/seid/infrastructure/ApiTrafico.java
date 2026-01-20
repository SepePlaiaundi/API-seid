package com.plaiaundi.sepe.seid.infrastructure;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.plaiaundi.sepe.seid.dominio.model.OpenDataException;
import com.plaiaundi.sepe.seid.dto.OpenDataCamera;
import com.plaiaundi.sepe.seid.dto.OpenDataCameraResponse;
import com.plaiaundi.sepe.seid.dto.OpenDataIncidence;
import com.plaiaundi.sepe.seid.dto.OpenDataIncidenceResponse;
import com.plaiaundi.sepe.seid.dto.OpenDataSource;
import com.plaiaundi.sepe.seid.dto.errors.OpenDataErrorModel;
import com.plaiaundi.sepe.seid.dto.errors.OpenDataValidationErrorModel;

import org.springframework.http.HttpRequest;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


@Service
public class ApiTrafico implements IApiTrafico {

    private final RestClient restClient;
    private final ObjectMapper objectMapper;

    public ApiTrafico(RestClient restClient, ObjectMapper objectMapper) {
        this.restClient = restClient;
        this.objectMapper = objectMapper;
    }

    public List<OpenDataCamera> getAllCameras() {
        List<OpenDataCamera> result = new ArrayList<>();

        for(int i = totalPaginasCamaras() ; i > 0 ; i--) {
            result.addAll(llamarApiCameras(i).cameras());
        }

        return filtrarCamaras(result);
    }

    private List<OpenDataCamera> filtrarCamaras(List<OpenDataCamera> lista) {
        lista.removeIf(camara -> camara.urlImage() == null);
        lista.removeIf(camara -> camara.latitude() == null);
        lista.removeIf(camara -> camara.longitude() == null);
        return lista;
    }

    private int totalPaginasCamaras() {
        return llamarApiCameras(1).totalPages();
    }

    public OpenDataCameraResponse llamarApiCameras(int numPagina) {
        return restClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/v1.0/cameras")
                        .queryParam("_page", numPagina)
                        .build()
                )
                .retrieve()
                .body(OpenDataCameraResponse.class);
    }

    public OpenDataCamera llamarApiCamerasById(int id, int idSource) {
        return restClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/v1.0/cameras/"+id+"/"+idSource)
                        .build()
                )
                .retrieve()
                .body(OpenDataCamera.class);
    }

    @Override
    public OpenDataCameraResponse listaCamaras() { return listaCamaras(1); }
    @Override
    public OpenDataCameraResponse listaCamaras(int numPagina) {
        return restClient.get()
            .uri(uriBuilder -> uriBuilder
                .path("/v1.0/cameras")
                .queryParam("_page", numPagina)
                .build()
            )
            .retrieve()
            .onStatus(HttpStatusCode::is4xxClientError, this::manejarError4xx)
            .onStatus(HttpStatusCode::is5xxServerError, this::manejarError5xx)
            .body(OpenDataCameraResponse.class);
    }

    @Override
    public OpenDataCameraResponse listaCamarasPorLocalizacion(double latitud, double longitud, int radioEnKm) {
        return listaCamarasPorLocalizacion(latitud, longitud, radioEnKm, 1);
    }
    @Override
    public OpenDataCameraResponse listaCamarasPorLocalizacion(double latitud, double longitud, int radioEnKm,
            int numPagina) {
        return restClient.get()
            .uri(uriBuilder -> uriBuilder
                .path("/v1.0/cameras/byLocation/{lat}/{lon}/{rad}")
                .queryParam("_page", numPagina)
                .build(latitud, longitud, radioEnKm)
            )
            .retrieve()
            .onStatus(HttpStatusCode::is4xxClientError, this::manejarError4xx)
            .onStatus(HttpStatusCode::is5xxServerError, this::manejarError5xx)
            .body(OpenDataCameraResponse.class);
    }

    @Override
    public OpenDataCameraResponse listaCamarasPorRecurso(int idRecurso) {
        return listaCamarasPorRecurso(idRecurso, 1);
    }
    @Override
    public OpenDataCameraResponse listaCamarasPorRecurso(int idRecurso, int numPagina) {
        return restClient.get()
            .uri(uriBuilder -> uriBuilder
                .path("/v1.0/cameras/bySource/{idRecurso}")
                .queryParam("_page", numPagina)
                .build(idRecurso)
            )
            .retrieve()
            .onStatus(HttpStatusCode::is4xxClientError, this::manejarError4xx)
            .onStatus(HttpStatusCode::is5xxServerError, this::manejarError5xx)
            .body(OpenDataCameraResponse.class);
    }

    @Override
    public OpenDataCameraResponse listaCamarasPorRecursoYPorLocalizacion(int idRecurso, double latitud, double longitud,
            int radioEnKm) {
        return listaCamarasPorRecursoYPorLocalizacion(idRecurso, latitud, longitud, radioEnKm, 1);
    }
    @Override
    public OpenDataCameraResponse listaCamarasPorRecursoYPorLocalizacion(int idRecurso, double latitud, double longitud,
            int radioEnKm, int numPagina) {
        return restClient.get()
            .uri(uriBuilder -> uriBuilder
                .path("/v1.0/cameras/bySource/{idRecurso}/byLocation/{lat}/{lon}/{rad}")
                .queryParam("_page", numPagina)
                .build(idRecurso, latitud, longitud, radioEnKm)
            )
            .retrieve()
            .onStatus(HttpStatusCode::is4xxClientError, this::manejarError4xx)
            .onStatus(HttpStatusCode::is5xxServerError, this::manejarError5xx)
            .body(OpenDataCameraResponse.class);
    }

    @Override
    public OpenDataCamera buscarCamaraPorIdYRecurso(int id, int idRecurso) {
        return restClient.get()
            .uri(uriBuilder -> uriBuilder
                .path("/v1.0/cameras/{id}/{idRecurso}")
                .build(id, idRecurso)
            )
            .retrieve()
            .onStatus(HttpStatusCode::is4xxClientError, this::manejarError4xx)
            .onStatus(HttpStatusCode::is5xxServerError, this::manejarError5xx)
            .body(OpenDataCamera.class);
    }

    @Override
    public OpenDataIncidenceResponse listaIncidencias() {
        return listaIncidencias(1);
    }
    @Override
    public OpenDataIncidenceResponse listaIncidencias(int numPagina) {
        return restClient.get()
            .uri(uriBuilder -> uriBuilder
                .path("/v1.0/incidences")
                .queryParam("_page", numPagina)
                .build()
            )
            .retrieve()
            .onStatus(HttpStatusCode::is4xxClientError, this::manejarError4xx)
            .onStatus(HttpStatusCode::is5xxServerError, this::manejarError5xx)
            .body(OpenDataIncidenceResponse.class);
    }

    @Override
    public OpenDataIncidenceResponse listaIncidenciasPorFecha(int ano, int mes, int dia) {
        return listaIncidenciasPorFecha(ano, mes, dia, 1);
    }
    @Override
    public OpenDataIncidenceResponse listaIncidenciasPorFecha(int ano, int mes, int dia, int numPagina) {
        return restClient.get()
            .uri(uriBuilder -> uriBuilder
                .path("/v1.0/incidences/byDate/{year}/{month}/{day}")
                .queryParam("_page", numPagina)
                .build(ano, mes, dia)
            )
            .retrieve()
            .onStatus(HttpStatusCode::is4xxClientError, this::manejarError4xx)
            .onStatus(HttpStatusCode::is5xxServerError, this::manejarError5xx)
            .body(OpenDataIncidenceResponse.class);
    }

    // TODO: seguir por aqui

    @Override
    public OpenDataIncidenceResponse listaIncidenciasPorFechaYPorLocalizacion(int ano, int mes, int dia, double latitud,
            double longitud, int radioEnKm) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'listaIncidenciasPorFechaYPorLocalizacion'");
    }

    @Override
    public OpenDataIncidenceResponse listaIncidenciasPorFechaYPorLocalizacion(int ano, int mes, int dia, double latitud,
            double longitud, int radioEnKm, int numPagina) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'listaIncidenciasPorFechaYPorLocalizacion'");
    }

    @Override
    public OpenDataIncidenceResponse listaIncidenciasPorMes(int ano, int mes) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'listaIncidenciasPorMes'");
    }

    @Override
    public OpenDataIncidenceResponse listaIncidenciasPorMes(int ano, int mes, int numPagina) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'listaIncidenciasPorMes'");
    }

    @Override
    public OpenDataIncidenceResponse listaIncidenciasPorMesYPorLocalizacion(int ano, int mes, double latitud,
            double longitud, int radioEnKm) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'listaIncidenciasPorMesYPorLocalizacion'");
    }

    @Override
    public OpenDataIncidenceResponse listaIncidenciasPorMesYPorLocalizacion(int ano, int mes, double latitud,
            double longitud, int radioEnKm, int numPagina) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'listaIncidenciasPorMesYPorLocalizacion'");
    }

    @Override
    public OpenDataIncidenceResponse listaIncidenciasPorRecurso(int idRecurso) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'listaIncidenciasPorRecurso'");
    }

    @Override
    public OpenDataIncidenceResponse listaIncidenciasPorRecurso(int idRecurso, int numPagina) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'listaIncidenciasPorRecurso'");
    }

    @Override
    public OpenDataIncidenceResponse listaIncidenciasPorRecursoYPorLocalizacion(int idRecurso, double latitud,
            double longitud, int radioEnKm) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'listaIncidenciasPorRecursoYPorLocalizacion'");
    }

    @Override
    public OpenDataIncidenceResponse listaIncidenciasPorRecursoYPorLocalizacion(int idRecurso, double latitud,
            double longitud, int radioEnKm, int numPagina) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'listaIncidenciasPorRecursoYPorLocalizacion'");
    }

    @Override
    public OpenDataIncidenceResponse listaIncidenciasPorAno(int ano) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'listaIncidenciasPorAno'");
    }

    @Override
    public OpenDataIncidenceResponse listaIncidenciasPorAno(int ano, int numPagina) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'listaIncidenciasPorAno'");
    }

    @Override
    public OpenDataIncidenceResponse listaIncidenciasPorAnoYPorLocalizacion(int ano, double latitud, double longitud,
            int radioEnKm) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'listaIncidenciasPorAnoYPorLocalizacion'");
    }

    @Override
    public OpenDataIncidenceResponse listaIncidenciasPorAnoYPorLocalizacion(int ano, double latitud, double longitud,
            int radioEnKm, int numPagina) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'listaIncidenciasPorAnoYPorLocalizacion'");
    }

    @Override
    public OpenDataIncidence buscarIncidenciaPorIdYRecurso(int id, int idRecurso) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'buscarIncidenciaPorIdYRecurso'");
    }

    @Override
    public List<OpenDataSource> listaRecursos() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'listaRecursos'");
    }

    @Override
    public List<OpenDataSource> listaRecursos(int numPagina) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'listaRecursos'");
    }

    private void manejarError4xx(HttpRequest request, ClientHttpResponse response) throws IOException {
        var error = objectMapper.readValue(response.getBody(), OpenDataValidationErrorModel.class);
        throw new OpenDataException("Error cliente", error);
    }

    private void manejarError5xx(HttpRequest request, ClientHttpResponse response) throws IOException {
        var error = objectMapper.readValue(response.getBody(), OpenDataErrorModel.class);
        throw new OpenDataException("Error servidor", error);
    }


}
