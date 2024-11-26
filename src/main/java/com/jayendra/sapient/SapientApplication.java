package com.jayendra.sapient;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.CrossOrigin;

import javax.sql.DataSource;

@SpringBootApplication
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class SapientApplication {

	private  final DataSource dataSource;

	public SapientApplication(DataSource dataSource) {
		this.dataSource = dataSource;
	}

	public static void main(String[] args) {
		SpringApplication.run(SapientApplication.class, args);
	}

}
