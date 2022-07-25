package fr.lesaffrefreres.rh.minibodet.model;

import fr.lesaffrefreres.rh.minibodet.view.CalendarView;
import javafx.scene.control.MenuItem;

import java.util.ArrayList;
import java.util.List;

/**
 * This class represent the base implementation of the {@link StatusListPickerMenuItemProvider} interface.
 * Its used by default by the {@link CalendarView} class.
 *
 * @author lesaffrefreres
 * @version 1.0
 * @since 1.0
 *
 * @see StatusListPickerMenuItemProvider
 * @see CalendarView
 */
public class StatusListPickerMenuItemProviderBase implements StatusListPickerMenuItemProvider{

    /**
     * Returns all the labels as a MenuItems list
     * @return a list of MenuItems
     */
    @Override
    public List<MenuItem> getMenuItemList() {
        DayLabelManager dlm = SQLDayLabelManager.getInstance();

        List<MenuItem> res = new ArrayList<>();
        for(DayLabel cur : dlm.getAllDayLabels()) {
            res.add(new MenuItem(cur.getText()));
        }
        return res;
    }
}
