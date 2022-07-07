package fr.lesaffrefreres.rh.minibodet.controller;

import fr.lesaffrefreres.rh.minibodet.model.Employee;
import fr.lesaffrefreres.rh.minibodet.view.CalendarView;
import fr.lesaffrefreres.rh.minibodet.view.EmployeeCalendarView;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.ChoiceBox;

public class EmployeeListChangeListener implements EventHandler<ActionEvent> {

    private CalendarView calendar;
    private EmployeeCalendarView employeeCalendarView;

    public EmployeeListChangeListener(CalendarView cv, EmployeeCalendarView ev) {
        calendar = cv;
        employeeCalendarView = ev;
    }

    @Override
    public void handle(ActionEvent event) {
        ChoiceBox<Employee> cb = (ChoiceBox<Employee>) event.getSource();
        calendar.setCalendar(cb.getValue().getCalendar());
    }
}
