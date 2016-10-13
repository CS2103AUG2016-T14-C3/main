package guitests;

import org.junit.Test;

import static org.junit.Assert.assertTrue;

public class ClearCommandTest extends TaskManagerGuiTest {

    @Test
    public void clear() {

        //verify a non-empty list can be cleared
        assertTrue(personListPanel.isListMatching(td.getTypicalItems()));
        assertClearCommandSuccess();

        //verify other commands can work after a clear command
        commandBox.runCommand(td.deadline3.getAddCommand());
        assertTrue(personListPanel.isListMatching(td.deadline3));
        commandBox.runCommand("deleteByIndex 1");
        assertListSize(0);

        //verify clear command works when the list is empty
        assertClearCommandSuccess();
    }

    private void assertClearCommandSuccess() {
        commandBox.runCommand("clear");
        assertListSize(0);
        assertResultMessage("Address book has been cleared!");
    }
}
