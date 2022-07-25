package fr.lesaffrefreres.rh.minibodet.controller;

import fr.lesaffrefreres.rh.minibodet.model.Calendar;
import fr.lesaffrefreres.rh.minibodet.model.CalendarElement;
import fr.lesaffrefreres.rh.minibodet.model.SQLDayLabelManager;
import fr.lesaffrefreres.rh.minibodet.view.CalendarView;
import fr.lesaffrefreres.rh.minibodet.view.StatusListPickerView;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;

import java.time.LocalDate;


/**
 * This class is used as a listener for the {@link StatusListPickerView} of the {@link CalendarView}.
 *
 * @author lesaffrefreres
 * @version 1.0
 * @since 1.0
 *
 * @see StatusListPickerView
 * @see CalendarView
 */
public class StatusListPickerListener implements EventHandler<ActionEvent> {

    private CalendarView<Calendar<CalendarElement>> calendar;

    /**
     * Set the calendar view of the picker view linked to this listener
     * it must be called just after the view is created.
     * @param cv the calendar view of the picker view linked to this listener
     */
    public StatusListPickerListener(CalendarView cv) {
        calendar = cv;
    }

    /**
     * This method is called when the user select a menu item in the context menu of the {@link StatusListPickerView}.
     * it change the label of the selected days and update the calendar.
     * @param event the event which occurred
     */
    @Override
    public void handle(ActionEvent event) {
        for(LocalDate cur : calendar.getSelectedDates()) {
            calendar.getCalendar().getDay(cur).setLabelId(SQLDayLabelManager.getInstance().getDayLabelIdByName(((MenuItem)event.getTarget()).getText()));
            calendar.updateAtDate(cur);
        }

        ContextMenu ct = (ContextMenu) event.getSource();
        ct.hide();
    }
}
