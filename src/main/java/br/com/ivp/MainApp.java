package br.com.ivp;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class MainApp extends Application {
    public static void launchApp(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader loader = new FXMLLoader(MainApp.class.getResource("/br/com/ivp/main-view.fxml"));
        Scene scene = new Scene(loader.load(), 850, 520);
        stage.setTitle("IVP - Hosts");
        stage.setScene(scene);
        stage.show();
    }
}
