package ch.zhaw.pm2.multichat.client;

import static java.lang.String.format;

import ch.zhaw.pm2.multichat.client.controller.ChatWindowController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import lombok.extern.slf4j.Slf4j;

/**
 * The {@code ClientUI} class represents the main entry point for the Multichat Client application.
 * It extends the JavaFX {@link Application} class and provides a method for starting up the chat
 * window.
 */
@Slf4j
public class ClientUI extends Application {

    /**
     * {@inheritDoc}
     */
    @Override
    public void start(Stage primaryStage) {
        chatWindow(primaryStage);
    }

    /**
     * Initializes the chat window and shows it on the primary stage. This method loads the
     * ChatWindow.fxml file using {@link FXMLLoader}, sets up the controller, creates a new
     * {@link Scene} from the loaded pane, and finally shows the primary stage.
     *
     * @param primaryStage the primary stage for this application
     */
    private void chatWindow(Stage primaryStage) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("ChatWindow.fxml"));
            Pane rootPane = loader.load();

            ChatWindowController controller = loader.getController();
            controller.initializeWithStage(primaryStage);

            // fill in scene and stage setup
            Scene scene = new Scene(rootPane);

            // configure and show stage
            primaryStage.setScene(scene);
            primaryStage.setMinWidth(450);
            primaryStage.setMinHeight(250);
            primaryStage.setTitle("Multichat Client");
            primaryStage.show();
        } catch (Exception e) {
            log.error(format("Error starting up the UI: %s", e.getMessage()));
        }
    }
    
}
