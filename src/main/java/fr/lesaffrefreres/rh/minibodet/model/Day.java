package fr.lesaffrefreres.rh.minibodet.model;

import fr.lesaffrefreres.rh.minibodet.view.CalendarView;

import java.time.LocalDate;

/**
 * This Interface represents a day. With a date and a Label.
 * It extend CalendarElement. So it must be used in a CalendarView.
 *
 * @author lesaffrefreres
 * @version 1.0
 * @since 1.0
 * @see CalendarElement
 * @see CalendarView
 */
public interface Day extends CalendarElement{

    /**
     * Return the label id of the day. The label can be recovered using an implementation of DayLabelManager.
     * @return the label id of the day.
     * @see DayLabelManager
     */
    public long getLabelId();

    /**
     * Return the date of the day.
     * @return the date of the day.
     */
    public LocalDate getDate();

    /**
     * Set the label id of the day. Use a DayLabelManager implementation to get the label id.
     * @param id the label to set.
     * @see DayLabelManager
     */
    public void setLabelId(long id);
}
