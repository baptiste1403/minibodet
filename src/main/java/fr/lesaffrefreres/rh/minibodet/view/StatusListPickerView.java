package fr.lesaffrefreres.rh.minibodet.view;

import fr.lesaffrefreres.rh.minibodet.model.DayLabel;
import fr.lesaffrefreres.rh.minibodet.model.SQLDayLabelManager;
import javafx.collections.ListChangeListener;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;

import java.util.List;

public class StatusListPickerView extends ContextMenu {

    public StatusListPickerView() {
        getItems().clear();
        List<DayLabel> labels = SQLDayLabelManager.getInstance().getAllDayLabels();
        for(DayLabel cur : labels) {
            MenuItem mi = new MenuItem(cur.getText());
            getItems().add(mi);
        }
    }

    @Override
    protected void show() {
        getItems().clear();
        List<DayLabel> labels = SQLDayLabelManager.getInstance().getAllDayLabels();
        for(DayLabel cur : labels) {
            MenuItem mi = new MenuItem(cur.getText());
            getItems().add(mi);
        }
        super.show();
    }
}
