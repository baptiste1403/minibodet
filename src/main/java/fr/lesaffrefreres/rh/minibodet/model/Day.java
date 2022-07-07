package fr.lesaffrefreres.rh.minibodet.model;

import java.time.LocalDate;

public interface Day {

    public long getLabelId();

    public LocalDate getDate();

    public void setLabelId(long id);
}
