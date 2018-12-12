package com.github.kubaseai.TimeZoneService;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.TextStyle;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.view.RedirectView;

import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.service.VendorExtension;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@SpringBootApplication
@RestController
@EnableAutoConfiguration
@Configuration
@EnableSwagger2
public class TimeZoneServiceApplication {
	
	public final static class Timezone {
		public String id;
		public String fullName;
		public String offset;
		public String timeNow;
		
		public Timezone(ZonedDateTime zdt) {
			this.id = zdt.getZone().getId();
			this.fullName = zdt.getZone().getDisplayName(TextStyle.FULL, Locale.US);
			this.offset = zdt.getOffset().toString();
			if (this.offset.equals("Z"))
				this.offset = "+00:00";
			this.timeNow = zdt.toOffsetDateTime().toString();
		}
		public Timezone(String id) {
			this(ZonedDateTime.of(LocalDateTime.now(), ZoneId.of(id)));
		}
		@Override
		public String toString() {
			return timeNow;
		}
	}
	
	private final static DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");
	
	public static void main(String[] args) {
		SpringApplication.run(TimeZoneServiceApplication.class, args);
	}
	
	@RequestMapping(value="/", method = { RequestMethod.POST, RequestMethod.GET })
	public RedirectView index() { 
		return new RedirectView("/swagger-ui.html");
	}
	
	@RequestMapping(value="/health", method = { RequestMethod.GET })
	public Health health() {
		return Health.up().build();		
	}
	
	@RequestMapping(value="/getTimeZoneOffset", method = { RequestMethod.POST, RequestMethod.GET })
	public Timezone getTimeZoneOffset(@RequestParam(name="dateTime") String dateTime, @RequestParam(name="location") String location) {
		LocalDateTime dt = null;
		try {
			dt = LocalDateTime.parse(dateTime);			
		}
		catch (Exception e) {
			dt = OffsetDateTime.parse(dateTime).toLocalDateTime();
		}
		ZonedDateTime zdt = ZonedDateTime.of(dt,  ZoneId.of(location));
		return new Timezone(zdt);
	}
	
	@RequestMapping(value="/getTimeZones", method = { RequestMethod.POST, RequestMethod.GET })
	public List<Timezone> getTimeZones() {
		return ZoneId.getAvailableZoneIds().stream().map( id -> new Timezone(id))
			.sorted((o1, o2) -> o1.id.compareTo(o2.id))
			.collect(Collectors.toList());
	}
	
	@Bean
	public Docket api() { 
		@SuppressWarnings("rawtypes")
		ApiInfo api = new ApiInfo("TimeZoneService", "Simple operations helping with dateTime and timezone",
			"1.0", "https://www.apache.org/licenses/LICENSE-2.0",
			new Contact("Jakub Jozwicki","https://github.com/kubaseai/s5", "kubaseai@github.com"),
			"Apache 2.0 License", "https://www.apache.org/licenses/LICENSE-2.0",
			new LinkedList<VendorExtension>())
		{
				@Override
				public String getTitle() {
					return ("TimeZoneService at "+df.format(new Date())).replace(' ', '_'); 
				}
		};
		Docket doc = new Docket(DocumentationType.SWAGGER_2).select()
			.apis(RequestHandlerSelectors.basePackage("com.github.kubaseai.TimeZoneService"))
			.paths(PathSelectors.any()).build().apiInfo(api);
		return doc;
    }
}
