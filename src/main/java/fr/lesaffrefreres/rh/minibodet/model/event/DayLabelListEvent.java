package fr.lesaffrefreres.rh.minibodet.model.event;

import fr.lesaffrefreres.rh.minibodet.model.DayLabel;
import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.event.EventTarget;
import javafx.event.EventType;

import java.util.Collections;
import java.util.List;

public class DayLabelListEvent extends Event {

    List<DayLabel> dayLabelList;

    public DayLabelListEvent(EventType<? extends Event> eventType, List<DayLabel> list) {
        super(eventType);
        dayLabelList = list;
    }

    public DayLabelListEvent(Object source, EventTarget target, EventType<? extends Event> eventType, List<DayLabel> list) {
        super(source, target, eventType);
        dayLabelList = list;
    }

    public List<DayLabel> getLabelList() {
        return Collections.unmodifiableList(dayLabelList);
    }
}
