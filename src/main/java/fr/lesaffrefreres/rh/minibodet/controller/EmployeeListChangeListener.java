package fr.lesaffrefreres.rh.minibodet.controller;

import fr.lesaffrefreres.rh.minibodet.model.Employee;
import fr.lesaffrefreres.rh.minibodet.model.SimpleEmployee;
import fr.lesaffrefreres.rh.minibodet.view.CalendarView;
import fr.lesaffrefreres.rh.minibodet.view.EmployeeView;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.ChoiceBox;

public class EmployeeListChangeListener implements EventHandler<ActionEvent> {

    private CalendarView calendar;
    private EmployeeView employeeView;

    public EmployeeListChangeListener(CalendarView cv, EmployeeView ev) {
        calendar = cv;
        employeeView = ev;
    }

    @Override
    public void handle(ActionEvent event) {
        ChoiceBox<Employee> cb = (ChoiceBox<Employee>) event.getSource();
        calendar.setCalendar(cb.getValue().getCalendar());
        employeeView.update();
    }
}
