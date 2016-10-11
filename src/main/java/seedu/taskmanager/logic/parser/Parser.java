package seedu.taskmanager.logic.parser;

import seedu.taskmanager.commons.exceptions.IllegalValueException;
import seedu.taskmanager.commons.util.StringUtil;
import seedu.taskmanager.logic.commands.AddCommand;
import seedu.taskmanager.logic.commands.ClearCommand;
import seedu.taskmanager.logic.commands.Command;
import seedu.taskmanager.logic.commands.DeleteByIndexCommand;
import seedu.taskmanager.logic.commands.EditCommand;
import seedu.taskmanager.logic.commands.ExitCommand;
import seedu.taskmanager.logic.commands.FindCommand;
import seedu.taskmanager.logic.commands.HelpCommand;
import seedu.taskmanager.logic.commands.IncorrectCommand;
import seedu.taskmanager.logic.commands.ListCommand;
import seedu.taskmanager.logic.commands.SelectCommand;
import seedu.taskmanager.model.item.Date;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static seedu.taskmanager.commons.core.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.taskmanager.commons.core.Messages.MESSAGE_UNKNOWN_COMMAND;

import java.text.ParseException;

/**
 * Parses user input.
 */
public class Parser {

    /**
     * Used for initial separation of command word and args.
     */
    private static final Pattern BASIC_COMMAND_FORMAT = Pattern.compile("(?<commandWord>\\S+)(?<arguments>.*)");

    private static final Pattern ITEM_INDEX_ARGS_FORMAT = Pattern.compile("(?<targetIndex>.+)");

    private static final Pattern KEYWORDS_ARGS_FORMAT =
            Pattern.compile("(?<keywords>\\S+(?:\\s+\\S+)*)"); // one or more keywords separated by whitespace

    private static final Pattern TASK_DATA_ARGS_FORMAT = // '/' forward slashes are reserved for delimiter prefixes
            Pattern.compile("(T|t)(A|a)(S|s)(K|k)"
                    + " n/(?<name>[^/]+)"
                    + "(?<tagArguments>(?: t/[^/]+)*)"); // variable number of tags

    private static final Pattern DEADLINE_DATA_ARGS_FORMAT = // '/' forward slashes are reserved for delimiter prefixes
            Pattern.compile("(D|d)(E|e)(A|a)(D|d)(L|l)(I|i)(N|n)(E|e)"
                    + " n/(?<name>[^/]+)"
                    + " ed/(?<endDate>[^/]+)"
                    + " et/(?<endTime>[^/]+)"
                    + "(?<tagArguments>(?: t/[^/]+)*)"); // variable number of tags

    private static final Pattern EVENT_DATA_ARGS_FORMAT = // '/' forward slashes are reserved for delimiter prefixes
            Pattern.compile("(E|e)(V|v)(E|e)(N|n)(T|t)"
                    + " n/(?<name>[^/]+)"
            		+ "sd/(?<startDate>[^/]+)"
                    + "st/(?<startTime>[^/]+)"
                    + " ed/(?<endDate>[^/]+)"
                    + " et/(?<endTime>[^/]+)"
                    + "(?<tagArguments>(?: t/[^/]+)*)"); // variable number of tags
    
    private static final Pattern EDIT_ARGS_FORMAT = // '/' forward slashes are reserved for delimiter prefixes
            Pattern.compile("(?<targetIndex>.+)"
                    + " n/(?<name>[^/]+)");

    public Parser() {}

    /**
     * Parses user input into command for execution.
     *
     * @param userInput full user input string
     * @return the command based on the user input
     */
    public Command parseCommand(String userInput) {
        final Matcher matcher = BASIC_COMMAND_FORMAT.matcher(userInput.trim());
        if (!matcher.matches()) {
            return new IncorrectCommand(String.format(MESSAGE_INVALID_COMMAND_FORMAT, HelpCommand.MESSAGE_USAGE));
        }

        final String commandWord = matcher.group("commandWord");
        final String arguments = matcher.group("arguments");
        switch (commandWord) {

        case AddCommand.COMMAND_WORD:
            return prepareAdd(arguments);

        case EditCommand.COMMAND_WORD:
            return prepareEdit(arguments);
        case SelectCommand.COMMAND_WORD:
            return prepareSelect(arguments);

        case DeleteByIndexCommand.COMMAND_WORD:
            return prepareDelete(arguments);

        case ClearCommand.COMMAND_WORD:
            return new ClearCommand();

        case FindCommand.COMMAND_WORD:
            return prepareFind(arguments);

        case ListCommand.COMMAND_WORD:
            return new ListCommand();

        case ExitCommand.COMMAND_WORD:
            return new ExitCommand();

        case HelpCommand.COMMAND_WORD:
            return new HelpCommand();

        default:
            return new IncorrectCommand(MESSAGE_UNKNOWN_COMMAND);
        }
    }

