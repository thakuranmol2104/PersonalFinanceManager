package personalfinance;

import javafx.application.Application;
import javafx.stage.Stage;
import personalfinance.storage.FileHandler;
import personalfinance.ui.MainFrame;

public class Main extends Application {

    @Override
    public void start(Stage stage) {
        FileHandler.init();
        new MainFrame().show(stage);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
