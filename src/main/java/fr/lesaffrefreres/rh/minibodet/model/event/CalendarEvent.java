package fr.lesaffrefreres.rh.minibodet.model.event;

import fr.lesaffrefreres.rh.minibodet.view.CalendarView;
import javafx.event.Event;
import javafx.event.EventTarget;
import javafx.event.EventType;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

/**
 * This class is used as en event for the {@link CalendarView} class.
 *
 * @author lesaffrefreres
 * @version 1.0
 * @since 1.0
 *
 * @see CalendarView
 */
public class CalendarEvent extends Event {

    private Set<LocalDate> dates;

    /**
     * Creates an event representing the selected dates in the calendar
     * @param source the source of the event
     * @param target the target of the event
     * @param eventType the type of the event
     * @param lds the list of selected dates
     */
    public CalendarEvent(Object source, EventTarget target, EventType<? extends Event> eventType, Set<LocalDate> lds) {
        super(source, target, eventType);
        dates = lds;
    }

    /**
     * get the selected dates associated to the event
     * @return the selected dates
     */
    public Set<LocalDate> getSelectedDates() {
        return dates;
    }
}
