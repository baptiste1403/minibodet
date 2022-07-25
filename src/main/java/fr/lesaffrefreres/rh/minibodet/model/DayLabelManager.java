package fr.lesaffrefreres.rh.minibodet.model;

import javafx.scene.paint.Color;

import java.util.List;
import java.util.Objects;

/**
 * This interface represents a provider of day labels.
 * It's used to control the life cycle of day labels.
 *
 * @author lesaffrefreres
 * @version 1.0
 * @since 1.0
 * @see DayLabel
 */
public interface DayLabelManager {

    /**
     * Create a new day label with the given text and color and return it's id.
     * the returned id can be used to retrieve the label later using {@link #getDayLabelById(long)}
     * @param txt the text of the label.
     * @param c the color of the label.
     * @return the id of the label.
     */
    public long createDayLabel(String txt, Color c);

    /**
     * Return the day label id with the given text
     * @param txt the text of the label to find.
     * @return the label with the given text.
     * if no label with the given text is found, check implementations for behavior.
     */
    public long getDayLabelIdByName(String txt);

    /**
     * Return the day label with the given id.
     * @param id the id of the label to find.
     * @return the label with the given id.
     * if no label with the given id is found, check implementations for behavior.
     */
    public DayLabel getDayLabelById(long id);

    /**
     * Remove the day label with the given id.
     * @param id the id of the label to remove.
     * if no label with the given id is found, check implementations for behavior.
     */
    public void removeDayLabelById(long id);

    /**
     * Set the text of the day label with the given id.
     * @param id the id of the label to set.
     * @param txt the text to set.
     * if no label with the given id is found, check implementations for behavior.
     */
    public void setDayLabelText(long id, String txt);

    /**
     * Set the color of the day label with the given id.
     * @param id the id of the label to set.
     * @param c the color to set.
     * if no label with the given id is found, check implementations for behavior.
     */
    public void setDayLabelColor(long id, Color c);

    /**
     * Check if the day label with the given id exists.
     * return true if the label exists, false otherwise.
     * @param id the id of the label to check.
     * @return true if the label exists, false otherwise.
     */
    public boolean labelExist(long id);

    /**
     * Return the list of all day labels created.
     * @return the list of all day labels.
     */
    public List<DayLabel> getAllDayLabels();

    /**
     * Return the id of the default day label. "undefined"
     * @return the id of the default day label.
     * The default day label always exists.
     */
    public long getUndefinedDayLabelId();
}
