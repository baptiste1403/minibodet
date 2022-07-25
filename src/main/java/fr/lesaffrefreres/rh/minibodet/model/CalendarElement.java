package fr.lesaffrefreres.rh.minibodet.model;

import fr.lesaffrefreres.rh.minibodet.view.CalendarView;
import javafx.scene.control.Label;

/**
 * This interface represents a calendar element. In a CalendarView, a calendar element is a day.
 * It's used to set the label displayed in the calendar.
 *
 * @author lesaffrefreres
 * @version 1.0
 * @since 1.0
 * @see CalendarView
 */
public interface CalendarElement {

    /**
     * Must be implemented to set the label displayed in the calendar.
     * It will be called by the CalendarView to set the label for unselected days.
     * Take in consideration that the Calendar view will add border to the label after this method is called.
     * @param label the label to set.
     */
    public void setView(Label label);

    /**
     * Must be implemented to set the label displayed in the calendar.
     * It will be called by the CalendarView to set the label for selected days.
     * Take in consideration that the Calendar view will add border to the label after this method is called.
     * @param label the label to set.
     */
    public void setViewOnSelected(Label label);

    /**
     * Set the label for this day.
     * @param labelId the label to set.
     */
    public void setLabelId(long labelId);
}
