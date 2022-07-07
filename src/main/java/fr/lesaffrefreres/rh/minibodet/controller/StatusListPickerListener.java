package fr.lesaffrefreres.rh.minibodet.controller;

import fr.lesaffrefreres.rh.minibodet.model.SQLDayLabelManager;
import fr.lesaffrefreres.rh.minibodet.view.CalendarView;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;

public class StatusListPickerListener implements EventHandler<ActionEvent> {

    private CalendarView calendar;

    public StatusListPickerListener(CalendarView cv) {
        calendar = cv;
    }

    @Override
    public void handle(ActionEvent event) {
        calendar.getCalendar().getDay(calendar.getSelectedDate()).setLabelId(SQLDayLabelManager.getInstance().getDayLabelIdByName(((MenuItem)event.getTarget()).getText()));
        calendar.updateAtDate(calendar.getSelectedDate());
        ContextMenu ct = (ContextMenu) event.getSource();
        ct.hide();
    }
}