    /**
     * Parses arguments in the context of the add item command.
     *
     * @param args full command args string
     * @return the prepared command
     */
    private Command prepareAdd(String args){
        final Matcher taskMatcher = TASK_DATA_ARGS_FORMAT.matcher(args.trim());
        final Matcher deadlineMatcher = DEADLINE_DATA_ARGS_FORMAT.matcher(args.trim());
        final Matcher eventMatcher = EVENT_DATA_ARGS_FORMAT.matcher(args.trim());
        // Validate arg string format
        if (!taskMatcher.matches() && !deadlineMatcher.matches() && !eventMatcher.matches()) {
        	return new IncorrectCommand(String.format(MESSAGE_INVALID_COMMAND_FORMAT, AddCommand.MESSAGE_USAGE));
        }
        try {
            if (taskMatcher.matches()) {
                return new AddCommand(
                    "task",
                    taskMatcher.group("name"),
                    getTagsFromArgs(taskMatcher.group("tagArguments"))
                );
            } else if (deadlineMatcher.matches()) {
            	return addDeadline(deadlineMatcher);
            } else if (eventMatcher.matches()) {
                return addEvent(eventMatcher);
            } else {
                return new IncorrectCommand(String.format(MESSAGE_INVALID_COMMAND_FORMAT, AddCommand.MESSAGE_USAGE));
            }
        } catch (IllegalValueException ive) {
            return new IncorrectCommand(ive.getMessage());
        }
    }

	private Command addEvent(final Matcher eventMatcher) throws IllegalValueException {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm");
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
		try {	
		    df.setLenient(false);
		    // If yyyy-MM-dd
		    String startDateString;
		    String endDateString;
		    String[] parts = eventMatcher.group("endDate").split("-");
		    if (parts.length == 3) {
		        endDateString = eventMatcher.group("endDate");
		        df.parse(eventMatcher.group("endDate"));
		    } else { // MM-dd
		        LocalDateTime ldt = LocalDateTime.now();
		        endDateString = ldt.getYear() + "-" + eventMatcher.group("endDate");
		        df.parse(endDateString);
		    }
		    String[] parts2 = eventMatcher.group("startDate").split("-");
		    // If yyyy-MM-dd
		    if (parts2.length == 3) {
		        startDateString = eventMatcher.group("startDate");
		        df.parse(eventMatcher.group("startDate"));
		    } else { // MM-dd
		        LocalDateTime ldt = LocalDateTime.now();
		        startDateString = ldt.getYear() + "-" + eventMatcher.group("startDate");
		        df.parse(startDateString);
		    }
		    if (sdf.parse(endDateString + " " + eventMatcher.group("endTime")).before(sdf.parse(startDateString + " " + eventMatcher.group("startTime")))) {
		        return new IncorrectCommand(String.format(MESSAGE_INVALID_COMMAND_FORMAT, AddCommand.EVENT_MESSAGE_USAGE));
		    }
		} catch (ParseException e) {
		    return new IncorrectCommand(String.format(MESSAGE_INVALID_COMMAND_FORMAT, Date.MESSAGE_DATE_CONSTRAINTS));
		}
		return new AddCommand(
		    "event",
		    eventMatcher.group("name"),
		    eventMatcher.group("startDate"),
		    eventMatcher.group("startTime"),
		    eventMatcher.group("endDate"),
		    eventMatcher.group("endTime"),
		    getTagsFromArgs(eventMatcher.group("tagArguments"))
		);
	}

