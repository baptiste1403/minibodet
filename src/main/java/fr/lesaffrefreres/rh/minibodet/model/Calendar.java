package fr.lesaffrefreres.rh.minibodet.model;

import java.time.LocalDate;

/**
 * This interface defines the behavior of a calendar.
 *
 * @param <T> the type of the elements of the calendar that reprensent the days.
 *
 * @author lesaffrefreres
 * @version 1.0
 * @since 1.0
 */
public interface Calendar<T> {
    /**
     * Return the day corresponding to the given date.
     * @param date the date of the day to return.
     * @return the day corresponding to the given date.
     */
    public T getDay(LocalDate date);

    /**
     * Return the year of the calendar.
     * @return the year of the calendar.
     */
    public int getYear();

    /**
     * Set the year of the calendar. It just an indication, implementation can contain day out of this year.
     * @param y the year of the calendar.
     */
    public void setYear(int y);
}
