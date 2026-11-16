package com.todobarato.pcl.todobarato;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

import java.time.ZoneId;
import java.util.TimeZone;

@SpringBootApplication
@EnableJpaAuditing
public class TodobaratoApplication {

	public static void main(String[] args) {
		// La auditoría y JDBC utilizan el mismo horario local del negocio.
		TimeZone.setDefault(TimeZone.getTimeZone(ZoneId.of("America/Lima")));
		SpringApplication.run(TodobaratoApplication.class, args);
	}

}
