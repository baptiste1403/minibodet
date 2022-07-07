package fr.lesaffrefreres.rh.minibodet.model;

import fr.lesaffrefreres.rh.minibodet.model.event.DayLabelListEvent;
import javafx.event.EventHandler;
import javafx.event.EventType;
import javafx.scene.paint.Color;

import java.util.*;

public class SimpleDayLabelManager implements DayLabelManager{
    private static final class DayLabelWIthId extends DayLabel {

        private int id;

        public DayLabelWIthId(int i, String txt, Color c) {
            super(txt, c);
            id = i;
        }

        public int getId() {
            return id;
        }

        public void setText(String txt) {
            Objects.requireNonNull(txt);
            text = txt;
        }

        public void setColor(Color c) {
            Objects.requireNonNull(c);
            color = c;
        }
    }

    private static SimpleDayLabelManager instance;

    private static Set<EventHandler<DayLabelListEvent>> eventListeners;

    private static final DayLabelWIthId undefined = new DayLabelWIthId(0, "indéfini", Color.LIGHTGREY);

    private static final List<DayLabel> dayLabelList = new ArrayList<>();

    private static final EventType<DayLabelListEvent> DAY_LABEL_LIST_CHANGE = new EventType<>("DAY_LABEL_LIST_CHANGE");

    private static int counter;

    public long getUndefinedDayLabelId() {
        return undefined.getId();
    }

    protected SimpleDayLabelManager() {
        counter = 1;
        dayLabelList.add(undefined);
        eventListeners = new HashSet<>();
    }

    public void addEventHandler(EventHandler<DayLabelListEvent> eventHandler) {
        eventListeners.add(eventHandler);
    }

    public void removeEventHandler(EventHandler<DayLabelListEvent> eventHandler) {
        eventListeners.remove(eventHandler);
    }

    private void fireEvent() {
        for(EventHandler<DayLabelListEvent> cur: eventListeners) {
            DayLabelListEvent dlle = new DayLabelListEvent(this, null, DAY_LABEL_LIST_CHANGE, dayLabelList);
            cur.handle(dlle);
        }
    }

    public static SimpleDayLabelManager getInstance() {
        if(instance == null) {
            instance = new SimpleDayLabelManager();
        }
        return instance;
    }

    public long createDayLabel(String txt, Color c) {
        Objects.requireNonNull(txt);
        Objects.requireNonNull(c);
        if(containsDayLabel(txt)) {
            throw new IllegalArgumentException("label " + txt + " already exist");
        }

        dayLabelList.add(new DayLabelWIthId(counter, txt, c));
        int res = counter;
        counter++;
        fireEvent();
        return res;
    }

    public long getDayLabelIdByName(String txt) {
        Objects.requireNonNull(txt);
        for(DayLabel cur : dayLabelList) {
            if(cur.getText().equals(txt)) {
                return ((DayLabelWIthId)cur).getId();
            }
        }
        throw new IllegalArgumentException("label " + txt + " do not exist");
    }

    public DayLabel getDayLabelById(long id) {
        for(DayLabel cur : dayLabelList) {
            if(((DayLabelWIthId)cur).getId() == id) {
                return cur;
            }
        }
        throw new IllegalArgumentException("label with id " + id + " do not exist");
    }

    public void removeDayLabelById(long id) {
        if(!containsDayLabel(id)) {
            return;
        }
        if(id == getUndefinedDayLabelId()) {
            throw new IllegalArgumentException("cannot remove default undefined label");
        }
        DayLabel dl = getDayLabelById(id);
        dayLabelList.remove(dayLabelList.indexOf(dl));
        fireEvent();
    }

    private boolean containsDayLabel(String txt) {
        Objects.requireNonNull(txt);
        for(DayLabel cur : dayLabelList) {
            if(cur.getText().equals(txt)) {
                return true;
            }
        }
        return false;
    }

    private boolean containsDayLabel(long id) {
        for(DayLabel cur : dayLabelList) {
            if(((DayLabelWIthId)cur).getId() == id) {
                return true;
            }
        }
        return false;
    }
    
    public void setDayLabelText(int id, String txt) {
        Objects.requireNonNull(txt);
        if(!labelExist(id)) {
            throw new IllegalArgumentException("label with id " + " do not exist");
        }

        ((DayLabelWIthId)getDayLabelById(id)).setText(txt);
        fireEvent();
    }

    public void setDayLabelColor(int id, Color c) {
        Objects.requireNonNull(c);
        if(!labelExist(id)) {
            throw new IllegalArgumentException("label with id " + " do not exist");
        }

        ((DayLabelWIthId)getDayLabelById(id)).setColor(c);
        fireEvent();
    }

    public boolean labelExist(long id) {
        return containsDayLabel(id);
    }

    public List<DayLabel> getAllDayLabels() {
        return dayLabelList;
    }
}