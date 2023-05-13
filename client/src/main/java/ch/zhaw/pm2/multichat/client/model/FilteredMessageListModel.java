package ch.zhaw.pm2.multichat.client.model;

import static javafx.application.Platform.runLater;

import ch.zhaw.pm2.multichat.client.message.Message;
import ch.zhaw.pm2.multichat.client.message.MessageFilter;
import ch.zhaw.pm2.multichat.client.utils.ConsumerTimeout;
import javafx.beans.Observable;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.collections.ObservableListBase;
import javafx.collections.transformation.FilteredList;

/**
 * A class representing a filtered list of messages. It is a JavaFX ObservableList that contains
 * messages and applies a filter to them. This class is used to display messages that match a
 * certain criteria, such as a specific keyword or a certain sender.
 */
public class FilteredMessageListModel extends ObservableListBase<Message> {

    private final FilteredList<Message> messages;
    private final ObjectProperty<MessageFilter> filterProperty = new SimpleObjectProperty<>();
    private final ConsumerTimeout<MessageFilter> consumerTimeout = new ConsumerTimeout<>(
        this::refreshFilter, 200);

    /**
     * Constructs a new FilteredMessageListModel object.
     *
     * @param messages The list of messages that the filter will be applied to.
     */
    public FilteredMessageListModel(ObservableList<Message> messages) {
        this.messages = new FilteredList<>(messages);
        filterProperty.addListener(this::onFilterChange);
        ListChangeListener<Message> listener = this::fireChange;
        this.messages.addListener(listener);
    }

    /**
     * Sets the filter to be used to filter the messages.
     *
     * @param filter The MessageFilter to be used to filter the messages.
     */
    public synchronized void setFilter(MessageFilter filter) {
        filterProperty.set(filter);
    }

    /**
     * Returns the Message at the specified index in the list.
     *
     * @param index The index of the Message to be returned.
     * @return The Message at the specified index in the list.
     */
    @Override
    public synchronized Message get(int index) {
        return messages.get(index);
    }

    /**
     * Returns the number of messages in the filtered list.
     *
     * @return The number of messages in the filtered list.
     */
    @Override
    public synchronized int size() {
        return messages.size();
    }

    /**
     * Returns true if the specified object is equal to this FilteredMessageListModel object, false
     * otherwise.
     *
     * @param o The object to compare to this FilteredMessageListModel object.
     * @return True if the specified object is equal to this FilteredMessageListModel object, false
     * otherwise.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        FilteredMessageListModel that = (FilteredMessageListModel) o;
        return messages.equals(that.messages);
    }

    /**
     * Returns the hash code of this FilteredMessageListModel object.
     *
     * @return The hash code of this FilteredMessageListModel object.
     */
    @Override
    public int hashCode() {
        return messages.hashCode();
    }

    /**
     * Called when the filter changes. Applies the new filter after a certain timeout.
     *
     * @param observable The Observable that triggered the event.
     * @param oldFilter  The old filter.
     * @param newFilter  The new filter.
     */
    private void onFilterChange(Observable observable, MessageFilter oldFilter,
        MessageFilter newFilter) {
        consumerTimeout.accept(newFilter);
    }

    /**
     * Refreshes the current filter by setting the predicate of the underlying filtered list. The
     * predicate is set to a lambda expression that determines whether a message is applicable to
     * the specified filter or not. The operation is executed asynchronously on the JavaFX
     * application thread using {@link javafx.application.Platform#runLater(Runnable)}. This is to
     * ensure that the update to the list is done on the JavaFX thread, which is required for
     * thread-safety reasons.
     *
     * @param filter the new filter to apply to the list
     */
    private void refreshFilter(MessageFilter filter) {
        runLater(() -> messages.setPredicate(m -> m.isFilterApplicable(filter)));
    }

}
