package seedu.taskmanager.logic.commands;

import java.util.ArrayList;
import java.util.ListIterator;
import java.util.logging.Logger;
import java.util.Collections;

import java.lang.StringBuilder;

import seedu.taskmanager.commons.core.LogsCenter;
import seedu.taskmanager.commons.core.Messages;
import seedu.taskmanager.commons.core.UnmodifiableObservableList;
import seedu.taskmanager.model.item.ReadOnlyItem;
import seedu.taskmanager.model.item.UniqueItemList.ItemNotFoundException;

/**
 * Deletes an item identified using it's last displayed index from the address book.
 */
public class DeleteCommand extends Command {

    public static final String COMMAND_WORD = "delete";
    
    //@@author A0140060A
    public static final String SHORT_COMMAND_WORD = "del";
    
    //@@author A0143641M
    public static final String MESSAGE_USAGE = COMMAND_WORD
            + ": Deletes the item(s) identified by the index number(s) used in the last item listing.\n"
            + "Parameters: INDEX (must be a positive integer)\n"
            + "Example: " + COMMAND_WORD + " 1"
            + "Example: " + COMMAND_WORD + " 5 2";

    public static final String MESSAGE_DELETE_ITEM_SUCCESS = "Deleted Item: %1$s";
    public static final String MESSAGE_DELETE_ITEMS_SUCCESS = "Deleted Items: %1$s";

    private int targetIndex;
    private ArrayList<Integer> targetIndexes;
    private boolean hasMultipleIndexes = false;

    private ArrayList<ReadOnlyItem> itemsToDelete = new ArrayList<ReadOnlyItem>();
    private static final Logger logger = LogsCenter.getLogger(DeleteCommand.class);
    /*
     * Deletes deadline, task, or event by keyword.
     */
    public DeleteCommand(int targetIndex) {
        this.targetIndex = targetIndex;
        hasMultipleIndexes = false;
    }
    
    /*
     * Deletes multiple deadlines, tasks, or events by index.
     */
    public DeleteCommand(ArrayList<Integer> targetIndexes) {
        this.targetIndexes = targetIndexes;
        hasMultipleIndexes = true;
    }


    /**
     * Deletes events, tasks, or deadlines by a single index or ascending multiple indexes.
     */
    @Override
    public CommandResult execute() {
        
        if(!hasMultipleIndexes){

            UnmodifiableObservableList<ReadOnlyItem> lastShownList = model.getFilteredItemList();
            if (lastShownList.size() < targetIndex) {
                indicateAttemptToExecuteIncorrectCommand();
                return new CommandResult(Messages.MESSAGE_INVALID_ITEM_DISPLAYED_INDEX);
            }

            ReadOnlyItem itemToDelete = lastShownList.get(targetIndex - 1);
            
            try {
                model.deleteItem(itemToDelete, String.format(MESSAGE_DELETE_ITEM_SUCCESS, itemToDelete));
            } catch (ItemNotFoundException pnfe) {
                assert false : "The target item cannot be missing";
            }
            
            return new CommandResult(String.format(MESSAGE_DELETE_ITEM_SUCCESS, itemToDelete));
        }

        else {
            UnmodifiableObservableList<ReadOnlyItem> lastShownList = model.getFilteredItemList();
            ListIterator<ReadOnlyItem> lslIterator = lastShownList.listIterator();
            
            Collections.sort(targetIndexes);
            
            for(int index : targetIndexes) {
                
                if (lastShownList.size() < index) {
                    indicateAttemptToExecuteIncorrectCommand();
                    return new CommandResult(Messages.MESSAGE_INVALID_ITEM_DISPLAYED_INDEX);
                }
                
                // reset iterator to point to first object when reach the end of list
                if(lslIterator.nextIndex() >= lastShownList.size()) {
                    lslIterator = lastShownList.listIterator();
                }

                while(lslIterator.nextIndex() != (index - 1)) {
                    if(lslIterator.nextIndex() > (index - 1)) {
                        lslIterator.previous();
                    } else if(lslIterator.nextIndex() < (index - 1)) {
                        lslIterator.next();
                    }
                }

                ReadOnlyItem itemToDelete = lastShownList.get(lslIterator.nextIndex());

                itemsToDelete.add(itemToDelete);
                logger.info("DELETEDITEMS: " + itemsToDelete.toString());
            }
            
            
            try {
                model.deleteItems(itemsToDelete, String.format(MESSAGE_DELETE_ITEMS_SUCCESS, itemsToDelete));
            } catch (ItemNotFoundException pnfe) {
                assert false : "The target item cannot be missing";
            }
            
            StringBuilder printResult = new StringBuilder(MESSAGE_DELETE_ITEMS_SUCCESS);
            
            for(ReadOnlyItem item : itemsToDelete) {
                printResult.append(item.toString());
            }
            
            return new CommandResult(String.format(MESSAGE_DELETE_ITEMS_SUCCESS, itemsToDelete));
        }
    }
}
