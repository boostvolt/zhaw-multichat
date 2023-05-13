package ch.zhaw.pm2.multichat.client.controller;

import static ch.zhaw.pm2.multichat.client.message.MessageCategory.LOG;
import static ch.zhaw.pm2.multichat.client.message.MessageCategory.MESSAGE;
import static ch.zhaw.pm2.multichat.client.message.MessageFactory.createErrorMessage;
import static ch.zhaw.pm2.multichat.protocol.Identifiers.ANONYMOUS;
import static java.lang.String.format;

import ch.zhaw.pm2.multichat.client.message.MessageCategory;
import ch.zhaw.pm2.multichat.client.message.MessageFilter;
import ch.zhaw.pm2.multichat.client.model.ChatWindowModel;
import ch.zhaw.pm2.multichat.client.utils.Formatter;
import ch.zhaw.pm2.multichat.client.utils.MessageParser;
import ch.zhaw.pm2.multichat.client.utils.MessageParser.MessageParserResult;
import ch.zhaw.pm2.multichat.client.view.MessageListView;
import ch.zhaw.pm2.multichat.protocol.NetworkHandler;
import ch.zhaw.pm2.multichat.protocol.connection.ConnectionStateType;
import ch.zhaw.pm2.multichat.protocol.exception.ChatProtocolException;
import java.net.URL;
import java.util.HashSet;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.regex.Pattern;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.CheckMenuItem;
import javafx.scene.control.MenuButton;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import lombok.extern.slf4j.Slf4j;

/**
 * A class representing the controller for the chat application.
 */
@Slf4j
public class ChatWindowController implements Initializable {

    private static final Pattern PORT_PATTERN = Pattern.compile("\\d{0,5}");
    private static final Pattern ADRESS_PATTERN = Pattern.compile("(\\d{0,3}(\\.\\d{0,3}){0,3})");
    private static final Pattern HOSTNAME_PATTERN = Pattern.compile(
        "[A-Za-z\\-]+(\\.[A-Za-z\\-]+)*");
    private static final Pattern USERNAME_PATTERN = Pattern.compile("\\S{0,50}");

    private BooleanBinding isServerAddressInvalidBinding;
    private ChatWindowModel model;

    @FXML
    private MenuButton filterOptionMenu;

    @FXML
    private CheckMenuItem filterMessages;

    @FXML
    private CheckMenuItem filterLogs;

    @FXML
    private BorderPane rootPane;

    @FXML
    private TextField serverAddressField;

    @FXML
    private TextField serverPortField;

    @FXML
    private TextField userNameField;

    @FXML
    private TextField messageField;

    @FXML
    private MessageListView messageArea;

    @FXML
    private Button connectButton;

    @FXML
    private Button sendButton;

    @FXML
    private Button clearButton;

    @FXML
    private TextField filterValue;

