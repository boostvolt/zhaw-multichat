package ch.zhaw.pm2.multichat.client.model;

import static javafx.beans.binding.Bindings.createIntegerBinding;
import static javafx.collections.FXCollections.observableArrayList;

import ch.zhaw.pm2.multichat.client.message.Message;
import javafx.beans.binding.IntegerBinding;
import javafx.collections.ObservableList;
import javafx.collections.ObservableListBase;

/**
 * A model class for a list of chat messages. This class extends {@link ObservableListBase},
 * providing the necessary functionality to create an observable list. The class also exposes an
 * {@link IntegerBinding} that is bound to the size of the list, allowing clients to track the
 * number of messages in the list without the need to listen for changes to the list directly.
 */
public class MessageListModel extends ObservableListBase<Message> {

    private final ObservableList<Message> messages;
    private final IntegerBinding sizeBinding = createIntegerBinding(this::size, this);

    /**
     * Creates a new instance of {@code MessageListModel} with an empty list of messages. The list
     * is an observable list, so it can be observed for changes.
     */
    public MessageListModel() {
        messages = observableArrayList();
        messages.addListener(this::fireChange);
    }

    /**
     * Adds the given message at the specified index in the list.
     *
     * @param index   the index at which to insert the message
     * @param message the message to add to the list
     */
    @Override
    public synchronized void add(int index, Message message) {
        messages.add(index, message);
    }

    /**
     * Returns the message at the specified index in the list.
     *
     * @param index the index of the message to retrieve
     * @return the message at the specified index
     */
    @Override
    public synchronized Message get(int index) {
        return messages.get(index);
    }

    /**
     * Removes all messages from the list.
     */
    @Override
    public synchronized void clear() {
        messages.clear();
    }

    /**
     * Returns the number of messages in the list.
     *
     * @return the number of messages in the list
     */
    @Override
    public synchronized int size() {
        return messages.size();
    }

    /**
     * Returns {@code true} if the given object is equal to this object, and {@code false}
     * otherwise. Two {@code MessageListModel} instances are considered equal if their underlying
     * message lists are equal.
     *
     * @param o the object to compare to this object
     * @return {@code true} if the objects are equal, {@code false} otherwise
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        MessageListModel that = (MessageListModel) o;
        return messages.equals(that.messages);
    }

    /**
     * Returns the hash code of this object.
     *
     * @return the hash code of this object
     */
    @Override
    public int hashCode() {
        return messages.hashCode();
    }

    /**
     * Returns the {@link IntegerBinding} that is bound to the size of the list.
     *
     * @return the {@code IntegerBinding} that is bound to the size of the list
     */
    public synchronized IntegerBinding getSizeBinding() {
        return sizeBinding;
    }

}
