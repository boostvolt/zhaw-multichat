package ch.zhaw.pm2.multichat.client.model;

import static ch.zhaw.pm2.multichat.client.message.MessageFactory.createErrorMessage;
import static ch.zhaw.pm2.multichat.client.message.MessageFilter.ALL_FILTER_OPTIONS;
import static ch.zhaw.pm2.multichat.protocol.NetworkHandler.openConnection;
import static ch.zhaw.pm2.multichat.protocol.connection.ConnectionStateType.CONFIRM_CONNECT;
import static ch.zhaw.pm2.multichat.protocol.connection.ConnectionStateType.CONFIRM_DISCONNECT;
import static ch.zhaw.pm2.multichat.protocol.connection.ConnectionStateType.CONNECTED;
import static ch.zhaw.pm2.multichat.protocol.connection.ConnectionStateType.DISCONNECTED;
import static ch.zhaw.pm2.multichat.protocol.connection.ConnectionStateType.NEW;
import static ch.zhaw.pm2.multichat.protocol.payload.PayloadFactory.createConnectPayload;
import static ch.zhaw.pm2.multichat.protocol.payload.PayloadFactory.createDisconnectPayload;
import static ch.zhaw.pm2.multichat.protocol.payload.PayloadFactory.createMessagePayload;
import static ch.zhaw.pm2.multichat.protocol.payload.PayloadType.CONFIRM;
import static ch.zhaw.pm2.multichat.protocol.payload.PayloadType.CONNECT;
import static ch.zhaw.pm2.multichat.protocol.payload.PayloadType.DISCONNECT;
import static ch.zhaw.pm2.multichat.protocol.payload.PayloadType.ERROR;
import static ch.zhaw.pm2.multichat.protocol.payload.PayloadType.MESSAGE;
import static java.lang.String.format;
import static javafx.collections.FXCollections.unmodifiableObservableList;

import ch.zhaw.pm2.multichat.client.connection.ClientConnectionListener;
import ch.zhaw.pm2.multichat.client.message.Message;
import ch.zhaw.pm2.multichat.client.message.MessageFilter;
import ch.zhaw.pm2.multichat.client.payload.ConfirmPayloadHandler;
import ch.zhaw.pm2.multichat.client.payload.ConnectPayloadHandler;
import ch.zhaw.pm2.multichat.client.payload.DisconnectPayloadHandler;
import ch.zhaw.pm2.multichat.client.payload.ErrorPayloadHandler;
import ch.zhaw.pm2.multichat.client.payload.MessagePayloadHandler;
import ch.zhaw.pm2.multichat.protocol.connection.Connection;
import ch.zhaw.pm2.multichat.protocol.connection.ConnectionStateType;
import ch.zhaw.pm2.multichat.protocol.exception.ChatProtocolException;
import ch.zhaw.pm2.multichat.protocol.payload.Payload;
import ch.zhaw.pm2.multichat.protocol.payload.PayloadHandler;
import ch.zhaw.pm2.multichat.protocol.payload.PayloadType;
import java.io.IOException;
import java.util.EnumMap;
import java.util.Map;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.ObservableList;
import lombok.extern.slf4j.Slf4j;

/**
 * A class representing the model for the chat window controller. It manages the connection and
 * messaging functionalities with the chat server. It provides the functionality to establish,
 * disconnect, and manage the state of the connection, retrieve and filter the messages, add a
 * message, and clear the message list. It also provides the binding and properties for the
 * username, connection state, message list, and waiting for the response status.
 */
@Slf4j
public class ChatWindowModel {

    private final MessageListModel messages = new MessageListModel();
    private final StringProperty usernameProperty = new SimpleStringProperty();
    private final Map<PayloadType, PayloadHandler<Payload>> payloadHandlers = new EnumMap<>(
        PayloadType.class);
    private final ObjectProperty<ConnectionStateType> stateProperty = new SimpleObjectProperty<>(
        DISCONNECTED);
    private final BooleanBinding isWaitingForResponseBinding = Bindings.or(
        stateProperty.isEqualTo(CONFIRM_CONNECT), stateProperty.isEqualTo(CONFIRM_DISCONNECT));
    private final BooleanBinding isConnectionEstablishedBinding = stateProperty.isEqualTo(
        CONNECTED);
    private final BooleanBinding isMessageListEmptyBinding = messages.getSizeBinding().isEqualTo(
        0);
    private final FilteredMessageListModel filteredMessages;
    private Connection<Payload> connection;

    /**
     * Constructs a new ChatWindowModel object. Initializes the filteredMessages attribute as a new
     * instance of FilteredMessageListModel, passing in the messages attribute.
     */
    public ChatWindowModel() {
        filteredMessages = new FilteredMessageListModel(messages);
        initializePayloadHandlers();
    }

    /**
     * Returns a StringProperty containing the username.
     *
     * @return usernameProperty.
     */
    public StringProperty getUsernameProperty() {
        return usernameProperty;
    }

    /**
     * Returns the username.
     *
     * @return username of connection.
     */
    public String getUsername() {
        return usernameProperty.get();
    }

    /**
     * Method to set the username.
     *
     * @param username username for client connection.
     */
    public void setUsername(String username) {
        usernameProperty.set(username);
    }

    /**
     * Method to clear the username.
     */
    public void clearUsername() {
        usernameProperty.set("");
    }

    /**
     * Returns stateProperty.
     *
     * @return ObjectProperty<ConnectionStateType>
     */
    public ObjectProperty<ConnectionStateType> getStateProperty() {
        return stateProperty;
    }

