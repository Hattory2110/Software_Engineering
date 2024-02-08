package boardgame.UI;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import boardgame.result.JsonGameResultManager;
import boardgame.result.PlayerStatistics;
import boardgame.result.GameResult;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.io.IOException;
import java.nio.file.Path;

public class TableViewController {
    @FXML
    private TableView<PlayerStatistics> tableView;

    @FXML
    private TableColumn<GameResult, String> winnerName;

    @FXML
    private TableColumn<GameResult, Long> numberOfWins;

    @FXML
    private void initialize() throws IOException {
        winnerName.setCellValueFactory(new PropertyValueFactory<>("winnerName"));
        numberOfWins.setCellValueFactory(new PropertyValueFactory<>("numberOfWins"));
        ObservableList<PlayerStatistics> observableList = FXCollections.observableArrayList();
        observableList.addAll(new JsonGameResultManager(Path.of("results.json")).getBestPlayers(10));
        tableView.setItems(observableList);
    }

    @FXML
    private void switchScene(ActionEvent event) throws IOException {
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        Parent root = FXMLLoader.load(getClass().getResource("/gameUI.fxml"));
        stage.setScene(new Scene(root));
        stage.show();
    }

    @FXML
    private void handleExit(ActionEvent event) {
        System.out.println("Exiting...");
        Platform.exit();
    }
}
