package fr.lesaffrefreres.rh.minibodet.view;

import fr.lesaffrefreres.rh.minibodet.model.DayLabel;
import fr.lesaffrefreres.rh.minibodet.model.SQLDayLabelManager;
import fr.lesaffrefreres.rh.minibodet.model.SimpleDayLabelManager;
import fr.lesaffrefreres.rh.minibodet.model.event.DayLabelListEvent;
import javafx.collections.ListChangeListener;
import javafx.event.EventHandler;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;

import java.util.List;

public class StatusListPickerView extends ContextMenu implements ListChangeListener<DayLabel> {

    public StatusListPickerView() {
        getItems().clear();
        List<DayLabel> labels = SQLDayLabelManager.getInstance().getAllDayLabels();
        for(DayLabel cur : labels) {
            MenuItem mi = new MenuItem(cur.getText());
            getItems().add(mi);
        }
    }

    @Override
    public void onChanged(Change<? extends DayLabel> c) {
        getItems().clear();
        for(DayLabel cur : c.getList()) {
            MenuItem mi = new MenuItem(cur.getText());
            getItems().add(mi);
        }
    }
}
