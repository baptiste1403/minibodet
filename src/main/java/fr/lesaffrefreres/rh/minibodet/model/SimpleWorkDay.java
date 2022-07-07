package fr.lesaffrefreres.rh.minibodet.model;

import java.time.LocalDate;
import java.util.Objects;

public class SimpleWorkDay extends SimpleDay implements WorkDay {

    private SimpleWorkSchedule schedule;

    private final SimpleWorkWeek week;

    private String comment;

    public SimpleWorkDay(LocalDate ld, long lid, SimpleWorkWeek w) {
        super(ld, lid);
        Objects.requireNonNull(w);
        schedule = new SimpleWorkSchedule();
        comment = "";
        week = w;
    }

    public SimpleWorkSchedule getSchedule() {
        return schedule;
    }

    public String getComment() {
        return comment;
    }

    public SimpleWorkWeek getWeek() {
        return week;
    }

    public void setComment(String com) {
        Objects.requireNonNull(com);
        comment = com;
    }


}
