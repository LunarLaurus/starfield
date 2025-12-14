package net.laurus.starfield;

import org.springframework.context.ConfigurableApplicationContext;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import lombok.Getter;
import net.laurus.starfield.app.SpringBootLauncher;
import net.laurus.starfield.app.bus.EventBus;
import net.laurus.starfield.app.events.SpringPostInitEvent;
import net.laurus.starfield.controller.MainFxController;
import net.laurus.starfield.controller.SpringController;
import net.laurus.starfield.service.InputService;
import net.laurus.starfield.service.repo.StarNameRepository;

@Getter
public class MainApp extends Application {

    public static MainApp INSTANCE;

    private ConfigurableApplicationContext springContext;

    private EventBus eventBus;

    private StarNameRepository starNames;

    private InputService inputService;

    private SpringController controllerSpring;

    private MainFxController controllerFx;

    @Override
    public void init() {
        INSTANCE = this;
        springContext = SpringBootLauncher
                .startSpring(getParameters().getRaw().toArray(String[]::new));

        eventBus = springContext.getBean(EventBus.class);
        inputService = springContext.getBean(InputService.class);
        controllerSpring = springContext.getBean(SpringController.class);
        starNames = springContext.getBean(StarNameRepository.class);

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
        stage.setScene(scene);
        controllerFx.setScene(scene);
        stage.show();

        eventBus.publish(new SpringPostInitEvent());

    }

    @Override
    public void stop() {
        springContext.close();
    }

    public static void main(String[] args) {
        launch(args);
    }

}
