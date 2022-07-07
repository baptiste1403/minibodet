package fr.lesaffrefreres.rh.minibodet.controller;

import fr.lesaffrefreres.rh.minibodet.model.WorkDay;
import fr.lesaffrefreres.rh.minibodet.view.CalendarView;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;

public class NightHourSpinnerChangeListener implements ChangeListener<Integer> {

    private CalendarView calendar;

    private Parent root;

    public NightHourSpinnerChangeListener(CalendarView cv, Parent prt) {
        calendar = cv;
        root = prt;
    }

    @Override
    public void changed(ObservableValue<? extends Integer> observable, Integer oldValue, Integer newValue) {
        ((WorkDay)calendar.getCalendar().getDay(calendar.getSelectedDate())).getSchedule().setNightHours(newValue);

        Spinner spTotal = (Spinner) root.lookup("#choice_total_hours");
        int curTotalHours = (Integer)spTotal.getValueFactory().getValue();
        spTotal.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(newValue, 24, curTotalHours));

        /*Label nbHoursWeekLabel = (Label) root.lookup("#week_hours_label");
        nbHoursWeekLabel.setText("Semaine total : " + ((WorkDay) calendar.getCalendar().getDay(calendar.getSelectedDate())).getWeek().getNbHours() + " h");*/
    }
}
