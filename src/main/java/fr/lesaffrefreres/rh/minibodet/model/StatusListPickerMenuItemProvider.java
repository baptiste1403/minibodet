package fr.lesaffrefreres.rh.minibodet.model;

import fr.lesaffrefreres.rh.minibodet.view.CalendarView;
import javafx.scene.control.MenuItem;

import java.util.List;

/**
 * This Interface represent a provider of MenuItem of label for a CalendarView.
 * It is used to sepcified the list of available label for a specific CalendarView. and can be override by the calendar contained in the CalendarView.
 *
 * @author lesaffrefreres
 * @version 1.0
 * @since 1.0
 *
 * @see CalendarView
 * @see DayLabel
 */
public interface StatusListPickerMenuItemProvider {

    /**
     * This method is used to get the list of available label as MenuItem for a specific CalendarView.
     * @return the list of available label as MenuItem for a specific CalendarView.
     */
    public List<MenuItem> getMenuItemList();
}
