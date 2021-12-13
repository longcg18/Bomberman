package uet.oop.bomberman.userInterface;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

import static uet.oop.bomberman.userInterface.Main.homeScene;

public class historyPlayController implements Initializable {

    @FXML
    private Button backButton;

    @FXML
    private TextArea space;

    private void addData(List<Player> players) {
        String out = "NAME\t\t\tPOINT";
        for (int i = 0; i < players.size(); i++) {
            out += "\n" + players.get(i).getName() + "\t\t\t" + players.get(i).getPoint();
        }
        space.setText(out);
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        backButton.setOnAction(actionEvent -> {
            Stage stage = (Stage) backButton.getScene().getWindow();
            stage.setTitle("HOME PAGE");
            stage.setScene(homeScene);
        });
    }
}
