package fr.lesaffrefreres.rh.minibodet.model;

import javafx.collections.ObservableList;

public interface EmployeeManager<T extends Employee> {

    public <D extends Day> Calendar<D> getCalendar();

    public ObservableList<Employee> getEmployeesList();

    public T getEmployee(String firstName, String lastName);

    public T createEmployee(String firstName, String lastName);
}
