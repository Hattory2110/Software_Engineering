package boardgame.UI;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class BoardGameApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("/gameUI.fxml"));
        stage.setTitle("Dao Game");
        stage.setScene(new Scene(root));
        stage.setResizable(false);
        stage.show();
    }
}
