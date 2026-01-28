package com.plaiaundi.sepe.seid.dto.errors;

public record OpenDataValidationErrorModel (
    int code,
    String exception,
    String exceptionMessagen,
    String message,
    OpenDataValidationParamErrorModel params
){}
