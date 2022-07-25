package fr.lesaffrefreres.rh.minibodet.view;

import fr.lesaffrefreres.rh.minibodet.controller.CalendarViewController;
import fr.lesaffrefreres.rh.minibodet.controller.ClickEventListener;
import fr.lesaffrefreres.rh.minibodet.controller.StatusListPickerListener;
import fr.lesaffrefreres.rh.minibodet.model.*;
import fr.lesaffrefreres.rh.minibodet.model.Calendar;
import fr.lesaffrefreres.rh.minibodet.model.event.CalendarEvent;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.event.EventType;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.text.TextAlignment;

import java.io.IOException;
import java.time.LocalDate;
import java.time.Year;
import java.time.format.TextStyle;
import java.time.temporal.TemporalAdjusters;
import java.util.*;

/**
 * Represent a calendar ui component.
 *
 * You must give it a Calnendar to display.
 * You can use it as any other javafx component.
 * You can get the selected dates by calling getSelectedDates().
 *
 * @author lesaffre
 * @version 1.0
 * @since 1.0
 * @see Calendar
 */
public class CalendarView<T extends Calendar<? extends CalendarElement>> extends AnchorPane {

    private Map<LocalDate, Label> labels;
    private TreeSet<LocalDate> selectedDates;

    private static final int KEY_DATE_YEAR = 2016;

    private LocalDate selectedDate;
    private Label selectedDay;
    private Label selectedMonth;
    private int selectedYear;
    private T calendar;

    private StatusListPickerView labeLPicker;

    private Set<EventHandler<CalendarEvent>> listeners;

    private static final EventType<CalendarEvent> CALENDAR_EVENT_TYPE = new EventType<>("CALENDAR_EVENT_TYPE");

    private boolean isDirty;

    private CalendarViewController controller;

    public void invalidate() {
        isDirty = true;
    }

    public boolean isDirty() {
        return isDirty;
    }

    public void addEventHandlers(EventHandler<CalendarEvent> listener) {
        listeners.add(listener);
    }

    public void removeEventHandler(EventHandler<CalendarEvent> listener) {
        listeners.remove(listener);
    }

    private void fireEvent() {
        for(EventHandler<CalendarEvent> cur : listeners) {
            cur.handle(new CalendarEvent(this, null,  CALENDAR_EVENT_TYPE, selectedDates));
        }
    }

    /**
     * set the calendar to display.
     * @param cal the calendar to display.
     * @see Calendar
     */
    public void setCalendar(T cal) {
        calendar = cal;
        update();
        if(calendar == null) {
            return;
        }
        clearSelection();
        addToSelection(LocalDate.of(calendar.getYear(), 1,1));
    }

    /**
     * give the calendar displayed.
     * @return the calendar displayed.
     * @see Calendar
     */
    public T getCalendar() {
        return calendar;
    }

    /**
     * add the specified date to the selection.
     * @param date the date to add to the selection.
     */
    public void addToSelection(LocalDate date) {
        if(calendar == null) {
            return;
        }
        LocalDate index = date.withYear(KEY_DATE_YEAR);
        if(selectedDates.add(date)) {
            Label label = labels.get(index);
            setSelectionClass(date, true,true);
            calendar.getDay(date).setViewOnSelected(label);
            selectedDate = selectedDates.first();
            setSelectedAxis(selectedDate);
            fireEvent();
        }
    }

    private void setSelectionClass(LocalDate ld, boolean selected, boolean checkNeighbours) {
        LocalDate index = ld.withYear(KEY_DATE_YEAR);

        Label current = labels.get(index);
        current.getStyleClass().clear();
        if(isSelected(ld.minusDays(1))) {
            if(isSelected(ld.plusDays(1))) {
                // middle
                if(selected) {
                    current.getStyleClass().add("grid-element-selected-middle");
                } else {
                    current.getStyleClass().add("grid-element");
                }
                if(checkNeighbours) {
                    setSelectionClass(ld.plusDays(1), true, false);
                    setSelectionClass(ld.minusDays(1), true, false);
                }
            } else {
                // after
                if(selected) {
                    current.getStyleClass().add("grid-element-selected-bottom");
                } else {
                    current.getStyleClass().add("grid-element");
                }
                if(checkNeighbours) {
                    setSelectionClass(ld.minusDays(1), true,  false);
                }
            }
        } else if(isSelected(ld.plusDays(1))) {
            // before
            if(selected) {
                current.getStyleClass().add("grid-element-selected-top");
            } else {
                current.getStyleClass().add("grid-element");
            }
            if(checkNeighbours) {
                setSelectionClass(ld.plusDays(1), true, false);
            }
        } else {
            // alone
            if(selected) {
                current.getStyleClass().add("grid-element-selected-alone");
            } else {
                current.getStyleClass().add("grid-element");
            }
        }
    }

