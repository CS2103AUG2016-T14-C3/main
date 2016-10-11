package seedu.address.logic.commands;

import java.util.Set;
import javafx.collections.transformation.FilteredList;
import seedu.address.commons.exceptions.IllegalValueException;

/**
 * Lists all tasks in the task manager to the user.
 */

public class ListTaskCommand extends Command {

    public static final String COMMAND_WORD = "listtask";

    public static final String MESSAGE_SUCCESS = "Listed all tasks";
    
    public static final String MESSAGE_USAGE = COMMAND_WORD + ": Lists all tasks.\n"
            + "Parameters: KEYWORD [MORE_KEYWORDS]...\n"
            + "Example: " + COMMAND_WORD; 

    public ListTaskCommand() {}

    

    @Override
    public CommandResult execute() {
        model.updateFilteredListToShowTask();
        
        return new CommandResult(MESSAGE_SUCCESS);
    }
}

