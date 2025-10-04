package com.hyunwoosing.perturba;

import com.hyunwoosing.perturba.common.config.props.AuthProps;
import com.hyunwoosing.perturba.common.config.props.PkceProps;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties({AuthProps.class, PkceProps.class})
public class PerturbaApplication {

	public static void main(String[] args) {
		SpringApplication.run(PerturbaApplication.class, args);
	}

}
