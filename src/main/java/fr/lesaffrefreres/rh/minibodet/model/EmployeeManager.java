package fr.lesaffrefreres.rh.minibodet.model;

import javafx.collections.ObservableList;

/**
 * This Interface is used to manage the employees and the global calendar.
 * It is used to manage the life cycle of the employees.
 * The global calendar is used to set days that must be taken into account by the employee's calendars.
 * @param <T> The type of the employees. It must be a subclass of Employee.
 *
 * @author lesaffrefreres
 * @version 1.0
 * @since 1.0
 */
public interface EmployeeManager<T extends Employee> {

    /**
     * Returns the Global Calendar.
     * @return The Global Calendar.
     * @param <D> The type of the days contained by the calendar. It must be a subclass of Day.
     */
    public <D extends Day> Calendar<D> getCalendar();

    /**
     * Returns the list of all the created employees.
     * @return The list of employees.
     */
    public ObservableList<Employee> getEmployeesList();

    /**
     * Return an employee by its name and first name.
     * @param firstName The first name of the employee.
     * @param lastName The last name of the employee.
     * @return The employee with the given name and first name.
     * if no employee with the given name and first name exists, check implementations for behaviors.
     */
    public T getEmployee(String firstName, String lastName);

    /**
     * Creates a new employee with the given name and first name.
     * @param firstName The first name of the employee.
     * @param lastName The last name of the employee.
     * @return The new employee.
     * if an employee with the given name and first name already exists, check implementations for behaviors.
     */
    public T createEmployee(String firstName, String lastName);
}
