package com.plaiaundi.sepe.seid.dominio.model;

import com.plaiaundi.sepe.seid.dto.errors.OpenDataErrorModel;
import com.plaiaundi.sepe.seid.dto.errors.OpenDataValidationErrorModel;

public class OpenDataException extends RuntimeException {

    private final OpenDataErrorModel errorModel;
    private final OpenDataValidationErrorModel errorValidationModel;

    public OpenDataException(String message, OpenDataErrorModel errorModel) {
        super(message);
        this.errorModel = errorModel;
        this.errorValidationModel = null;
    }

    public OpenDataException(String message, OpenDataValidationErrorModel errorValidationModel) {
        super(message);
        this.errorModel = null;
        this.errorValidationModel = errorValidationModel;
    }

    public OpenDataErrorModel getErrorModel() {
        return errorModel;
    }

    public OpenDataValidationErrorModel getErrorValidationModel() {
        return errorValidationModel;
    }
}