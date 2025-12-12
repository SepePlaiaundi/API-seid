package com.plaiaundi.sepe.seid.infrastructure;

import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.MediaType;
import org.springframework.http.converter.AbstractHttpMessageConverter;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.converter.HttpMessageNotWritableException;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.config.annotation.ContentNegotiationConfigurer;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.List;

@Configuration
public class CsvConfig implements WebMvcConfigurer {

    // 1. Convertidor personalizado para CSV
    public class CsvHttpMessageConverter extends AbstractHttpMessageConverter<List<Object>> {

        public CsvHttpMessageConverter() {
            super(new MediaType("text", "csv"));
        }

        @Override
        protected boolean supports(Class<?> clazz) {
            return List.class.isAssignableFrom(clazz);
        }

        @Override
        protected List<Object> readInternal(Class<? extends List<Object>> clazz, HttpInputMessage inputMessage)
                throws IOException, HttpMessageNotReadableException {
            return null; // Solo implementamos escritura (descarga) para este ejemplo
        }

        @Override
        protected void writeInternal(List<Object> lista, HttpOutputMessage outputMessage)
                throws IOException, HttpMessageNotWritableException {
            if (lista.isEmpty()) return;

            // Jackson CSV Mapper
            CsvMapper mapper = new CsvMapper();
            
            // Crea el Schema basado en la clase del primer elemento de la lista
            Class<?> claseElemento = lista.get(0).getClass();
            CsvSchema schema = mapper.schemaFor(claseElemento).withHeader();

            // Escribe los datos en el output stream
            try (OutputStreamWriter writer = new OutputStreamWriter(outputMessage.getBody(), StandardCharsets.UTF_8)) {
                mapper.writer(schema).writeValues(writer).writeAll(lista);
            }
        }
    }

    // 2. Registramos el convertidor en Spring
    @Override
    public void extendMessageConverters(List<org.springframework.http.converter.HttpMessageConverter<?>> converters) {
        converters.add(new CsvHttpMessageConverter());
    }
    
    // (Opcional) 3. Estrategia para permitir ?format=csv en la URL
    @Override
    public void configureContentNegotiation(ContentNegotiationConfigurer configurer) {
        configurer.favorParameter(true)
                  .parameterName("format")
                  .defaultContentType(MediaType.APPLICATION_JSON)
                  .mediaType("csv", new MediaType("text", "csv"));
    }
}
