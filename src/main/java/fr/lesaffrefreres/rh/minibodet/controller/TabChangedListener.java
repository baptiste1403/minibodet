package fr.lesaffrefreres.rh.minibodet.controller;

import fr.lesaffrefreres.rh.minibodet.view.CalendarView;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;

public class TabChangedListener implements EventHandler<Event> {

    TabPane parent;

    public TabChangedListener(TabPane tp) {
        parent = tp;
    }

    @Override
    public void handle(Event event) {
        Tab tab = (Tab)event.getSource();
        if(tab.isSelected()) {
            if(tab.getId().equals("tab_calendar")) {
                CalendarView cv = (CalendarView)parent.lookup("#calendar");
                cv.update();
            } else if (tab.getId().equals("tab_employee")) {
                CalendarView cv = (CalendarView)parent.lookup("#calendar_employee");
                cv.update();
            }
        }
    }
}
