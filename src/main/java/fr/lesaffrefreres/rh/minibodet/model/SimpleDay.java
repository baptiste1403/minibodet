package fr.lesaffrefreres.rh.minibodet.model;

import java.time.LocalDate;
import java.util.Objects;

public class SimpleDay implements Day{
    private long labelId;
    private LocalDate date;

    public SimpleDay(LocalDate ld, long lid) {
        Objects.requireNonNull(ld);
        date = ld;
        labelId = lid;
    }

    public long getLabelId() {
        DayLabelManager dlm = SQLDayLabelManager.getInstance();
        if(dlm.labelExist(labelId)) {
            return labelId;
        }
        return dlm.getUndefinedDayLabelId();
    }

    public LocalDate getDate() {
        return date;
    }

    public void setLabelId(long id) {
        if(!SimpleDayLabelManager.getInstance().labelExist(id)) {
            throw new IllegalArgumentException("label with id " + id + " do not exist");
        }
        labelId = id;
    }
}
