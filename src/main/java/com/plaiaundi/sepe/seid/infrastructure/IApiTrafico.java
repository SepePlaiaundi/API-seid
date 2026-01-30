package com.plaiaundi.sepe.seid.infrastructure;

import java.util.List;

import com.plaiaundi.sepe.seid.dto.*;

public interface IApiTrafico {
    
    // Camaras
    OpenDataCameraResponse listaCamaras() throws Exception; // NumPagina = 1
    OpenDataCameraResponse listaCamaras(int numPagina) throws Exception;
    OpenDataCameraResponse listaCamarasPorLocalizacion(double latitud, double longitud, int radioEnKm) throws Exception; // NumPagina = 1
    OpenDataCameraResponse listaCamarasPorLocalizacion(double latitud, double longitud, int radioEnKm, int numPagina) throws Exception;
    OpenDataCameraResponse listaCamarasPorRecurso(int idRecurso) throws Exception; // NumPagina = 1
    OpenDataCameraResponse listaCamarasPorRecurso(int idRecurso, int numPagina) throws Exception;
    OpenDataCameraResponse listaCamarasPorRecursoYPorLocalizacion(int idRecurso, double latitud, double longitud, int radioEnKm) throws Exception; // NumPagina = 1
    OpenDataCameraResponse listaCamarasPorRecursoYPorLocalizacion(int idRecurso, double latitud, double longitud, int radioEnKm, int numPagina) throws Exception;
    OpenDataCamera buscarCamaraPorIdYRecurso(int id, int idRecurso) throws Exception;
    
    // Incidencias
    OpenDataIncidenceResponse listaIncidencias() throws Exception; // NumPagina = 1
    OpenDataIncidenceResponse listaIncidencias(int numPagina) throws Exception;
    OpenDataIncidenceResponse listaIncidenciasPorFecha(int ano, int mes, int dia) throws Exception; // NumPagina = 1
    OpenDataIncidenceResponse listaIncidenciasPorFecha(int ano, int mes, int dia, int numPagina) throws Exception;
    OpenDataIncidenceResponse listaIncidenciasPorFechaYPorLocalizacion(int ano, int mes, int dia, double latitud, double longitud, int radioEnKm) throws Exception; // NumPagina = 1
    OpenDataIncidenceResponse listaIncidenciasPorFechaYPorLocalizacion(int ano, int mes, int dia, double latitud, double longitud, int radioEnKm, int numPagina) throws Exception;
    OpenDataIncidenceResponse listaIncidenciasPorMes(int ano, int mes) throws Exception; // NumPagina = 1
    OpenDataIncidenceResponse listaIncidenciasPorMes(int ano, int mes, int numPagina) throws Exception;
    OpenDataIncidenceResponse listaIncidenciasPorMesYPorLocalizacion(int ano, int mes, double latitud, double longitud, int radioEnKm) throws Exception; // NumPagina = 1
    OpenDataIncidenceResponse listaIncidenciasPorMesYPorLocalizacion(int ano, int mes, double latitud, double longitud, int radioEnKm, int numPagina) throws Exception;
    OpenDataIncidenceResponse listaIncidenciasPorRecurso(int idRecurso) throws Exception; // NumPagina = 1
    OpenDataIncidenceResponse listaIncidenciasPorRecurso(int idRecurso, int numPagina) throws Exception;
    OpenDataIncidenceResponse listaIncidenciasPorRecursoYPorLocalizacion(int idRecurso, double latitud, double longitud, int radioEnKm) throws Exception; // NumPagina = 1
    OpenDataIncidenceResponse listaIncidenciasPorRecursoYPorLocalizacion(int idRecurso, double latitud, double longitud, int radioEnKm, int numPagina) throws Exception;
    OpenDataIncidenceResponse listaIncidenciasPorAno(int ano) throws Exception; // NumPagina = 1
    OpenDataIncidenceResponse listaIncidenciasPorAno(int ano, int numPagina) throws Exception;
    OpenDataIncidenceResponse listaIncidenciasPorAnoYPorLocalizacion(int ano, double latitud, double longitud, int radioEnKm) throws Exception; // NumPagina = 1
    OpenDataIncidenceResponse listaIncidenciasPorAnoYPorLocalizacion(int ano, double latitud, double longitud, int radioEnKm, int numPagina) throws Exception;
    OpenDataIncidence buscarIncidenciaPorIdYRecurso(int id, int idRecurso) throws Exception;

    // Recursos
    List<OpenDataSource> listaRecursos(); // NumPagina = 1
    List<OpenDataSource> listaRecursos(int numPagina);

}
