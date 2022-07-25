package fr.lesaffrefreres.rh.minibodet.controller;

import fr.lesaffrefreres.rh.minibodet.model.Employee;
import fr.lesaffrefreres.rh.minibodet.view.CalendarView;
import fr.lesaffrefreres.rh.minibodet.view.EmployeeCalendarView;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.HBox;

/**
 * This class is used as the listener for the tab changed event.
 *
 * @author lesaffrefreres
 * @version 1.0
 * @since 1.0
 */
public class TabChangedListener implements EventHandler<Event> {

    TabPane parent;

    /**
     * Set the TabPane linked to this listener.
     * it must be called just after the view is created.
     * @param tp The TabPane linked to this listener.
     */
    public TabChangedListener(TabPane tp) {
        parent = tp;
    }

    /**
     * This method is called when the tab is changed.
     * it is used to synchronize the different tab if needed
     * @param event The event that triggered the call of this method.
     */
    @Override
    public void handle(Event event) {
        Tab tab = (Tab)event.getSource();
        if(tab.isSelected()) {
            if(tab.getId().equals("tab_calendar")) {
                CalendarView cv = (CalendarView)parent.lookup("#calendar");
                cv.setSelectedYear(cv.getCalendar().getYear());
                cv.fullUpdate();
            } else if (tab.getId().equals("tab_employee")) {
                CalendarView cv = (CalendarView)parent.lookup("#employeeCalendar");
                ComboBox<Employee> listEmployeeParam = (ComboBox<Employee>)parent.lookup("#paramEmployeeListPicker");
                ComboBox<Employee> listEmployeeCalendar = (ComboBox<Employee>)parent.lookup("#employeeChoicebox");
                if(!listEmployeeParam.getItems().isEmpty()) {
                    if(listEmployeeParam.getSelectionModel().isEmpty()) {
                        listEmployeeCalendar.getSelectionModel().selectFirst();
                    } else {
                        listEmployeeCalendar.getSelectionModel().select(listEmployeeParam.getSelectionModel().getSelectedItem());
                    }
                    cv.fullUpdate();
                    HBox container = (HBox) parent.lookup("#tab_employee_container");
                    container.setDisable(false);
                } else {
                    HBox container = (HBox) parent.lookup("#tab_employee_container");
                    container.setDisable(true);
                    cv.setCalendar(null);
                }
            } else if (tab.getId().equals("tab_param")) {
                ComboBox<Employee> listEmployeeParam = (ComboBox<Employee>)parent.lookup("#paramEmployeeListPicker");
                ComboBox<Employee> listEmployeeCalendar = (ComboBox<Employee>)parent.lookup("#employeeChoicebox");
                if(!listEmployeeParam.getItems().isEmpty()) {
                    listEmployeeParam.getSelectionModel().select(listEmployeeCalendar.getSelectionModel().getSelectedItem());
                }
            }
        }
    }
}
