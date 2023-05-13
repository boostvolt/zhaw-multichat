package ch.zhaw.pm2.multichat.client;

import javafx.application.Application;
import lombok.extern.slf4j.Slf4j;

/**
 * Entry point for the MultiChat client application. This class contains a `main` method that
 * launches the JavaFX-based client user interface (`ClientUI`). It serves as the starting point for
 * the client application.
 */
@Slf4j
public class Client {

    /**
     * Launches the MultiChat client application. This method starts the JavaFX application by
     * calling `Application.launch` and passing it the `ClientUI` class and command line arguments.
     *
     * @param args Command line arguments passed to the application.
     */
    public static void main(String[] args) {
        Application.launch(ClientUI.class, args);
    }

}
