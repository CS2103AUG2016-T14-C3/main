package guitests;

import org.junit.Test;

import seedu.taskmanager.commons.core.Messages;
import seedu.taskmanager.logic.commands.DeleteCommand;
import seedu.taskmanager.testutil.TestItem;
import seedu.taskmanager.testutil.TestUtil;

import static org.junit.Assert.assertTrue;
import static seedu.taskmanager.logic.commands.DeleteCommand.MESSAGE_DELETE_ITEM_SUCCESS;
import static seedu.taskmanager.logic.commands.DeleteCommand.MESSAGE_DELETE_ITEMS_SUCCESS;

import java.util.ArrayList;
import static seedu.taskmanager.commons.core.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.taskmanager.commons.core.Messages.MESSAGE_INVALID_ITEM_DISPLAYED_INDEX;

//@@author A0143641M   
public class DeleteCommandTest extends TaskManagerGuiTest {

    @Test
    public void deleteItem() {

        /* Delete single items */
        //delete the first in the list
        TestItem[] currentList = td.getTypicalItems();
        int targetIndex = 1;
        assertDeleteSuccess(targetIndex, currentList);

        //delete the last in the list
        currentList = TestUtil.removeItemFromList(currentList, targetIndex);
        targetIndex = currentList.length;
        assertDeleteSuccess(targetIndex, currentList);

        //delete from the middle of the list
        currentList = TestUtil.removeItemFromList(currentList, targetIndex);
        targetIndex = currentList.length/2;
        assertDeleteSuccess(targetIndex, currentList);
        
        //invalid index
        commandBox.runCommand("delete " + (currentList.length + 1));
        assertResultMessage(MESSAGE_INVALID_ITEM_DISPLAYED_INDEX);
        
        //invalid argument
        commandBox.runCommand("delete nameOfItem");
        assertResultMessage(String.format(MESSAGE_INVALID_COMMAND_FORMAT, DeleteCommand.MESSAGE_USAGE));
        
        //invalid command
        commandBox.runCommand("deletes 1");
        assertResultMessage(Messages.MESSAGE_UNKNOWN_COMMAND);
    }

    @Test
    public void deleteMultipleItems() {
        
        /* Delete multiple items */
        // delete first and last in the list
        TestItem[] currentList = td.getTypicalItems();
        int[] targetTwoIndexes = {1, currentList.length};
        assertDeleteSuccess(targetTwoIndexes, currentList);
        
        // delete all items except the first item
        currentList = TestUtil.removeItemsFromList(currentList, targetTwoIndexes);
        int[] targetMultipleIndexes = new int[currentList.length-1];
        int arrayIndex = 0;
        for(int i = 1; i < currentList.length; i++) {
            targetMultipleIndexes[arrayIndex] = i + 1;
            arrayIndex += 1;
        }
        assertDeleteSuccess(targetMultipleIndexes, currentList);
        
        // delete invalid indexes
        currentList = TestUtil.removeItemsFromList(currentList, targetMultipleIndexes);
        targetTwoIndexes[0] = currentList.length + 1;
        targetTwoIndexes[1] = targetTwoIndexes[0] + 1;
        commandBox.runCommand("delete " + targetTwoIndexes[0] + " " + targetTwoIndexes[1]);
        assertResultMessage(MESSAGE_INVALID_ITEM_DISPLAYED_INDEX);

        // non-positive integer index
        commandBox.runCommand("delete 0 " + (currentList.length + 1));
        assertResultMessage(String.format(MESSAGE_INVALID_COMMAND_FORMAT, DeleteCommand.MESSAGE_USAGE));
        
    }
    
    @Test
    public void deleteItemShortCommand() {
        
        //delete the first in the list
        TestItem[] currentList = td.getTypicalItems();
        int targetIndex = 1;
        assertDeleteSuccessShortCommand(currentList, targetIndex);

        //delete the last in the list
        currentList = TestUtil.removeItemFromList(currentList, targetIndex);
        targetIndex = currentList.length;
        assertDeleteSuccessShortCommand(currentList, targetIndex);

        //delete from the middle of the list
        currentList = TestUtil.removeItemFromList(currentList, targetIndex);
        targetIndex = currentList.length/2;
        assertDeleteSuccessShortCommand(currentList, targetIndex);
        
        //invalid index
        commandBox.runCommand("del " + (currentList.length + 1));
        assertResultMessage(MESSAGE_INVALID_ITEM_DISPLAYED_INDEX);
        
        //invalid argument
        commandBox.runCommand("del name");
        assertResultMessage(String.format(MESSAGE_INVALID_COMMAND_FORMAT, DeleteCommand.MESSAGE_USAGE));
        
        //invalid command
        commandBox.runCommand("delByName");
        assertResultMessage(String.format(Messages.MESSAGE_UNKNOWN_COMMAND));
    }

