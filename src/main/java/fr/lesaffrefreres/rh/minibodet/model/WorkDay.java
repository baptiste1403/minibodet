package fr.lesaffrefreres.rh.minibodet.model;

/**
 * This interface represent a work day in the calendar with schedule and comment and a label.
 *
 * @author lesaffrefreres
 * @version 1.0
 * @since 1.0
 *
 * @see WorkSchedule
 * @see Day
 */
public interface WorkDay extends Day{
    /**
     * This method is used to get the schedule of the work day.
     * @return the schedule of the work day.
     */
    public WorkSchedule getSchedule();

    /**
     * This method is used to get the comment of the work day.
     * @return the comment of the work day.
     */
    public String getComment();

    /**
     * This method is used to set the Comment of the work day.
     * @param com the comment of the work day.
     */
    public void setComment(String com);
}
