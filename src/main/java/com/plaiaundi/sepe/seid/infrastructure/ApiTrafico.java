package com.plaiaundi.sepe.seid.infrastructure;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.plaiaundi.sepe.seid.dominio.model.OpenDataException;
import com.plaiaundi.sepe.seid.dto.*;
import com.plaiaundi.sepe.seid.dto.errors.OpenDataErrorModel;
import com.plaiaundi.sepe.seid.dto.errors.OpenDataValidationErrorModel;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpRequest;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.io.IOException;
import java.util.List;

@Service
public class ApiTrafico implements IApiTrafico {

        private final RestClient restClient;
        private final ObjectMapper objectMapper = new ObjectMapper();

        public ApiTrafico(@Qualifier("ClienteApiTrafico") RestClient restClient) {
                this.restClient = restClient;
        }

        @Override
        public OpenDataCameraResponse listaCamaras() {
                return listaCamaras(1);
        }

        @Override
        public OpenDataCameraResponse listaCamaras(int numPagina) {
                return restClient.get()
                                .uri(uriBuilder -> uriBuilder
                                                .path("/v1.0/cameras")
                                                .queryParam("_page", numPagina)
                                                .build())
                                .accept(MediaType.APPLICATION_JSON)
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
                                                .build(latitud, longitud, radioEnKm))
                                .accept(MediaType.APPLICATION_JSON)
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
                                                .build(idRecurso))
                                .accept(MediaType.APPLICATION_JSON)
                                .retrieve()
                                .onStatus(HttpStatusCode::is4xxClientError, this::manejarError4xx)
                                .onStatus(HttpStatusCode::is5xxServerError, this::manejarError5xx)
                                .body(OpenDataCameraResponse.class);
        }

        @Override
        public OpenDataCameraResponse listaCamarasPorRecursoYPorLocalizacion(int idRecurso, double latitud,
                        double longitud,
                        int radioEnKm) {
                return listaCamarasPorRecursoYPorLocalizacion(idRecurso, latitud, longitud, radioEnKm, 1);
        }

        @Override
        public OpenDataCameraResponse listaCamarasPorRecursoYPorLocalizacion(int idRecurso, double latitud,
                        double longitud,
                        int radioEnKm, int numPagina) {
                return restClient.get()
                                .uri(uriBuilder -> uriBuilder
                                                .path("/v1.0/cameras/bySource/{idRecurso}/byLocation/{lat}/{lon}/{rad}")
                                                .queryParam("_page", numPagina)
                                                .build(idRecurso, latitud, longitud, radioEnKm))
                                .accept(MediaType.APPLICATION_JSON)
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
                                                .build(id, idRecurso))
                                .accept(MediaType.APPLICATION_JSON)
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
                                                .build())
                                .accept(MediaType.APPLICATION_JSON)
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
                                                .build(ano, mes, dia))
                                .accept(MediaType.APPLICATION_JSON)
                                .retrieve()
                                .onStatus(HttpStatusCode::is4xxClientError, this::manejarError4xx)
                                .onStatus(HttpStatusCode::is5xxServerError, this::manejarError5xx)
                                .body(OpenDataIncidenceResponse.class);
        }

        @Override
        public OpenDataIncidenceResponse listaIncidenciasPorFechaYPorLocalizacion(int ano, int mes, int dia,
                        double latitud,
                        double longitud, int radioEnKm) {
                return listaIncidenciasPorFechaYPorLocalizacion(ano, mes, dia, latitud, longitud, radioEnKm, 1);
        }

        @Override
        public OpenDataIncidenceResponse listaIncidenciasPorFechaYPorLocalizacion(int ano, int mes, int dia,
                        double latitud,
                        double longitud, int radioEnKm, int numPagina) {
                return restClient.get()
                                .uri(uriBuilder -> uriBuilder
                                                .path("/v1.0/incidences/byDate/{year}/{month}/{day}/byLocation/{lat}/{lon}/{km}")
                                                .queryParam("_page", numPagina)
                                                .build(ano, mes, dia, latitud, longitud, radioEnKm))
                                .accept(MediaType.APPLICATION_JSON)
                                .retrieve()
                                .onStatus(HttpStatusCode::is4xxClientError, this::manejarError4xx)
                                .onStatus(HttpStatusCode::is5xxServerError, this::manejarError5xx)
                                .body(OpenDataIncidenceResponse.class);
        }

        @Override
        public OpenDataIncidenceResponse listaIncidenciasPorMes(int ano, int mes) {
                return listaIncidenciasPorMes(ano, mes, 1);
        }

        @Override
        public OpenDataIncidenceResponse listaIncidenciasPorMes(int ano, int mes, int numPagina) {
                return restClient.get()
                                .uri(uriBuilder -> uriBuilder
                                                .path("/v1.0/incidences/byMonth/{year}/{month}")
                                                .queryParam("_page", numPagina)
                                                .build(ano, mes))
                                .accept(MediaType.APPLICATION_JSON)
                                .retrieve()
                                .onStatus(HttpStatusCode::is4xxClientError, this::manejarError4xx)
                                .onStatus(HttpStatusCode::is5xxServerError, this::manejarError5xx)
                                .body(OpenDataIncidenceResponse.class);
        }

        @Override
        public OpenDataIncidenceResponse listaIncidenciasPorMesYPorLocalizacion(int ano, int mes, double latitud,
                        double longitud, int radioEnKm) {
                return listaIncidenciasPorMesYPorLocalizacion(ano, mes, latitud, longitud, radioEnKm, 1);
        }

        @Override
        public OpenDataIncidenceResponse listaIncidenciasPorMesYPorLocalizacion(int ano, int mes, double latitud,
                        double longitud, int radioEnKm, int numPagina) {
                return restClient.get()
                                .uri(uriBuilder -> uriBuilder
                                                .path("/v1.0/incidences/byMonth/{year}/{month}/byLocation/{lat}/{lon}/{km}")
                                                .queryParam("_page", numPagina)
                                                .build(ano, mes, latitud, longitud, radioEnKm))
                                .accept(MediaType.APPLICATION_JSON)
                                .retrieve()
                                .onStatus(HttpStatusCode::is4xxClientError, this::manejarError4xx)
                                .onStatus(HttpStatusCode::is5xxServerError, this::manejarError5xx)
                                .body(OpenDataIncidenceResponse.class);
        }

        @Override
        public OpenDataIncidenceResponse listaIncidenciasPorRecurso(int idRecurso) {
                return listaIncidenciasPorRecurso(idRecurso, 1);
        }

        @Override
        public OpenDataIncidenceResponse listaIncidenciasPorRecurso(int idRecurso, int numPagina) {
                return restClient.get()
                                .uri(uriBuilder -> uriBuilder
                                                .path("/v1.0/incidences/bySource/{idSource}")
                                                .queryParam("_page", numPagina)
                                                .build(idRecurso))
                                .accept(MediaType.APPLICATION_JSON)
                                .retrieve()
                                .onStatus(HttpStatusCode::is4xxClientError, this::manejarError4xx)
                                .onStatus(HttpStatusCode::is5xxServerError, this::manejarError5xx)
                                .body(OpenDataIncidenceResponse.class);
        }

        @Override
        public OpenDataIncidenceResponse listaIncidenciasPorRecursoYPorLocalizacion(int idRecurso, double latitud,
                        double longitud, int radioEnKm) {
                return listaIncidenciasPorRecursoYPorLocalizacion(idRecurso, latitud, longitud, radioEnKm, 1);
        }

        @Override
        public OpenDataIncidenceResponse listaIncidenciasPorRecursoYPorLocalizacion(int idRecurso, double latitud,
                        double longitud, int radioEnKm, int numPagina) {
                return restClient.get()
                                .uri(uriBuilder -> uriBuilder
                                                .path("/v1.0/incidences/bySource/{idSource}/byLocation/{lat}/{lon}/{km}")
                                                .queryParam("_page", numPagina)
                                                .build(idRecurso, latitud, longitud, radioEnKm))
                                .accept(MediaType.APPLICATION_JSON)
                                .retrieve()
                                .onStatus(HttpStatusCode::is4xxClientError, this::manejarError4xx)
                                .onStatus(HttpStatusCode::is5xxServerError, this::manejarError5xx)
                                .body(OpenDataIncidenceResponse.class);
        }

        @Override
        public OpenDataIncidenceResponse listaIncidenciasPorAno(int ano) {
                return listaIncidenciasPorAno(ano, 1);
        }

        @Override
        public OpenDataIncidenceResponse listaIncidenciasPorAno(int ano, int numPagina) {
                return restClient.get()
                                .uri(uriBuilder -> uriBuilder
                                                .path("/v1.0/incidences/bYear/{year}")
                                                .queryParam("_page", numPagina)
                                                .build(ano))
                                .accept(MediaType.APPLICATION_JSON)
                                .retrieve()
                                .onStatus(HttpStatusCode::is4xxClientError, this::manejarError4xx)
                                .onStatus(HttpStatusCode::is5xxServerError, this::manejarError5xx)
                                .body(OpenDataIncidenceResponse.class);
        }

        @Override
        public OpenDataIncidenceResponse listaIncidenciasPorAnoYPorLocalizacion(int ano, double latitud,
                        double longitud,
                        int radioEnKm) {
                return listaIncidenciasPorAnoYPorLocalizacion(ano, latitud, longitud, radioEnKm, 1);
        }

        @Override
        public OpenDataIncidenceResponse listaIncidenciasPorAnoYPorLocalizacion(int ano, double latitud,
                        double longitud,
                        int radioEnKm, int numPagina) {
                return restClient.get()
                                .uri(uriBuilder -> uriBuilder
                                                .path("/v1.0/incidences/byYear/{year}/byLocation/{lat}/{lon}/{km}")
                                                .queryParam("_page", numPagina)
                                                .build(ano, latitud, longitud, radioEnKm))
                                .accept(MediaType.APPLICATION_JSON)
                                .retrieve()
                                .onStatus(HttpStatusCode::is4xxClientError, this::manejarError4xx)
                                .onStatus(HttpStatusCode::is5xxServerError, this::manejarError5xx)
                                .body(OpenDataIncidenceResponse.class);
        }

        @Override
        public OpenDataIncidence buscarIncidenciaPorIdYRecurso(int id, int idRecurso) {
                return restClient.get()
                                .uri(uriBuilder -> uriBuilder
                                                .path("/v1.0/incidences/{id}/{idSource}")
                                                .build(id, idRecurso))
                                .accept(MediaType.APPLICATION_JSON)
                                .retrieve()
                                .onStatus(HttpStatusCode::is4xxClientError, this::manejarError4xx)
                                .onStatus(HttpStatusCode::is5xxServerError, this::manejarError5xx)
                                .body(OpenDataIncidence.class);
        }

        @Override
        public List<OpenDataSource> listaRecursos() {
                return listaRecursos(1);
        }

        @Override
        public List<OpenDataSource> listaRecursos(int numPagina) {
                return restClient.get()
                                .uri(uriBuilder -> uriBuilder
                                                .path("/v1.0/sources")
                                                .queryParam("_page", numPagina)
                                                .build())
                                .accept(MediaType.APPLICATION_JSON)
                                .retrieve()
                                .onStatus(HttpStatusCode::is4xxClientError, this::manejarError4xx)
                                .onStatus(HttpStatusCode::is5xxServerError, this::manejarError5xx)
                                .body(new ParameterizedTypeReference<List<OpenDataSource>>() {
                                });
        }

        private void manejarError4xx(HttpRequest request, ClientHttpResponse response) throws IOException {
                var error = objectMapper
                                .readValue(
                                                response.getBody(),
                                                OpenDataValidationErrorModel.class);
                throw new OpenDataException("Error cliente", error);
        }

        private void manejarError5xx(HttpRequest request, ClientHttpResponse response) throws IOException {
                var error = objectMapper
                                .readValue(
                                        response.getBody(),
                                        OpenDataErrorModel.class);
                throw new OpenDataException("Error servidor", error);
        }

}
