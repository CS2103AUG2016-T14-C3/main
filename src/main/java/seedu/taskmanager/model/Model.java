package seedu.taskmanager.model;

import seedu.taskmanager.commons.core.UnmodifiableObservableList;
import seedu.taskmanager.model.item.Item;
import seedu.taskmanager.model.item.ReadOnlyItem;
import seedu.taskmanager.model.item.UniqueItemList;

import java.util.Set;

/**
 * The API of the Model component.
 */
public interface Model {
    /** Clears existing backing model and replaces with the provided new data. */
    void resetData(ReadOnlyTaskManager newData);

    /** Returns the AddressBook */
    ReadOnlyTaskManager getAddressBook();

    /** Deletes the given item. */
    void deleteItem(ReadOnlyItem target) throws UniqueItemList.ItemNotFoundException;

    /** Adds the given item */
    void addItem(Item item) throws UniqueItemList.DuplicateItemException;

    /** Replaces the given item */
    void replaceItem(ReadOnlyItem target, Item toReplace) throws UniqueItemList.ItemNotFoundException, UniqueItemList.DuplicateItemException;
    
    /** Returns the filtered item list as an {@code UnmodifiableObservableList<ReadOnlyItem>} */
    UnmodifiableObservableList<ReadOnlyItem> getFilteredItemList();

    /** Updates the filter of the filtered person list to show all items */
    void updateFilteredListToShowAll();

    /** Updates the filter of the filtered item list to filter by the given keywords*/
    void updateFilteredPersonList(Set<String> keywords);

}
