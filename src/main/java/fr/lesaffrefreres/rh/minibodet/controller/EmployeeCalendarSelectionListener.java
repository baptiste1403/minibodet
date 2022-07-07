package fr.lesaffrefreres.rh.minibodet.controller;

import fr.lesaffrefreres.rh.minibodet.model.Calendar;
import fr.lesaffrefreres.rh.minibodet.model.WorkDay;
import fr.lesaffrefreres.rh.minibodet.model.event.CalendarEvent;
import fr.lesaffrefreres.rh.minibodet.view.CalendarView;
import javafx.event.EventHandler;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.Locale;

public class EmployeeCalendarSelectionListener implements EventHandler<CalendarEvent> {

    private Parent root;

    public EmployeeCalendarSelectionListener(Parent prt) {
        root = prt;
    }

    @Override
    public void handle(CalendarEvent event) {
        Label dateLabel = (Label) root.lookup("#selected_date");
        CalendarView cv= (CalendarView) root.lookup("#calendar_employee");
        dateLabel.setText(cv.getSelectedDate().format(DateTimeFormatter.ofLocalizedDate(FormatStyle.FULL).localizedBy(Locale.FRENCH)));

        int curNightHours = ((WorkDay)cv.getCalendar().getDay(cv.getSelectedDate())).getSchedule().getNightHours();
        int curTotalHours = ((WorkDay)cv.getCalendar().getDay(cv.getSelectedDate())).getSchedule().getTotalHours();

        Spinner spTotal = (Spinner) root.lookup("#choice_total_hours");
        spTotal.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(curNightHours,24,curTotalHours));

        Spinner spNight = (Spinner) root.lookup("#choice_night_hours");
        spNight.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, curTotalHours, curNightHours));

        /*Label nbHoursWeekLabel = (Label) root.lookup("#week_hours_label");
        nbHoursWeekLabel.setText("Semaine total : " + ((WorkDay) cv.getCalendar().getDay(cv.getSelectedDate())).getWeek().getNbHours() + " h");*/

    }
}
