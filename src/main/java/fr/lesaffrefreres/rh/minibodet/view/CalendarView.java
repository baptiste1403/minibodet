package fr.lesaffrefreres.rh.minibodet.view;

import fr.lesaffrefreres.rh.minibodet.controller.ClickEventListener;
import fr.lesaffrefreres.rh.minibodet.controller.StatusListPickerListener;
import fr.lesaffrefreres.rh.minibodet.model.*;
import fr.lesaffrefreres.rh.minibodet.model.event.CalendarEvent;
import javafx.event.EventHandler;
import javafx.event.EventType;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
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
import java.util.HashSet;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.Set;

/**
 * Represent a calendar ui component.
 *
 * You must give it a GlobalCalendar to display.
 * You can use it as any other javafx component.
 * You can get the selected date by calling getSelectedDate().
 *
 * @author lesaffre
 * @version 1.0
 * @since 1.0
 * @see Calendar
 */
public class CalendarView extends AnchorPane {

    private Label selected;
    private LocalDate selectedDate;
    private Label selectedDay;
    private Label selectedMonth;
    private Calendar calendar;

    private StatusListPickerView labeLPicker;

    private Set<EventHandler<CalendarEvent>> listeners;

    private static final EventType<CalendarEvent> CALENDAR_EVENT_TYPE = new EventType<>("CALENDAR_EVENT_TYPE");

    private boolean isDirty;

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
            cur.handle(new CalendarEvent(this, null,  CALENDAR_EVENT_TYPE, selectedDate));
        }
    }

    /**
     * set the calendar to display.
     * @param cal the calendar to display.
     * @see Calendar
     */
    public void setCalendar(Calendar cal) {
        calendar = cal;
        update();
        setSelectedDate(LocalDate.of(calendar.getYear(), 1,1));
    }

    /**
     * give the calendar displayed.
     * @return the calendar displayed.
     * @see Calendar
     */
    public Calendar getCalendar() {
        return calendar;
    }

    public void setSelectedDate(LocalDate ld) {
        if(calendar == null || ld.getYear() != calendar.getYear()) {
            throw new IllegalArgumentException("a calendar must be set and the given date must be in the same year than the actual calendar");
        }

        selectedDate = ld;

        DayLabelManager dlm = SQLDayLabelManager.getInstance();

        if(selected != null) {
            selected.getStyleClass().clear();
            selected.getStyleClass().add("grid-element");
            selected.setBackground(new Background(new BackgroundFill(dlm.getDayLabelById((int) calendar.getDay(selectedDate).getLabelId()).getColor(), null, null)));
            selected.setTextFill(Color.BLACK);
        }

        selected = (Label)lookup("#" + (ld.getMonth().getValue()-1) + "_" + (ld.getDayOfMonth()-1));

        selected.setBackground(new Background(new BackgroundFill(dlm.getDayLabelById((int) calendar.getDay(selectedDate).getLabelId()).getColor().darker(), null, null)));
        selected.setTextFill(Color.BLACK);

        selected.getStyleClass().clear();
        selected.getStyleClass().add("grid-element-selected");

        GridPane gp = (GridPane)selected.getParent();
        int row = GridPane.getRowIndex(selected);
        int col = GridPane.getColumnIndex(selected);

        selectedDate = LocalDate.of(calendar.getYear(), col + 1, row + 1);

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
        fireEvent();
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

        labeLPicker.setOnAction(new StatusListPickerListener(this));

        ResourceBundle bundle = ResourceBundle.getBundle("/strings", new Locale("fr", "FR"));
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(
                "/calendar-view.fxml"), bundle);
        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);

        try {
            fxmlLoader.load();
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }

        GridPane gp = (GridPane)this.lookup("#grid_content");

        gp.addEventHandler(MouseEvent.MOUSE_CLICKED, new ClickEventListener(labeLPicker, gp));

        DayLabelManager dlm = SQLDayLabelManager.getInstance();

        for(int i = 0; i < 12; i++) {
            for(int j = 0; j < 31; j++) {
                Label cur = new Label();
                cur.setId(i + "_" + j);
                cur.setMaxWidth(Double.MAX_VALUE);
                cur.setMaxHeight(Double.MAX_VALUE);
                gp.add(cur, i, j);
                if(j < Year.of(2022).atMonth(i+1).lengthOfMonth()) {

                    cur.setAlignment(Pos.CENTER_LEFT);
                    cur.setTextAlignment(TextAlignment.LEFT);
                    cur.setTextFill(Color.BLACK);

                    LocalDate date = LocalDate.of(2022, i + 1, j + 1);
                    cur.setText(date.getDayOfWeek().getDisplayName(TextStyle.NARROW, Locale.FRANCE) + " no status");
                    cur.setBackground(new Background(new BackgroundFill(Color.WHITE, null, null)));
                    cur.getStyleClass().add("grid-element");
                    cur.setOnMouseClicked(this::click);
                } else {
                    cur.setDisable(true);
                    cur.setBackground(new Background(new BackgroundFill(Color.WHITE, null, null)));
                }
            }
        }
    }

    /**
     * update the view.
     * call this method when the calendar is changed.
     * does nothing if the calendar to display is not set.
     * @see Calendar
     */
    public void update() {

        if(calendar == null) {
            return;
        }

        GridPane gp = (GridPane)this.lookup("#grid_content");

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

    public void updateAtDate(LocalDate ld) {
        if(ld.getYear() != calendar.getYear()) {
            return;
        }
        Label lab = (Label)lookup("#" + (ld.getMonth().getValue()-1) + "_" + (ld.getDayOfMonth()-1));
        if(lab == null) {
            return;
        }
        updateLabel(ld, lab);
    }

    private void updateLabel(LocalDate ld, Label lab) {
        DayLabelManager dlm = SQLDayLabelManager.getInstance();
        lab.setText("  " + ld.getDayOfWeek().getDisplayName(TextStyle.NARROW, Locale.FRANCE) + " " + dlm.getDayLabelById(calendar.getDay(ld).getLabelId()).getText());
        Color c = dlm.getDayLabelById((int) calendar.getDay(ld).getLabelId()).getColor();
        if(lab == selected) {
            lab.setBackground(new Background(new BackgroundFill(c.darker(), null, null)));
        } else {
            lab.setBackground(new Background(new BackgroundFill(c, null, null)));
        }

        lab.getStyleClass().add("grid-element");
    }

    @FXML
    private void click(MouseEvent me) {

        if(calendar == null) {
            return;
        }

        Label label = (Label)me.getSource();
        if(selected != null) {
            selected.getStyleClass().clear();
            selected.getStyleClass().add("grid-element");
            DayLabelManager dlm = SQLDayLabelManager.getInstance();
            selected.setBackground(new Background(new BackgroundFill(dlm.getDayLabelById((int) calendar.getDay(selectedDate).getLabelId()).getColor(), null, null)));
            selected.setTextFill(Color.BLACK);
        }
        label.getStyleClass().clear();
        label.getStyleClass().add("grid-element-selected");

        selected = label;

        Color c = (Color) label.getBackground().getFills().get(0).getFill();

        label.setBackground(new Background(new BackgroundFill(c.darker(), null, null)));
        selected.setTextFill(Color.BLACK);

        GridPane gp = (GridPane)label.getParent();
        int row = GridPane.getRowIndex(label);
        int col = GridPane.getColumnIndex(label);

        selectedDate = LocalDate.of(2022, col + 1, row + 1);

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
        fireEvent();
    }

    /**
     * get the selected date.
     * @return the selected date.
     */
    public LocalDate getSelectedDate() {
        return selectedDate;
    }
}