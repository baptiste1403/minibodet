package fr.lesaffrefreres.rh.minibodet.controller.extractor;

import fr.lesaffrefreres.rh.minibodet.model.DayLabel;
import fr.lesaffrefreres.rh.minibodet.model.SQLDayLabel;
import fr.lesaffrefreres.rh.minibodet.model.SQLDayLabelManager;
import javafx.beans.Observable;
import javafx.util.Callback;

public class DayLabelExtractor implements Callback<DayLabel, Observable[]> {

    @Override
    public Observable[] call(DayLabel param) {
        return new Observable[] {param.textProperty()};
    }
}
