package com.plaiaundi.sepe.seid.dominio.util;


import org.springframework.stereotype.Component;

import com.plaiaundi.sepe.seid.dominio.model.Incidence;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class IncidenceValidator {

    public Incidence validar(Incidence incidencia) {
        return incidencia;
    }
}