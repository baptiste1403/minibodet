package fr.lesaffrefreres.rh.minibodet.controller;

import fr.lesaffrefreres.rh.minibodet.model.Calendar;
import fr.lesaffrefreres.rh.minibodet.model.CalendarElement;
import fr.lesaffrefreres.rh.minibodet.view.CalendarView;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;

import java.time.LocalDate;


/**
 * This class is used as the controller of the calendar view. @see {@link CalendarView}
 *
 * @author lesaffrefreres
 * @version 1.0
 * @since 1.0
 *
 * @see CalendarView
 */
public class CalendarViewController {

    @FXML
    private Label yearLabel;

    private CalendarView<Calendar<CalendarElement>> calendar;

    /**
     * Set the calendar view for this controller, this method must be called as soon as the view is loaded
     * to make sur all the handler and listener will work properly.
     * @param cv the calendar view to set.
     */
    public void setCalendar(CalendarView cv) {
        calendar = cv;
        yearLabel.setText("" + calendar.getSelectedYear());
    }

    /**
     * This method is called when the user click on the button "next" on the top of the view
     * It will change the year of the calendar by +1 and update the view.
     * @param event the event that triggered this method.
     */
    public void onYearNextButtonClick(ActionEvent event) {
        calendar.setSelectedYear(calendar.getSelectedYear() + 1);
        yearLabel.setText("" + calendar.getSelectedYear());
    }

    /**
     * This method is called when the user click on the button "previous" on the top of the view
     * It will change the year of the calendar by -1 and update the view.
     * @param event the event that triggered this method.
     */
    public void onYearBeforeButtonClick(ActionEvent event) {
        calendar.setSelectedYear(calendar.getSelectedYear() - 1);
        yearLabel.setText("" + calendar.getSelectedYear());
    }

    /**
     * This method is called when the user click on the calendar to select a day.
     *
     * if the user click on a day that is not already selected :
     *  - if the control key is hold, the day will be added to the previous selection.
     *    otherwise the selection will be clear and the day will be added to the selection.
     *
     * if the user click on a day that is already selected :
     *  - if the control key is hold, the day will be removed from the selection.
     *    otherwise the selection will be clear and the day will be added to the selection.
     *    !!! if the selection contains only one day, even if the control key is hold, the day will stay selected. !!!
     *
     * @param me the mouse event that triggered this method.
     */
    public void onMouseClicked(MouseEvent me) {
        if(!me.getButton().equals(MouseButton.PRIMARY) &&
        !me.getButton().equals(MouseButton.SECONDARY)) {
            return;
        }
        LocalDate ld = getDateFromLabel((Label) me.getSource());
        if(me.getButton().equals(MouseButton.PRIMARY)) {
            if(me.isControlDown()) {
                if(calendar.isSelected(ld)) {
                    calendar.removeFromSelection(ld);
                } else {
                    calendar.addToSelection(ld);
                }
            } else {
                calendar.setSelected(ld);
            }
        } else if(!calendar.isSelected(ld)) {
            calendar.setSelected(ld);
        }

    }

    private LocalDate getDateFromLabel(Label label) {
        GridPane gp = (GridPane)label.getParent();
        int row = GridPane.getRowIndex(label);
        int col = GridPane.getColumnIndex(label);

        return LocalDate.of(calendar.getSelectedYear(), col + 1, row + 1);
    }
}
