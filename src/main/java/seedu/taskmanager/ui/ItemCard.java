package seedu.taskmanager.ui;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import seedu.taskmanager.logic.commands.DoneCommand;
import seedu.taskmanager.logic.commands.NotDoneCommand;
import seedu.taskmanager.logic.Logic;
import seedu.taskmanager.logic.commands.DeleteCommand;
import seedu.taskmanager.model.item.ItemType;
import seedu.taskmanager.model.item.ReadOnlyItem;

import java.util.Date;

import org.ocpsoft.prettytime.PrettyTime;

public class ItemCard extends UiPart{

    private static final String FXML = "ItemListCard.fxml";

    @FXML
    private HBox cardPane;
    
    @FXML
    private Text name;
    
    //@@author A0065571A 
    @FXML
    private Text itemType;
    @FXML
    private Label endTime;
    @FXML
    private Label endDate;
    @FXML
    private Label endFromNow;
    @FXML
    private Label startTime;
    @FXML
    private Label startDate;
    @FXML
    private Text tags;

    private ReadOnlyItem item;
    @FXML
    private Button doneButton;
    @FXML
    private Button deleteButton;

    private int displayedIndex;
    private Logic logic;
    public static final String DONE_TEXT = "Done";
    public static final String UNDONE_TEXT = "Not Done";
    
    @FXML
    public void doneButtonAction(ActionEvent event) {
        Button button = (Button) event.getSource();
        if (button.getText().equals(UNDONE_TEXT)) {
            button.setText(DONE_TEXT);
            logic.execute(DoneCommand.COMMAND_WORD + " " + displayedIndex);
        } else {
            button.setText(UNDONE_TEXT);
            logic.execute(NotDoneCommand.COMMAND_WORD + " " + displayedIndex);
        }
    }
    
    @FXML
    public void deleteButtonAction(ActionEvent event) {
        logic.execute(DeleteCommand.COMMAND_WORD + " " + displayedIndex);
    }

    public ItemCard(){

    }

    public static ItemCard load(ReadOnlyItem item, int displayedIndex, Logic logic){
        ItemCard card = new ItemCard();
        card.item = item;
        card.displayedIndex = displayedIndex;
        card.configure(logic);
        return UiPartLoader.loadUiPart(card);
    }
    
    public void configure(Logic logic) {
        this.logic = logic;
    }

    @FXML
    public void initialize() {
        name.setText(item.getName().value);
        name.setFill(Color.web("#4f4f4f"));
        itemType.setText(item.getItemType().value);
        itemType.setFill(Color.web("#007fff"));
        endTime.setText(item.getEndTime().value);
        endTime.setStyle("-fx-text-fill: #a00000");
        endDate.setText(item.getEndDate().value);
        endDate.setStyle("-fx-text-fill: #a00000");
        String endFromNowText = "";
        Date endFromNowDate;
        PrettyTime p = new PrettyTime();
        if (item.getItemType().isEvent()) {
            endFromNowDate = item.getEndDateTime();
            endFromNowText = p.format(endFromNowDate);
            if (item.isInProgress()) {
                endFromNow.setText("Ends " + endFromNowText);
                endFromNow.setStyle("-fx-text-fill: #0083ff");
            } else if (item.isPastDeadline()) {
                endFromNow.setText("Ended " + endFromNowText);
                endFromNow.setStyle("-fx-text-fill: #898989");
            } else {
                endFromNow.setText("Ends " + endFromNowText);
            }
        } else if (item.getItemType().isDeadline()) {
            endFromNowDate = item.getEndDateTime();
            endFromNowText = p.format(endFromNowDate);
            if (item.isPastDeadline()) { // Past Deadline
                endFromNow.setText("Ended " + endFromNowText);
                endFromNow.setStyle("-fx-text-fill: #FF0000");
            } else if (item.isNearDeadline()) { // 24 Hours Before End Date
                endFromNow.setText("Ends " + endFromNowText);
                endFromNow.setStyle("-fx-text-fill: #ff8300");
            } else { 
                endFromNow.setText("Ends " + endFromNowText);
            }
        } else { 
            endFromNow.setText(endFromNowText);
        }
        startTime.setText(item.getStartTime().value);
        startTime.setStyle("-fx-text-fill: #048200");
        startDate.setText(item.getStartDate().value);
        startDate.setStyle("-fx-text-fill: #048200");
        tags.setText(item.tagsString());
        if (item.getDone()) {
            doneButton.setStyle("-fx-background-color: #28A428");
            doneButton.setText(DONE_TEXT);
        } else {
        	doneButton.setText(UNDONE_TEXT);
        }
        deleteButton.setStyle("-fx-background-color: c10000");
        tags.setText(item.tagsString());
        tags.setFill(Color.web("#4f4f4f"));
    }

    public HBox getLayout() {
        return cardPane;
    }

    @Override
    public void setNode(Node node) {
        cardPane = (HBox)node;
    }

    @Override
    public String getFxmlPath() {
        return FXML;
    }
}
