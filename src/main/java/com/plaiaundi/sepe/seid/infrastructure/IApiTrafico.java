package com.plaiaundi.sepe.seid.infrastructure;

import java.util.List;

import com.plaiaundi.sepe.seid.dto.*;

public interface IApiTrafico {
    
    // Camaras
    OpenDataCameraResponse listaCamaras(); // NumPagina = 1
    OpenDataCameraResponse listaCamaras(int numPagina);
    OpenDataCameraResponse listaCamarasPorLocalizacion(double latitud, double longitud, int radioEnKm); // NumPagina = 1
    OpenDataCameraResponse listaCamarasPorLocalizacion(double latitud, double longitud, int radioEnKm, int numPagina);
    OpenDataCameraResponse listaCamarasPorRecurso(int idRecurso); // NumPagina = 1
    OpenDataCameraResponse listaCamarasPorRecurso(int idRecurso, int numPagina);
    OpenDataCameraResponse listaCamarasPorRecursoYPorLocalizacion(int idRecurso, double latitud, double longitud, int radioEnKm); // NumPagina = 1
    OpenDataCameraResponse listaCamarasPorRecursoYPorLocalizacion(int idRecurso, double latitud, double longitud, int radioEnKm, int numPagina);
    OpenDataCamera buscarCamaraPorIdYRecurso(int id, int idRecurso);
    
    // Incidencias
    OpenDataIncidenceResponse listaIncidencias(); // NumPagina = 1
    OpenDataIncidenceResponse listaIncidencias(int numPagina);
    OpenDataIncidenceResponse listaIncidenciasPorFecha(int ano, int mes, int dia); // NumPagina = 1
    OpenDataIncidenceResponse listaIncidenciasPorFecha(int ano, int mes, int dia, int numPagina);
    OpenDataIncidenceResponse listaIncidenciasPorFechaYPorLocalizacion(int ano, int mes, int dia, double latitud, double longitud, int radioEnKm); // NumPagina = 1
    OpenDataIncidenceResponse listaIncidenciasPorFechaYPorLocalizacion(int ano, int mes, int dia, double latitud, double longitud, int radioEnKm, int numPagina);
    OpenDataIncidenceResponse listaIncidenciasPorMes(int ano, int mes); // NumPagina = 1
    OpenDataIncidenceResponse listaIncidenciasPorMes(int ano, int mes, int numPagina);
    OpenDataIncidenceResponse listaIncidenciasPorMesYPorLocalizacion(int ano, int mes, double latitud, double longitud, int radioEnKm); // NumPagina = 1
    OpenDataIncidenceResponse listaIncidenciasPorMesYPorLocalizacion(int ano, int mes, double latitud, double longitud, int radioEnKm, int numPagina);
    OpenDataIncidenceResponse listaIncidenciasPorRecurso(int idRecurso); // NumPagina = 1
    OpenDataIncidenceResponse listaIncidenciasPorRecurso(int idRecurso, int numPagina);
    OpenDataIncidenceResponse listaIncidenciasPorRecursoYPorLocalizacion(int idRecurso, double latitud, double longitud, int radioEnKm); // NumPagina = 1
    OpenDataIncidenceResponse listaIncidenciasPorRecursoYPorLocalizacion(int idRecurso, double latitud, double longitud, int radioEnKm, int numPagina);
    OpenDataIncidenceResponse listaIncidenciasPorAno(int ano); // NumPagina = 1
    OpenDataIncidenceResponse listaIncidenciasPorAno(int ano, int numPagina);
    OpenDataIncidenceResponse listaIncidenciasPorAnoYPorLocalizacion(int ano, double latitud, double longitud, int radioEnKm); // NumPagina = 1
    OpenDataIncidenceResponse listaIncidenciasPorAnoYPorLocalizacion(int ano, double latitud, double longitud, int radioEnKm, int numPagina);
    OpenDataIncidence buscarIncidenciaPorIdYRecurso(int id, int idRecurso);

    // Recursos
    List<OpenDataSource> listaRecursos(); // NumPagina = 1
    List<OpenDataSource> listaRecursos(int numPagina);

}