    /**
     * Method to set the state of a connection.
     *
     * @param state ConnectionStateType of the connection
     */
    public void setState(ConnectionStateType state) {
        stateProperty.set(state);

        if (connection != null) {
            connection.setState(state);
        }
    }

    /**
     * Checks if the current state of the connection is equal to the specified state.
     *
     * @param state the ConnectionStateType to compare with the current state
     * @return true if the current state is equal to the specified state, false otherwise
     */
    public boolean isState(ConnectionStateType state) {
        return stateProperty.get() == state;
    }

    /**
     * Returns a BooleanBinding that represents whether the chat window model is currently waiting
     * for a response from the server.
     *
     * @return A BooleanBinding that represents whether the chat window model is currently waiting
     * for a response from the server.
     */
    public BooleanBinding isWaitingForResponseBinding() {
        return isWaitingForResponseBinding;
    }

    /**
     * Returns a BooleanBinding that represents whether the connection to the server has been
     * established or not.
     *
     * @return A BooleanBinding that is true if the connection to the server has been established,
     * false otherwise.
     */
    public BooleanBinding isConnectionEstablishedBinding() {
        return isConnectionEstablishedBinding;
    }

    /**
     * Returns whether the connection with the server has been established.
     *
     * @return {@code true} if the connection has been established, {@code false} otherwise.
     */
    public boolean isConnectionEstablished() {
        return isConnectionEstablishedBinding.get();
    }

    /**
     * Returns a BooleanBinding representing whether the message list is empty or not.
     *
     * @return a BooleanBinding representing whether the message list is empty or not.
     */
    public BooleanBinding isMessageListEmptyBinding() {
        return isMessageListEmptyBinding;
    }

    /**
     * Sets the message filter to the specified filter and updates the filtered messages list
     * accordingly.
     *
     * @param filter the message filter to set.
     */
    public void setFilter(MessageFilter filter) {
        filteredMessages.setFilter(filter);
    }

    /**
     * Returns an ObservableList<Message> with the filtered messages.
     *
     * @return ObservableList<Message> containing filtered messages.
     */
    public ObservableList<Message> getFilteredMessages() {
        return unmodifiableObservableList(filteredMessages);
    }

    /**
     * Adds a new message to the list.
     *
     * @param message message that is added.
     */
    public void addMessage(Message message) {
        messages.add(message);
    }

    /**
     * Deletes all messages.
     */
    public void clearMessages() {
        messages.clear();
    }

    /**
     * Connects to a chat server at the specified host and port. If the connection is already
     * established, an error message is added to the chat window and the method returns. Otherwise,
     * the connection is established and a new thread is started to listen for incoming payloads. A
     * "connect" payload is sent to the server with the username of the client, and the client state
     * is set to CONFIRM_CONNECT. If an I/O exception or chat protocol exception occurs, an error
     * message is added to the chat window and the disconnect() method is called to clean up.
     *
     * @param host the host to connect to
     * @param port the port to connect to
     */
    public void connect(String host, int port) {
        clearMessages();
        
        if (!isState(DISCONNECTED)) {
            addMessage(createErrorMessage("Connection is already established"));
            return;
        }

        setState(NEW);
        setFilter(ALL_FILTER_OPTIONS);

        try {
            connection = new Connection<>(openConnection(host, port));
            new Thread(new ClientConnectionListener(connection, payloadHandlers, this)).start();

            if (!isState(NEW)) {
                throw new ChatProtocolException(
                    format("Illegal state for connection: %s", connection.getState()));
            }

            connection.sendPayload(createConnectPayload(getUsername()));
            setState(CONFIRM_CONNECT);
        } catch (IOException | ChatProtocolException e) {
            addMessage(createErrorMessage(e.getMessage()));
            disconnect();
        }
    }

    /**
     * Disconnects from the chat server. If the connection is not established, this method does
     * nothing. Otherwise, a "disconnect" payload is sent to the server with the username of the
     * client. If the network connection is not available, the client state is set to DISCONNECTED
     * and the username is cleared. Otherwise, the messages are cleared and the client state is set
     * to CONFIRM_DISCONNECT. Finally, the username is cleared.
     */
    public void disconnect() {
        if (connection != null && !connection.isState(DISCONNECTED)) {
            connection.sendPayload(createDisconnectPayload(getUsername()));

            if (!connection.getNetworkConnection().isAvailable()) {
                setState(DISCONNECTED);
                clearUsername();

                return;
            }

            clearMessages();
            setState(CONFIRM_DISCONNECT);
        } else {
            setState(DISCONNECTED);
        }
        clearUsername();
    }

    /**
     * Sends a message to the specified receiver through the established connection. If the
     * connection is not established or the message is empty, it will add an error message to the
     * chat.
     *
     * @param receiver the username of the message recipient
     * @param content  the content of the message
     */
    public void send(String receiver, String content) {
        if (connection == null || connection.getState() != CONNECTED) {
            addMessage(createErrorMessage("Connection is not established"));
            return;
        }

        if (content == null || content.isBlank()) {
            addMessage(createErrorMessage("Empty messages are not allowed to be sent"));
            return;
        }

        connection.sendPayload(createMessagePayload(getUsername(), receiver, content));
    }

    /**
     * Initializes the payload handlers for each payload type. This method is called when the
     * ChatWindowModel is created.
     */
    private void initializePayloadHandlers() {
        payloadHandlers.put(CONNECT, new ConnectPayloadHandler());
        payloadHandlers.put(CONFIRM, new ConfirmPayloadHandler(this));
        payloadHandlers.put(DISCONNECT, new DisconnectPayloadHandler(this));
        payloadHandlers.put(MESSAGE, new MessagePayloadHandler(this));
        payloadHandlers.put(ERROR, new ErrorPayloadHandler(this));
    }

}