    /**
     * Runs the delete command to delete the items at specified indexes and confirms the result is correct.
     * @param targetIndexes e.g. to delete the first and last items in the list, 1 and currentlist.length should be given as the target index.
     * @param currentList A copy of the current list of items (before deletion).
     */
    private void assertDeleteSuccess(int[] targetIndexes, final TestItem[] currentList) {
        
        ArrayList<TestItem> itemsToDelete = new ArrayList<TestItem>();
        for(int index : targetIndexes) {
            itemsToDelete.add(currentList[index-1]); //-1 because array uses zero indexing
        }
        TestItem[] expectedRemainder = TestUtil.removeItemsFromList(currentList, targetIndexes);

        commandBox.runCommand("delete " + obtainIndexesStringFormat(targetIndexes));

        //confirm the list now contains all previous items except the deleted items
        assertTrue(shortItemListPanel.isListMatching(expectedRemainder));

        //confirm the result message is correct
        assertResultMessage(String.format(MESSAGE_DELETE_ITEMS_SUCCESS, itemsToDelete));
    }

    /**
     * Obtain the indexes to be deleted in the right format of a string for parameters of the command.
     * @param targetIndexes the indexes of the items to be deleted
     */
    private StringBuilder obtainIndexesStringFormat(int[] targetIndexes) {
        StringBuilder targetIndexesStringFormat = new StringBuilder();
        for(int i = 0; i < targetIndexes.length; i++) {
            targetIndexesStringFormat.append(String.valueOf(targetIndexes[i]));
            if(i != (targetIndexes.length - 1)) {
                targetIndexesStringFormat.append(" ");
            }
        }
        return targetIndexesStringFormat;
    }
    
    private void assertDeleteSuccessShortCommand(final TestItem[] currentList, int... targetIndexes) {
        if (targetIndexes.length == 1) {
            TestItem itemToDelete = currentList[targetIndexes[0]-1]; //-1 because array uses zero indexing
            TestItem[] expectedRemainder = TestUtil.removeItemFromList(currentList, targetIndexes[0]);
            
            commandBox.runCommand("del " + targetIndexes[0]);
            
            //confirm the list now contains all previous items except the deleted item
            assertTrue(shortItemListPanel.isListMatching(expectedRemainder));

            //confirm the result message is correct
            assertResultMessage(String.format(MESSAGE_DELETE_ITEM_SUCCESS, itemToDelete));
        }
        else if (targetIndexes.length > 1) {
            ArrayList<TestItem> itemsToDelete = new ArrayList<TestItem>();
            for(int index : targetIndexes) {
                itemsToDelete.add(currentList[index-1]); //-1 because array uses zero indexing
            }
            TestItem[] expectedRemainder = TestUtil.removeItemsFromList(currentList, targetIndexes);
            
            commandBox.runCommand("del " + obtainIndexesStringFormat(targetIndexes));
            
            //confirm the list now contains all previous items except the deleted items
            assertTrue(shortItemListPanel.isListMatching(expectedRemainder));

            //confirm the result message is correct
            assertResultMessage(String.format(MESSAGE_DELETE_ITEMS_SUCCESS, itemsToDelete));
        }
    }

    //@@author A0143641M-reused
    
    /**
     * Runs the delete command to delete the item at specified index and confirms the result is correct.
     * @param targetIndexOneIndexed e.g. to delete the first item in the list, 1 should be given as the target index.
     * @param currentList A copy of the current list of items (before deletion).
     */
    private void assertDeleteSuccess(int targetIndexOneIndexed, final TestItem[] currentList) {
        TestItem itemToDelete = currentList[targetIndexOneIndexed-1]; //-1 because array uses zero indexing
        TestItem[] expectedRemainder = TestUtil.removeItemFromList(currentList, targetIndexOneIndexed);

        commandBox.runCommand("delete " + targetIndexOneIndexed);
        
        //confirm the list now contains all previous items except the deleted item
        assertTrue(shortItemListPanel.isListMatching(expectedRemainder));

        //confirm the result message is correct
        assertResultMessage(String.format(MESSAGE_DELETE_ITEM_SUCCESS, itemToDelete));
    }

}
