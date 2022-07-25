package fr.lesaffrefreres.rh.minibodet.model;

/**
 * This interface represents a work schedule for an employee. with a total number of hours and a number of hours worked at night.
 *
 *  @author lesaffrefreres
 *  @version 1.0
 *  @since 1.0
 *
 */
public interface WorkSchedule {
    /**
     * returns the number of hours worked at night.
     * @return the number of hours worked at night.
     */
    public double getNightHours();

    /**
     * returns the total number of hours.
     * @return the total number of hours.
     */
    public double getTotalHours();

    /**
     * Set the number of hours worked at night.
     * @param nh the number of hours worked at night.
     */
    public void setNightHours(double nh);

    /**
     * Set the total number of hours.
     * @param th the total number of hours.
     */
    public void setTotalHours(double th);
}
