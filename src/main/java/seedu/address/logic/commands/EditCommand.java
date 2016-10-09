package seedu.address.logic.commands;

import seedu.address.commons.core.Messages;
import seedu.address.commons.core.UnmodifiableObservableList;
import seedu.address.commons.exceptions.IllegalValueException;
import seedu.address.model.item.Item;
import seedu.address.model.item.Name;
import seedu.address.model.item.ReadOnlyItem;
import seedu.address.model.item.UniquePersonList;
import seedu.address.model.item.UniquePersonList.PersonNotFoundException;

/**
 * Edits an item identified using it's last displayed index from the task manager.
 */
public class EditCommand extends Command {

    public static final String COMMAND_WORD = "edit";

    public static final String MESSAGE_USAGE = COMMAND_WORD
            + ": Edits the item identified by the index number used in the last item listing.\n"
            + "Parameters: INDEX (must be a positive integer)" + "n/NAME" + "\n"
            + "Example: " + COMMAND_WORD + " 1" + "buy milk";

    public static final String MESSAGE_EDIT_ITEM_SUCCESS = "Edited Item: %1$s";
    
    public final int targetIndex;
    public final Name newName;

    /*
     * Deletes deadline, task, or event by index.
     *      
     * @throws IllegalValueException if any of the raw values are invalid
     */
    public EditCommand(int targetIndex, String name)
            throws IllegalValueException {
        this.targetIndex = targetIndex;
        this.newName = new Name(name);
    }

    @Override
    public CommandResult execute() {

        UnmodifiableObservableList<ReadOnlyItem> lastShownList = model.getFilteredPersonList();

        if (lastShownList.size() < targetIndex) {
            indicateAttemptToExecuteIncorrectCommand();
            return new CommandResult(Messages.MESSAGE_INVALID_PERSON_DISPLAYED_INDEX);
        }

        ReadOnlyItem itemToDelete = lastShownList.get(targetIndex - 1);
        Item itemToAdd = new Item(itemToDelete);
        itemToAdd.setName(newName);
        try {
            model.deleteItem(itemToDelete);
        } catch (PersonNotFoundException pnfe) {
            assert false : "The target item cannot be missing";
        }
        
        try {
            model.addItem(itemToAdd);
            return new CommandResult(String.format(MESSAGE_EDIT_ITEM_SUCCESS, itemToAdd));
        } catch (UniquePersonList.DuplicatePersonException e) {
            return new CommandResult(MESSAGE_DUPLICATE_ITEM);
        }
    }

}
