package fr.lesaffrefreres.rh.minibodet.view;

import fr.lesaffrefreres.rh.minibodet.controller.EmployeeCalendarSelectionListener;
import fr.lesaffrefreres.rh.minibodet.controller.EmployeeListChangeListener;
import fr.lesaffrefreres.rh.minibodet.controller.NightHourSpinnerChangeListener;
import fr.lesaffrefreres.rh.minibodet.controller.TotalHourSpinnerChangeListener;
import fr.lesaffrefreres.rh.minibodet.model.*;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.Locale;
import java.util.ResourceBundle;

public class EmployeeView extends VBox implements ChangeListener<Scene> {

    private String calendarViewId;

    private CalendarView calendar;

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

    public EmployeeView() {
        ResourceBundle bundle = ResourceBundle.getBundle("/strings", new Locale("fr", "FR"));
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/employee-view.fxml"), bundle);
        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);

        try {
            fxmlLoader.load();
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }

        sceneProperty().addListener(this); // dirty method to fire a method when the scene is loaded
    }

    @Override
    public void changed(ObservableValue<? extends Scene> observable, Scene oldValue, Scene newValue) {
        if(newValue == null) {
            return;
        }

        ChoiceBox<Employee> cb = (ChoiceBox<Employee>) getScene().lookup("#employee_choicebox");

        cb.setOnAction(new EmployeeListChangeListener(getCalendarView(), this));

        Spinner spTotal = (Spinner) getScene().lookup("#choice_total_hours");
        spTotal.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 24, 0));
        spTotal.valueProperty().addListener(new TotalHourSpinnerChangeListener(getCalendarView(), getScene().getRoot()));

        Spinner spNight = (Spinner) getScene().lookup("#choice_night_hours");
        spNight.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 24, 0));
        spNight.valueProperty().addListener(new NightHourSpinnerChangeListener(getCalendarView(), getScene().getRoot()));

        getCalendarView().addEventHandlers(new EmployeeCalendarSelectionListener(getScene().getRoot()));

        EmployeeManager<Employee> em = SQLEmployeeManager.getInstance();

        cb.setItems(em.getEmployeesList());

        if(!em.getEmployeesList().isEmpty()) {
            getCalendarView().setCalendar(em.getEmployeesList().get(0).getCalendar());
            cb.setValue(em.getEmployeesList().get(0));
        }

        update();
    }

    public void update() {

        if(getCalendarView().getCalendar() == null) {
            return;
        }

        Label dateLabel = (Label) getScene().getRoot().lookup("#selected_date");

        dateLabel.setText(getCalendarView().getSelectedDate().format(DateTimeFormatter.ofLocalizedDate(FormatStyle.FULL).localizedBy(Locale.FRENCH)));


        int curNightHours = ((WorkDay)getCalendarView().getCalendar().getDay(getCalendarView().getSelectedDate())).getSchedule().getNightHours();
        int curTotalHours = ((WorkDay)getCalendarView().getCalendar().getDay(getCalendarView().getSelectedDate())).getSchedule().getTotalHours();

        Spinner spTotal = (Spinner) getScene().getRoot().lookup("#choice_total_hours");
        spTotal.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(curNightHours,24,curTotalHours));

        Spinner spNight = (Spinner) getScene().getRoot().lookup("#choice_night_hours");
        spNight.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, curTotalHours, curNightHours));

        /*Label nbHoursWeekLabel = (Label) getScene().getRoot().lookup("#week_hours_label");
        nbHoursWeekLabel.setText("Semaine total : " + ((WorkDay) getCalendarView().getCalendar().getDay(getCalendarView().getSelectedDate())).getWeek().getNbHours() + " h");*/
    }
}
