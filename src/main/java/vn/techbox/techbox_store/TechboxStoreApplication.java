package vn.techbox.techbox_store;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import java.util.TimeZone;
import io.github.cdimascio.dotenv.Dotenv;

@SpringBootApplication
public class TechboxStoreApplication {

	public static void main(String[] args) {
		TimeZone.setDefault(TimeZone.getTimeZone("Asia/Ho_Chi_Minh"));

		Dotenv dotenv = Dotenv.load();
		dotenv.entries().forEach(entry ->
				System.setProperty(entry.getKey(), entry.getValue())
		);

		SpringApplication.run(TechboxStoreApplication.class, args);
	}

}
