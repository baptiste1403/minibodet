package fr.lesaffrefreres.rh.minibodet.controller;

import fr.lesaffrefreres.rh.minibodet.model.WorkDay;
import fr.lesaffrefreres.rh.minibodet.view.CalendarView;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;

public class TotalHourSpinnerChangeListener implements ChangeListener<Integer> {

    private CalendarView calendar;

    private Parent root;

    public TotalHourSpinnerChangeListener(CalendarView cv, Parent prt) {
        calendar = cv;
        root = prt;
    }

    @Override
    public void changed(ObservableValue<? extends Integer> observable, Integer oldValue, Integer newValue) {
        ((WorkDay)calendar.getCalendar().getDay(calendar.getSelectedDate())).getSchedule().setTotalHours(newValue);

        Spinner spNight = (Spinner) root.lookup("#choice_night_hours");
        int curNightHours = (Integer)spNight.getValueFactory().getValue();
        spNight.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, newValue, curNightHours));

        /*Label nbHoursWeekLabel = (Label) root.lookup("#week_hours_label");
        nbHoursWeekLabel.setText("Semaine total : " + ((WorkDay) calendar.getCalendar().getDay(calendar.getSelectedDate())).getWeek().getNbHours() + " h");*/
    }
}