    /**
     * Initializes the chat window by setting up the UI components, disabling and enabling various
     * elements based on their respective bindings, and setting up listeners for input changes.
     *
     * @param location  the URL of the fxml file that defines the layout of the chat window
     * @param resources the ResourceBundle that contains the locale-specific resources for the chat
     *                  window
     */
    @FXML
    public void initialize(URL location, ResourceBundle resources) {
        model = new ChatWindowModel();

        isServerAddressInvalidBinding = Bindings.or(model.isWaitingForResponseBinding(),
            Bindings.or(serverAddressField.textProperty().isEmpty(),
                serverPortField.textProperty().isEmpty()));

        // Initialize MessageArea
        messageArea = new MessageListView(model.getFilteredMessages());
        messageArea.setId("messageArea");
        messageArea.setFocusTraversable(false);
        messageArea.setPadding(new Insets(0, 5, 0, 5));
        rootPane.setCenter(messageArea);

        // Disable elements based on binding state
        serverAddressField.disableProperty().bind(model.isConnectionEstablishedBinding());
        serverPortField.disableProperty().bind(model.isConnectionEstablishedBinding());
        userNameField.disableProperty().bind(model.isConnectionEstablishedBinding());
        connectButton.setDisable(true);
        connectButton.disableProperty().bind(isServerAddressInvalidBinding);
        sendButton.disableProperty().bind(model.isConnectionEstablishedBinding().not()
            .or(Bindings.createBooleanBinding(
                () -> MessageParser.parse(messageField.getText()).message().isBlank(),
                messageField.textProperty())));
        messageField.disableProperty().bind(model.isConnectionEstablishedBinding().not());
        clearButton.disableProperty().bind(model.isMessageListEmptyBinding());
        filterValue.disableProperty().bind(model.isMessageListEmptyBinding());
        filterOptionMenu.disableProperty().bind(model.isMessageListEmptyBinding());

        // Set default input field values
        serverAddressField.setText(NetworkHandler.DEFAULT_ADDRESS.getCanonicalHostName());
        serverPortField.setText(String.valueOf(NetworkHandler.DEFAULT_PORT));

        // Set input field formatter
        userNameField.setTextFormatter(Formatter.format(USERNAME_PATTERN));
        serverAddressField.setTextFormatter(Formatter.format(HOSTNAME_PATTERN, ADRESS_PATTERN));
        serverPortField.setTextFormatter(Formatter.format(PORT_PATTERN));

        // Bind username to property
        userNameField.textProperty().bindBidirectional(model.getUsernameProperty());

        // Set onChange listeners
        filterValue.textProperty().addListener(observable -> onFilterInputChange());
        filterLogs.selectedProperty().addListener(observable -> onFilterInputChange());
        filterMessages.selectedProperty().addListener(observable -> onFilterInputChange());
        model.getStateProperty()
            .addListener((observable, oldValue, newValue) -> onConnectButtonTextChange(newValue));
    }

    /**
     * Initializes the window's stage by setting an event listener to handle the stage's close
     * request and disconnecting the model from the network when the stage is closed.
     *
     * @param stage The JavaFX stage to initialize with the event listener.
     */
    public void initializeWithStage(Stage stage) {
        stage.setOnCloseRequest(e -> model.disconnect());
    }

    @FXML
    private void onConnectButtonClick() {
        if (model.isConnectionEstablished()) {
            disconnect();
            messageField.clear();
        } else {
            if (userNameField.getText() == null || userNameField.getText().isBlank()) {
                userNameField.setText(ANONYMOUS);
            }

            connect();
        }
    }

    @FXML
    private void onSendButtonClick() {
        try {
            MessageParserResult result = MessageParser.parse(messageField.getText().strip());
            if (!result.message().isBlank()) {
                model.send(result.receiver(), result.message());
                messageField.clear();
                messageField.requestFocus();
            }
        } catch (ChatProtocolException e) {
            model.addMessage(createErrorMessage(e.getMessage()));
        }
    }

    @FXML
    private void onClearButtonClick() {
        model.clearMessages();
        messageField.requestFocus();
    }

    private void onFilterInputChange() {
        Set<MessageCategory> categories = new HashSet<>();
        if (filterLogs.isSelected()) {
            categories.add(LOG);
        }
        if (filterMessages.isSelected()) {
            categories.add(MESSAGE);
        }

        model.setFilter(new MessageFilter(categories, filterValue.getText()));
    }

    private void onConnectButtonTextChange(ConnectionStateType value) {
        connectButton.setText(switch (value) {
            case NEW, CONFIRM_CONNECT -> "Connecting...";
            case CONNECTED -> "Disconnect";
            case CONFIRM_DISCONNECT -> "Disconnecting...";
            case DISCONNECTED -> "Connect";
        });
    }

    private void connect() {
        if (isServerAddressInvalidBinding.get()) {
            return;
        }

        try {
            model.connect(serverAddressField.getText(),
                Integer.parseInt(serverPortField.getText()));
            messageField.requestFocus();
        } catch (NumberFormatException e) {
            model.addMessage(createErrorMessage(format("Invalid port number: %s", e.getMessage())));
        }
    }

    private void disconnect() {
        model.disconnect();
    }

}

