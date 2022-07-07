package fr.lesaffrefreres.rh.minibodet.model.event;

import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.event.EventTarget;
import javafx.event.EventType;

import java.time.LocalDate;

public class CalendarEvent extends Event {

    private LocalDate date;

    public CalendarEvent(EventType<? extends Event> eventType, LocalDate ld) {
        super(eventType);
        date = ld;
    }

    public CalendarEvent(Object source, EventTarget target, EventType<? extends Event> eventType, LocalDate ld) {
        super(source, target, eventType);
        date = ld;
    }
}
