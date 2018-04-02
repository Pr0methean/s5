package com.blogspot3.TimeZoneService;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Collection;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.view.RedirectView;

import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@SpringBootApplication
@RestController
@EnableAutoConfiguration
@Configuration
@EnableSwagger2
public class TimeZoneServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(TimeZoneServiceApplication.class, args);
	}
	
	@RequestMapping(value="/", method = { RequestMethod.POST, RequestMethod.GET })
	public RedirectView root() { return new RedirectView("/swagger-ui.html"); }
	
	@RequestMapping(value="/getTimeZoneOffset", method = { RequestMethod.POST, RequestMethod.GET })
	public String getTimeZoneOffset(@RequestParam(name="dateTime") String dateTime, @RequestParam(name="location") String location) {
		ZonedDateTime zdt = ZonedDateTime.of(LocalDateTime.parse(dateTime), ZoneId.of(location));
		return zdt.getOffset().toString();
	}
	
	@RequestMapping(value="/getTimeZones", method = { RequestMethod.POST, RequestMethod.GET })
	public Collection<String> getTimeZones() {
		return ZoneId.getAvailableZoneIds();
	}
	
	@Bean
    public Docket api() { 
        return new Docket(DocumentationType.SWAGGER_2)  
          .select().apis(RequestHandlerSelectors.any()).paths(PathSelectors.any()).build();                                           
    }
}
