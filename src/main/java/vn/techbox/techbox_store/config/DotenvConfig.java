package vn.techbox.techbox_store.config;

import io.github.cdimascio.dotenv.Dotenv;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.env.EnvironmentPostProcessor;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MapPropertySource;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class DotenvConfig implements EnvironmentPostProcessor {

    // @Override
    // public void postProcessEnvironment(ConfigurableEnvironment environment, SpringApplication application) {
    //     try {
    //         Dotenv dotenv = Dotenv.configure().ignoreIfMissing().load();
    //         Map<String, Object> dotenvProperties = new HashMap<>();

    //         dotenv.entries().forEach(entry -> {
    //             dotenvProperties.put(entry.getKey(), entry.getValue());
    //         });

    //         environment.getPropertySources().addFirst(new MapPropertySource("dotenv", dotenvProperties));
    //     } catch (Exception e) {
    //         System.err.println("Could not load .env file: " + e.getMessage());
    //     }
    // }

    @Override
    public void postProcessEnvironment(ConfigurableEnvironment environment, SpringApplication application) {
        try {
            // Lấy profile active
            String[] activeProfiles = environment.getActiveProfiles();
            
            // Mặc định load .env
            String envFile = ".env";
            
            // Nếu profile test active, chuyển sang .env.test
            for (String profile : activeProfiles) {
                System.out.println("Active profile: " + profile);

                if ("test".equalsIgnoreCase(profile)) {

                    System.out.println("Loading .env.test for 'test' profile.");

                    envFile = ".env.test";
                }
            }

            // Load file dotenv tương ứng
            Dotenv dotenv = Dotenv.configure()
                                   .ignoreIfMissing()
                                   .filename(envFile)
                                   .load();

            Map<String, Object> dotenvProperties = new HashMap<>();
            dotenv.entries().forEach(entry -> dotenvProperties.put(entry.getKey(), entry.getValue()));

            // Add vào Spring Environment
            environment.getPropertySources().addFirst(new MapPropertySource("dotenv", dotenvProperties));

        } catch (Exception e) {
            System.err.println("Could not load .env file: " + e.getMessage());
        }
    }
}
