package fr.lesaffrefreres.rh.minibodet.model;

import java.time.DayOfWeek;

/**
 * This Interface represents a planning of an employee for a week.
 *
 * @author lesaffrefreres
 * @version 1.0
 * @since 1.0
 */
public interface Planning {

    /**
     * Returns the schedule of the employee's planning for the given day.
     * @param dow The day of the week.
     * @return The schedule of the employee's planning for the given day.
     * @see WorkSchedule
     */
    public WorkSchedule getSchedule(DayOfWeek dow);

    /**
     * Return the default Label id for the planning at a given day.
     * @param dow The day of the week.
     * @return The default Label id for the planning at a given day.
     */
    public long getDayLabelId(DayOfWeek dow);


    /**
     * Sets the default Label id for the planning at a given day.
     * @param dow The day of the week.
     * @param id The default Label id for the planning at a given day.
     */
    public void setDayLabel(DayOfWeek dow, long id);
}
