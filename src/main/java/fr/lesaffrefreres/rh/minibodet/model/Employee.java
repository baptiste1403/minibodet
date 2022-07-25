package fr.lesaffrefreres.rh.minibodet.model;

import javafx.beans.property.StringProperty;

import java.time.DayOfWeek;

/**
 * This interface represents an employee. with a name, a first name, a @{@link Planning} and a @{@link Calendar} of @{@link WorkDay}
 * the name and firstname are represented by a @{@link StringProperty}
 * @author lesaffrefreres
 * @version 1.0
 * @since 1.0
 */
public interface Employee {

    /**
     * Set the name of the employee
     * @param ln the name of the employee
     */
    public void setLastName(String ln);

    /**
     * Set the first name of the employee
     * @param fn the first name of the employee
     */
    public void setFirstName(String fn);

    /**
     * Return the name of the employee
     * @return the name of the employee
     */
    public String getLastName();

    /**
     * Return the first name of the employee
     * @return the first name of the employee
     */
    public String getFirstName();

    /**
     * Return the StringProperty of the firstname of the employee
     * @return the StringProperty of the firstname of the employee
     */
    public StringProperty FirstNameProperty();

    /**
     * Return the StringProperty of the lastname of the employee
     * @return the StringProperty of the lastname of the employee
     */
    public StringProperty LastNameProperty();

    /**
     * Return the calendar of the employee as a @{@link Calendar} of @{@link WorkDay}
     * @param year the year of the calendar to look for
     * @return the calendar of the employee as a @{@link Calendar} of @{@link WorkDay}
     * @param <T> the type of the calendar's days
     * @see Calendar
     * @see WorkDay
     */
    public <T extends WorkDay> Calendar<T> getCalendar(int year);

    /**
     * Set the number of hours worked by the employee at night at a given day of the week on it's planning
     * @param dow the day of the week
     * @param nh the number of hours worked by the employee at night
     */
    public void setPlanningNightHours(DayOfWeek dow, double nh);

    /**
     * Set the total number of hours worked by the employee at a given day of the week on it's planning (night + day)
     * @param dow the day of the week
     * @param th the total number of hours worked by the employee (night + day)
     */
    public void setPlanningTotalHours(DayOfWeek dow, double th);

    /**
     * set the default Label id for the day of the week in it's planning (ex: "work" or "rest")
     * @param dow the day of the week
     * @param id the default Label id for the day of the week in it's planning (ex: "work" or "rest")
     * the Label id is usually provided by an implementation of the @{@link DayLabelManager} interface
     * @see DayLabelManager
     * @see DayLabel
     */
    public void setPlanningLabelId(DayOfWeek dow, long id);

    /**
     * Return the number of hours worked by the employee at night at a given day of the week on it's planning
     * @param dow the day of the week
     * @return the number of hours worked by the employee at night at a given day of the week on it's planning
     */
    public double getPlanningNightHours(DayOfWeek dow);

    /**
     * Return the total number of hours worked by the employee at a given day of the week on it's planning (night + day)
     * @param dow the day of the week
     * @return the total number of hours worked by the employee at a given day of the week on it's planning (night + day)
     */
    public double getPlanningTotalHours(DayOfWeek dow);

    /**
     * Return the default Label id for the day of the week in it's planning
     * @param dow the day of the week
     * @return the default Label id for the day of the week in it's planning
     */
    public long getPlanningLabelId(DayOfWeek dow);
}
