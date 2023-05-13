package ch.zhaw.pm2.multichat.client.view;

import static java.util.Objects.requireNonNull;

import ch.zhaw.pm2.multichat.client.message.Message;
import java.util.List;
import java.util.stream.Collectors;
import javafx.collections.ListChangeListener.Change;
import javafx.collections.ObservableList;
import javafx.scene.control.TextArea;
import javafx.scene.layout.BorderPane;

/**
 * A class representing a MessageListView used for displaying a list of messages in the JavaFX chat
 * message application.
 */
public class MessageListView extends BorderPane {

    private final ObservableList<Message> messageList;
    private TextArea textArea;

    /**
     * Constructs a new instance of {@code MessageListView} with the specified
     * {@link ObservableList} of messages.
     *
     * @param messageList the {@code ObservableList} of messages to be displayed in the view
     * @throws NullPointerException if the {@code messageList} parameter is {@code null}
     */
    public MessageListView(ObservableList<Message> messageList) {
        this.messageList = requireNonNull(messageList);
        initializeView();
        initializeMessageList();
    }

    private void initializeView() {
        textArea = new TextArea();
        textArea.setEditable(false);
        textArea.setWrapText(true);
        setCenter(textArea);
        textArea.focusTraversableProperty().bind(focusTraversableProperty());
    }

    private void initializeMessageList() {
        messageList.addListener(this::refreshView);
        textArea.clear();
        refreshMessageList(messageList);
    }

    private void refreshView(Change<? extends Message> change) {
        boolean entireListRefreshed = false;
        while (change.next() && !entireListRefreshed) {
            if (change.wasUpdated() || change.wasRemoved() || change.wasReplaced()
                || change.wasPermutated()) {
                textArea.clear();
                entireListRefreshed = true;
            }

            refreshMessageList(change.getAddedSubList());
        }
    }

    private void refreshMessageList(List<? extends Message> list) {
        textArea.appendText(
            list.stream().map(Message::getMessage)
                .collect(Collectors.joining(System.lineSeparator())));

        if (!list.isEmpty()) {
            textArea.appendText(System.lineSeparator());
        }
    }

}
