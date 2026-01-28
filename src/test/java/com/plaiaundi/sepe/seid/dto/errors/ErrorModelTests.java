package com.plaiaundi.sepe.seid.dto.errors;

import com.plaiaundi.sepe.seid.dominio.model.OpenDataException;
import org.junit.jupiter.api.Test;
import java.util.Collections;
import static org.junit.jupiter.api.Assertions.*;

class ErrorModelTests {

    @Test
    void testOpenDataErrorModel() {
        OpenDataErrorModel error = new OpenDataErrorModel(500, "ERR", "ExceptionMsg", "Message");
        assertEquals(500, error.code());
        assertEquals("Message", error.message());
    }

    @Test
    void testOpenDataValidationErrorModel() {
        OpenDataValidationErrorModel error = new OpenDataValidationErrorModel(
                400, "Ex", "ExMsg", "Msg", new OpenDataValidationParamErrorModel("M", "P"));
        assertEquals(400, error.code());
        assertNotNull(error.params());
    }

    @Test
    void testOpenDataException() {
        OpenDataErrorModel model = new OpenDataErrorModel(500, "C", "EM", "M");
        OpenDataException ex = new OpenDataException("Msg", model);
        assertEquals("Msg", ex.getMessage());
        assertEquals(model, ex.getErrorModel());

        OpenDataValidationErrorModel vmodel = new OpenDataValidationErrorModel(400, "E", "EM", "M", null);
        OpenDataException ex2 = new OpenDataException("Msg", vmodel);
        assertEquals(vmodel, ex2.getErrorValidationModel());
    }
}
