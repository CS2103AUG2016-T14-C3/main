package seedu.address.logic.parser;

import seedu.address.logic.commands.*;
import seedu.address.model.item.Date;
import seedu.address.model.item.Time;
import seedu.address.commons.util.StringUtil;
import seedu.address.commons.exceptions.IllegalValueException;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.text.ParseException;

import static seedu.address.commons.core.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.commons.core.Messages.MESSAGE_UNKNOWN_COMMAND;

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

        case SelectCommand.COMMAND_WORD:
            return prepareSelect(arguments);

        case DeleteCommand.COMMAND_WORD:
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
     * Parses arguments in the context of the add person command.
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
                    Date.EMPTY_DATE, // No Start Date
                    Time.EMPTY_TIME, // No Start Time
                    Date.EMPTY_DATE, // No End Date
                    Time.EMPTY_TIME, // No End Time
                    getTagsFromArgs(taskMatcher.group("tagArguments"))
                );
            } else if (deadlineMatcher.matches()) {
                return new AddCommand(
                    "deadline",
                    deadlineMatcher.group("name"),
                    Date.EMPTY_DATE, // No Start Date
                    Time.EMPTY_TIME, // No Start Time
                    deadlineMatcher.group("endDate"),
                    deadlineMatcher.group("endTime"),
                    getTagsFromArgs(deadlineMatcher.group("tagArguments"))
                );
            } else if (eventMatcher.matches()) {
            	SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm");
            	try {
                    if (sdf.parse(eventMatcher.group("endDate") + " " + eventMatcher.group("endTime")).before(sdf.parse(eventMatcher.group("startDate") + " " + eventMatcher.group("startTime")))) {
                        return new IncorrectCommand(String.format(MESSAGE_INVALID_COMMAND_FORMAT, AddCommand.EVENT_MESSAGE_USAGE));
                    }
            	} catch (ParseException e) {
            	    return new IncorrectCommand(String.format(MESSAGE_INVALID_COMMAND_FORMAT, AddCommand.MESSAGE_USAGE));
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
            } else {
                return new IncorrectCommand(String.format(MESSAGE_INVALID_COMMAND_FORMAT, AddCommand.MESSAGE_USAGE));
            }
        } catch (IllegalValueException ive) {
            return new IncorrectCommand(ive.getMessage());
        }
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
                    String.format(MESSAGE_INVALID_COMMAND_FORMAT, DeleteCommand.MESSAGE_USAGE));
        }

        return new DeleteCommand(index.get());
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