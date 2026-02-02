package com.plaiaundi.sepe.seid.infrastructure;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import javax.sql.DataSource;

@SpringBootTest
@org.springframework.test.context.TestPropertySource(properties = {
        "spring.datasource.url=jdbc:mysql://seid-gamr.i.aivencloud.com:20406/seid?sslMode=REQUIRED",
        "spring.datasource.username=avnadmin",
        "spring.datasource.password=AVNS_kys3c64-duW9zh4YMoP",
        "spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver",
        "spring.jpa.hibernate.ddl-auto=none"
})
public class SchemaFixTest {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Test
    public void fixEstadoColumn() {
        System.out.println("Attempting to alter table 'camaras' column 'estado'...");
        jdbcTemplate.execute("ALTER TABLE camaras MODIFY COLUMN estado VARCHAR(20)");
        System.out.println("Successfully altered table 'camaras' column 'estado' to VARCHAR(20).");
    }
}
