package seedu.taskmanager.storage;

import seedu.taskmanager.commons.exceptions.IllegalValueException;
import seedu.taskmanager.model.item.Date;
import seedu.taskmanager.model.item.Item;
import seedu.taskmanager.model.item.ItemType;
import seedu.taskmanager.model.item.Name;
import seedu.taskmanager.model.item.ReadOnlyItem;
import seedu.taskmanager.model.item.Time;
import seedu.taskmanager.model.tag.Tag;
import seedu.taskmanager.model.tag.UniqueTagList;

import javax.xml.bind.annotation.XmlElement;
import java.util.ArrayList;
import java.util.List;

/**
 * JAXB-friendly version of the Item.
 */
public class XmlAdaptedItem {

    @XmlElement(required = true)
    private String name;
    @XmlElement(required = true)
    private String itemType;
    @XmlElement(required = true)
    private String startDate;
    @XmlElement(required = true)
    private String startTime;
    @XmlElement(required = true)
    private String endDate;
    @XmlElement(required = true)
    private String endTime;
    @XmlElement(required = true)
    private boolean done;

    @XmlElement
    private List<XmlAdaptedTag> tagged = new ArrayList<>();

    /**
     * No-arg constructor for JAXB use.
     */
    public XmlAdaptedItem() {}


    /**
     * Converts a given Item into this class for JAXB use.
     *
     * @param source future changes to this will not affect the created XmlAdaptedItem
     */
    public XmlAdaptedItem(ReadOnlyItem source) {
        name = source.getName().value;
        itemType = source.getItemType().value;
        startDate = source.getStartDate().value;
        startTime = source.getStartTime().value;
        endDate = source.getEndDate().value;
        endTime = source.getEndTime().value;
        done = source.getDone();
        tagged = new ArrayList<>();
        for (Tag tag : source.getTags()) {
            tagged.add(new XmlAdaptedTag(tag));
        }
    }

    /**
     * Converts this jaxb-friendly adapted item object into the model's Item object.
     *
     * @throws IllegalValueException if there were any data constraints violated in the adapted item
     */
    public Item toModelType() throws IllegalValueException {
        final List<Tag> itemTags = new ArrayList<>();
        for (XmlAdaptedTag tag : tagged) {
            itemTags.add(tag.toModelType());
        }
        final ItemType itemType = new ItemType(this.itemType);
        final Name name = new Name(this.name);
        final Date startDate = new Date(this.startDate);
        final Time startTime = new Time(this.startTime);
        final Date endDate = new Date(this.endDate);
        final Time endTime = new Time(this.endTime);
        final UniqueTagList tags = new UniqueTagList(itemTags);
        return new Item(itemType, name, startDate, startTime, endDate, endTime, this.done, tags);
    }
}