    /**
     * clear the selection and add the specified date to the selection.
     * @param date the date to add to the selection.
     */
    public void setSelected(LocalDate date) {
        if(calendar == null) {
            return;
        }
        clearSelection();
        addToSelection(date);
    }

    public void selectNext() {
        if(calendar == null) {
            return;
        }

        LocalDate next = getSelectedDate().plusDays(1);

        if(next.getYear() != selectedYear) {
            setSelectedYear(selectedYear+1);
        }

        setSelected(next);
    }

    public void selectPrevious() {
        if(calendar == null) {
            return;
        }

        LocalDate previous = getSelectedDate().minusDays(1);

        if(previous.getYear() != selectedYear) {
            setSelectedYear(selectedYear-1);
        }

        setSelected(previous);
    }

    /**
     * remove the specified date from the selection.
     * @param date the date to remove from the selection.
     */
    public void removeFromSelection(LocalDate date) {
        if(calendar == null) {
            return;
        }
        LocalDate index = date.withYear(KEY_DATE_YEAR);
        if(selectedDates.size() <= 1) {
            return;
        }
        if(selectedDates.remove(date)) {
            Label label = labels.get(index);
            setSelectionClass(date, false,true);
            calendar.getDay(date).setView(label);
            selectedDate = selectedDates.first();
            setSelectedAxis(selectedDate);
            fireEvent();
        }
    }

    /**
     * check if the specified date is selected.
     * @param ld the date to check.
     * @return true if the date is selected.
     */
    public boolean isSelected(LocalDate ld) {
        return selectedDates.contains(ld);
    }

    private void clearSelection() {;
        for(LocalDate cur : selectedDates) {
            Label label = labels.get(cur.withYear(KEY_DATE_YEAR));
            label.getStyleClass().clear();
            calendar.getDay(cur).setView(label);
            label.getStyleClass().add("grid-element");
        }
        selectedDates.clear();
        selectedDate = null;
    }

    private void setSelectedAxis(LocalDate date) {
        LocalDate index = date.withYear(KEY_DATE_YEAR);
        int row = GridPane.getRowIndex(labels.get(index));
        int col = GridPane.getColumnIndex(labels.get(index));
        Label lDay = (Label)this.lookup("#day_" + (row+1));
        lDay.getStyleClass().add("grid-title-selected");

        if(selectedDay != null) {
            selectedDay.getStyleClass().remove("grid-title-selected");
        }

        selectedDay = lDay;

        Label lMonth = (Label)this.lookup("#month_" + (col+1));
        lMonth.getStyleClass().add("grid-title-selected");

        if(selectedMonth != null) {
            selectedMonth.getStyleClass().remove("grid-title-selected");
        }

        selectedMonth = lMonth;
    }

    /**
     * set the label list provider to the {@link StatusListPickerView}
     * @param mip the label list provider to set.
     */
    public void setStatusListProvider(StatusListPickerMenuItemProvider mip) {
        labeLPicker.setProvider(mip);
    }

    /**
     * the default constructor.
     * load the fxml file and initialize the ui with default values.
     * You must call setCalendar() to set the calendar to display. and call update() to update the view.
     */
    public CalendarView() {
        isDirty = false;
        listeners = new HashSet<>();
        labeLPicker = new StatusListPickerView();

        selectedYear = LocalDate.now().getYear();
        labels = new TreeMap<>();
        selectedDates = new TreeSet<>();

        labeLPicker.setOnAction(new StatusListPickerListener(this));

        ResourceBundle bundle = ResourceBundle.getBundle("/strings", new Locale("fr", "FR"));
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(
                "/fxml/calendar-view.fxml"), bundle);
        fxmlLoader.setRoot(this);

        try {
            fxmlLoader.load();
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }

        controller = fxmlLoader.getController();
        controller.setCalendar(this);

        List<LocalDate> initList = LocalDate.of(KEY_DATE_YEAR, 1, 1).datesUntil(LocalDate.of(KEY_DATE_YEAR+1, 1, 1)).toList();

        GridPane gp = (GridPane)this.lookup("#grid_content");

        for(int i = 0; i < 12; i++) {
            for(int j = 0; j < 31; j++) {
                Label cur = new Label();
                gp.add(cur, i, j);
                cur.setId(i + "_" + j);
                cur.setMaxWidth(Double.MAX_VALUE);
                cur.setMaxHeight(Double.MAX_VALUE);
            }
        }


