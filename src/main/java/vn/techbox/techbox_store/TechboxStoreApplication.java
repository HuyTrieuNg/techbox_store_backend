package vn.techbox.techbox_store;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.retry.annotation.EnableRetry;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.context.annotation.Bean;
import org.springframework.web.reactive.function.client.WebClient;
import java.util.TimeZone;

@SpringBootApplication
@EnableScheduling
@EnableRetry
@EnableAsync
public class TechboxStoreApplication {

	public static void main(String[] args) {
		TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
		SpringApplication.run(TechboxStoreApplication.class, args);
	}

}
