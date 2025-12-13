package net.laurus.starfield;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import lombok.Getter;
import net.laurus.starfield.bus.EventBus;
import net.laurus.starfield.controller.MainFxController;
import net.laurus.starfield.controller.SpringController;
import net.laurus.starfield.events.StarfieldInputEvent;
import net.laurus.starfield.service.InputService;

@Getter
public class MainApp extends Application {

    public static MainApp INSTANCE;

    private ConfigurableApplicationContext springContext;

    private EventBus eventBus;

    private InputService inputService;

    private SpringController controllerSpring;

    private MainFxController controllerFx;

    @Override
    public void init() {
        springContext = SpringBootLauncher
                .startSpring(getParameters().getRaw().toArray(String[]::new));

        eventBus = springContext.getBean(EventBus.class);
        inputService = springContext.getBean(InputService.class);
        controllerSpring = springContext.getBean(SpringController.class);

        INSTANCE = this;
    }

    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/main.fxml"));

        // **Do not ask Spring for the FX controller**
        loader.setControllerFactory(param -> new MainFxController());

        BorderPane root = loader.load();

        // After FXML is loaded, inject controller into Spring wrapper
        controllerFx = loader.getController();
        SpringController wrapper = springContext.getBean(SpringController.class);
        wrapper.setFxController(controllerFx);

        Scene scene = new Scene(root, 800, 600);
        stage.setTitle("Starfield");
        scene.setOnKeyPressed(e -> eventBus.publish(StarfieldInputEvent.keyPressed(e)));
        scene.setOnKeyReleased(e -> eventBus.publish(StarfieldInputEvent.keyReleased(e)));
        stage.setScene(scene);
        controllerFx.setScene(scene);
        stage.show();

    }

    @Override
    public void stop() {
        springContext.close();
    }

    public static void main(String[] args) {
        launch(args);
    }

    @SpringBootApplication(scanBasePackages = "net.laurus.starfield")
    private static class SpringBootLauncher {

        public static ConfigurableApplicationContext startSpring(String[] args) {
            return SpringApplication.run(SpringBootLauncher.class, args);
        }

    }

}