        for(LocalDate date : initList) {
            Label cur = (Label) gp.lookup("#" + (date.getMonthValue()-1) + "_" + (date.getDayOfMonth()-1));
            labels.put(date, cur);
            cur.setOnMouseClicked(controller::onMouseClicked);
            cur.setAlignment(Pos.CENTER_LEFT);
            cur.setTextAlignment(TextAlignment.LEFT);
            cur.setTextFill(Color.BLACK);
        }
        reset();
    }

    private void reset() {
        GridPane gp = (GridPane)this.lookup("#grid_content");
        gp.addEventHandler(MouseEvent.MOUSE_CLICKED, new ClickEventListener(labeLPicker, gp));
        List<LocalDate> resetList = LocalDate.of(KEY_DATE_YEAR, 1, 1).datesUntil(LocalDate.of(KEY_DATE_YEAR+1, 1, 1)).toList();

        for(LocalDate date : resetList) {
            Label cur = labels.get(date);
            cur.setText("");
            cur.setBackground(new Background(new BackgroundFill(Color.WHITE, null, null)));
            cur.getStyleClass().clear();
        }
    }

    /**
     * return the current year of the calendar.
     * @return the current year of the calendar.
     */
    public int getSelectedYear() {
        return selectedYear;
    }

    /**
     * set the current year of the calendar.
     * it will cause the calendar to be updated.
     * @param y the year to set.
     */
    public void setSelectedYear(int y) {
        if(calendar == null) {
            return;
        }
        selectedYear = y;
        calendar.setYear(y);

        clearSelection();
        addToSelection(LocalDate.of(getSelectedYear(), 1, 1));
        update();
        Label yearLabel = (Label)lookup("#yearLabel");
        yearLabel.setText("" + getSelectedYear());
    }

    /**
     * update the calendar used as model and
     * update the view
     */
    public void fullUpdate() {
        if(calendar instanceof SQLCalendar) {
            ((SQLCalendar)calendar).updateBuffer();
        } else if(calendar instanceof  SQLWorkCalendar) {
            ((SQLWorkCalendar)calendar).updateBuffer();
        }
        update();
    }

    /**
     * update the view.
     * call this method when the calendar is changed.
     * does nothing if the calendar to display is not set.
     * @see Calendar
     */
    public void update() {

        GridPane gp = (GridPane)this.lookup("#grid_content");

        if(calendar == null) {
            gp.setDisable(true);
            reset();
            return;
        }
        gp.setDisable(false);

        for(int i = 0; i < 12; i++) {
            for(int j = 0; j < 31; j++) {
                Label cur = (Label)gp.lookup("#" + i + "_" + j);
                if(j < Year.of(calendar.getYear()).atMonth(i+1).lengthOfMonth()) {
                    LocalDate date = LocalDate.of(calendar.getYear(), i + 1, j + 1);
                    updateLabel(date, cur);
                } else {
                    cur.setDisable(true);
                    cur.setBackground(new Background(new BackgroundFill(Color.WHITE, null, null)));
                }
            }
        }
        isDirty = false;
    }

    /**
     * update the label of the given date.
     * @param ld the date to update.
     */
    public void updateAtDate(LocalDate ld) {
        if(ld.getYear() != calendar.getYear()) {
            return;
        }
        Label lab = labels.get(ld.withYear(KEY_DATE_YEAR));
        if(lab == null) {
            return;
        }
        updateLabel(ld, lab);
    }

    private void updateLabel(LocalDate ld, Label lab) {
        lab.setText(" " + ld.getDayOfWeek().getDisplayName(TextStyle.NARROW, Locale.FRANCE) + " " + calendar.getDay(ld).toString());
        if(!lab.getStyleClass().contains("grid-element")) {
            lab.getStyleClass().add("grid-element");
        }
        if(selectedDates.contains(ld)) {
            calendar.getDay(ld).setViewOnSelected(lab);
        } else {
            calendar.getDay(ld).setView(lab);
        }

        lab.getStyleClass().add("grid-element");
    }

    private LocalDate getDateFromLabel(Label label) {
        int row = GridPane.getRowIndex(label);
        int col = GridPane.getColumnIndex(label);

        return LocalDate.of(getSelectedYear(), col + 1, row + 1);
    }

    /**
     * return the selected dates.
     * @return the selected dates.
     */
    public Set<LocalDate> getSelectedDates() {
        return selectedDates;
    }

    /**
     * return the first date in the selection. (the lowest date)
     * @return the first date in the selection.
     */
    public LocalDate getSelectedDate() {
        return selectedDate;
    }

    /**
     * called when the view is fully loaded
     * @param observable
     *            The {@code ObservableValue} which value changed
     * @param oldValue
     *            The old value
     * @param newValue
     *            The new value
     */
}