package uet.oop.bomberman.userInterface;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;

import static uet.oop.bomberman.Sound.Sound.*;

public class Main extends Application {

    public static Scene homeScene = showScene("src/uet/oop/bomberman/userInterface/HomePage.fxml");
    public static Scene userLoginScene = showScene("src/uet/oop/bomberman/userInterface/userLogin.fxml");

    public static void main(String[] args) {
        Platform.setImplicitExit(false);
        Application.launch(Main.class);
    }

    public static Scene showScene(String url) {
        FXMLLoader fxmlLoader = null;
        try {
            fxmlLoader = new FXMLLoader(new File(url).toURI().toURL());
        } catch (Exception e) {
            e.printStackTrace();
        }
        Parent parent = null;
        try {
            parent = fxmlLoader.load();
        } catch (Exception e) {
            e.printStackTrace();
        }
        Scene scene = new Scene(parent);
        return scene;
    }

    @Override
    public void start(Stage stage) throws Exception {
        stage.setResizable(false);
        stage.setOnCloseRequest(e -> {
            Platform.exit();
            System.exit(0);
        });
        loadHomePage(stage);
    }

    public static void loadHomePage(Stage stage) throws IOException {
        playMusic(introMusic);
        stage.setResizable(false);
        stage.setTitle("BOMBERMAN UET");
        stage.setScene(homeScene);
        stage.show();
    }
}
