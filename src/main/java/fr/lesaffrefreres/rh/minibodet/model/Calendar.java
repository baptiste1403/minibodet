package fr.lesaffrefreres.rh.minibodet.model;

import java.time.LocalDate;

public interface Calendar<T extends Day> {
    public T getDay(LocalDate date);

    public int getYear();
}
