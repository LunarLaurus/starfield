package net.laurus.starfield.spring;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootApplication(scanBasePackages = "net.laurus.starfield")
public class SpringBootLauncher {

    public static ConfigurableApplicationContext CONTEXT;

    public static ConfigurableApplicationContext startSpring(String[] args) {
        return CONTEXT = SpringApplication.run(SpringBootLauncher.class, args);
    }

}
