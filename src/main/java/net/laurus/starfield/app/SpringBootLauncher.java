package net.laurus.starfield.app;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootApplication(scanBasePackages = "net.laurus.starfield")
public class SpringBootLauncher {

    public static ConfigurableApplicationContext startSpring(String[] args) {
        return SpringApplication.run(SpringBootLauncher.class, args);
    }

}
