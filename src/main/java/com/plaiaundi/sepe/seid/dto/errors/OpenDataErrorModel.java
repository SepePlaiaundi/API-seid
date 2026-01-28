package com.plaiaundi.sepe.seid.dto.errors;

public record OpenDataErrorModel (
    int code,
    String exception,
    String exceptionMessagen,
    String message
){}