	private Command addDeadline(final Matcher deadlineMatcher) throws IllegalValueException {
		try {
		    SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
		    SimpleDateFormat df2 = new SimpleDateFormat("MM-dd");
		    df.setLenient(false);
		    String[] parts = deadlineMatcher.group("endDate").split("-");
		    // If yyyy-MM-dd
		    if (parts.length == 3) {
		        df.parse(deadlineMatcher.group("endDate"));
		    } else { // MM-dd
		        df2.parse(deadlineMatcher.group("endDate"));
		    }
		} catch (ParseException e) {
			return new IncorrectCommand(String.format(MESSAGE_INVALID_COMMAND_FORMAT, Date.MESSAGE_DATE_CONSTRAINTS));
		}
		return new AddCommand(
		    "deadline",
		    deadlineMatcher.group("name"),
		    deadlineMatcher.group("endDate"),
		    deadlineMatcher.group("endTime"),
		    getTagsFromArgs(deadlineMatcher.group("tagArguments"))
		);
	}

    /**
     * Parses arguments in the context of the edit item command.
     *
     * @param args full command args string
     * @return the prepared command
     */
    private Command prepareEdit(String args) {
        final Matcher matcher = EDIT_ARGS_FORMAT.matcher(args.trim());
        if (matcher.matches()) {
            Optional<Integer> index = parseIndex(matcher.group("targetIndex"));
            if(index.isPresent()){
                try {
                    return new EditCommand(index.get(), matcher.group("name"));
                } catch (IllegalValueException ive) {
                    return new IncorrectCommand(ive.getMessage());
                }
            }
        }
        return new IncorrectCommand(String.format(MESSAGE_INVALID_COMMAND_FORMAT, EditCommand.MESSAGE_USAGE));
    }
    

    /**
     * Extracts the new person's tags from the add command's tag arguments string.
     * Merges duplicate tag strings.
     */
    private static Set<String> getTagsFromArgs(String tagArguments) throws IllegalValueException {
        // no tags
        if (tagArguments.isEmpty()) {
            return Collections.emptySet();
        }
        // replace first delimiter prefix, then split
        final Collection<String> tagStrings = Arrays.asList(tagArguments.replaceFirst(" t/", "").split(" t/"));
        return new HashSet<>(tagStrings);
    }

    /**
     * Parses arguments in the context of the delete person command.
     *
     * @param args full command args string
     * @return the prepared command
     */
    private Command prepareDelete(String args) {

        Optional<Integer> index = parseIndex(args);
        if(!index.isPresent()){
            return new IncorrectCommand(
                    String.format(MESSAGE_INVALID_COMMAND_FORMAT, DeleteByIndexCommand.MESSAGE_USAGE));
        }

        return new DeleteByIndexCommand(index.get());
    }

    /**
     * Parses arguments in the context of the select person command.
     *
     * @param args full command args string
     * @return the prepared command
     */
    private Command prepareSelect(String args) {
        Optional<Integer> index = parseIndex(args);
        if(!index.isPresent()){
            return new IncorrectCommand(
                    String.format(MESSAGE_INVALID_COMMAND_FORMAT, SelectCommand.MESSAGE_USAGE));
        }

        return new SelectCommand(index.get());
    }
    
    /**
     * Returns the specified index in the {@code command} IF a positive unsigned integer is given as the index.
     *   Returns an {@code Optional.empty()} otherwise.
     */
    private Optional<Integer> parseIndex(String command) {
        final Matcher matcher = ITEM_INDEX_ARGS_FORMAT.matcher(command.trim());
        if (!matcher.matches()) {
            return Optional.empty();
        }

        String index = matcher.group("targetIndex");
        if(!StringUtil.isUnsignedInteger(index)){
            return Optional.empty();
        }
        return Optional.of(Integer.parseInt(index));

    }

    /**
     * Parses arguments in the context of the find person command.
     *
     * @param args full command args string
     * @return the prepared command
     */
    private Command prepareFind(String args) {
        final Matcher matcher = KEYWORDS_ARGS_FORMAT.matcher(args.trim());
        if (!matcher.matches()) {
            return new IncorrectCommand(String.format(MESSAGE_INVALID_COMMAND_FORMAT,
                    FindCommand.MESSAGE_USAGE));
        }

        // keywords delimited by whitespace
        final String[] keywords = matcher.group("keywords").split("\\s+");
        final Set<String> keywordSet = new HashSet<>(Arrays.asList(keywords));
        return new FindCommand(keywordSet);
    }

}