package fr.lesaffrefreres.rh.minibodet.view;

import fr.lesaffrefreres.rh.minibodet.controller.*;
import fr.lesaffrefreres.rh.minibodet.model.*;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.util.Locale;
import java.util.ResourceBundle;

public class EmployeeCalendarView extends VBox implements ChangeListener<Scene> {

    private String calendarViewId;

    private CalendarView calendar;

    private EmployeeCalendarViewController controller;

    public String getCalendar() {
        return calendarViewId;
    }

    public void setCalendar(String cvi) {
        calendarViewId = cvi;
    }

    private CalendarView getCalendarView() {
        if(calendar == null) {
            calendar = (CalendarView) getScene().getRoot().lookup("#" + calendarViewId);
        }
        return calendar;
    }

    public EmployeeCalendarView() {
        ResourceBundle bundle = ResourceBundle.getBundle("/strings", new Locale("fr", "FR"));
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/employee-calendar-view.fxml"), bundle);
        fxmlLoader.setRoot(this);


        try {
            fxmlLoader.load();
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }

        controller = fxmlLoader.getController();

        sceneProperty().addListener(this); // dirty method to fire a method when the scene is loaded
    }

    @Override
    public void changed(ObservableValue<? extends Scene> observable, Scene oldValue, Scene newValue) {
        controller.setCalendarView(getCalendarView());

        EmployeeManager<Employee> em = SQLEmployeeManager.getInstance();

        ComboBox<Employee> cb = (ComboBox<Employee>) getScene().lookup("#employeeChoicebox");
        cb.setItems(em.getEmployeesList());

        Spinner<Integer> spTotal = (Spinner<Integer>) getScene().lookup("#choiceTotalHours");
        spTotal.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 24, 0));
        spTotal.valueProperty().addListener(controller::onTotalHoursChange);

        Spinner<Integer> spNight = (Spinner<Integer>) getScene().lookup("#choiceNightHours");
        spNight.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 24, 0));
        spNight.valueProperty().addListener(controller::onNightHoursChange);

        getCalendarView().addEventHandlers(controller);

        if(!em.getEmployeesList().isEmpty()) {
            cb.getSelectionModel().selectFirst();
            getCalendarView().setCalendar(cb.getValue().getCalendar());
        }
    }
}
