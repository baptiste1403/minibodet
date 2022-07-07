package fr.lesaffrefreres.rh.minibodet.controller;

import fr.lesaffrefreres.rh.minibodet.model.Employee;
import fr.lesaffrefreres.rh.minibodet.model.WorkDay;
import fr.lesaffrefreres.rh.minibodet.model.event.CalendarEvent;
import fr.lesaffrefreres.rh.minibodet.view.CalendarView;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.Locale;
import java.util.Objects;

public class EmployeeCalendarViewController implements EventHandler<CalendarEvent> {

    @FXML
    private Label selectedDate;
    @FXML
    private Spinner<Integer> choiceTotalHours;
    @FXML
    private Spinner<Integer> choiceNightHours;
    @FXML
    private Label weekHoursLabel;
    @FXML
    private ComboBox<Employee> employeeChoicebox;

    private CalendarView calendar;

    public void setCalendarView(CalendarView cv) {
        calendar = Objects.requireNonNull(cv);
    }

    @FXML
    public void onEmployeeListChange(ActionEvent event) {
        if(calendar == null) {
            return;
        }
        calendar.setCalendar(employeeChoicebox.getValue().getCalendar());
        update();
    }

    public void onTotalHoursChange(ObservableValue<? extends Integer> observable, Integer oldValue, Integer newValue) {
        if(calendar == null) {
            return;
        }
        ((WorkDay)calendar.getCalendar().getDay(calendar.getSelectedDate())).getSchedule().setTotalHours(newValue);

        int curNightHours = choiceNightHours.getValueFactory().getValue();
        choiceNightHours.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, newValue, curNightHours));
    }

    public void onNightHoursChange(ObservableValue<? extends Integer> observable, Integer oldValue, Integer newValue) {
        if(calendar == null) {
            return;
        }
        ((WorkDay)calendar.getCalendar().getDay(calendar.getSelectedDate())).getSchedule().setNightHours(newValue);

        int curTotalHours = (Integer)choiceTotalHours.getValueFactory().getValue();
        choiceTotalHours.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(newValue, 24, curTotalHours));
    }

    public void update() {
        if(calendar == null) {
            return;
        }
        selectedDate.setText(calendar.getSelectedDate().format(DateTimeFormatter.ofLocalizedDate(FormatStyle.FULL).localizedBy(Locale.getDefault())));

        int curNightHours = ((WorkDay)calendar.getCalendar().getDay(calendar.getSelectedDate())).getSchedule().getNightHours();
        int curTotalHours = ((WorkDay)calendar.getCalendar().getDay(calendar.getSelectedDate())).getSchedule().getTotalHours();

        choiceTotalHours.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(curNightHours,24,curTotalHours));
        choiceNightHours.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, curTotalHours, curNightHours));
    }

    @Override
    public void handle(CalendarEvent event) {
        update();
    }
}
