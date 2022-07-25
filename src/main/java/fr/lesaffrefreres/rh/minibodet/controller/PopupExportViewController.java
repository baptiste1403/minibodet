package fr.lesaffrefreres.rh.minibodet.controller;

import fr.lesaffrefreres.rh.minibodet.model.DayLabel;
import fr.lesaffrefreres.rh.minibodet.model.SQLEmployeeManager;
import fr.lesaffrefreres.rh.minibodet.model.XLSXManager;
import fr.lesaffrefreres.rh.minibodet.view.PopupExportView;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.stage.FileChooser;
import javafx.stage.Window;
import javafx.util.converter.IntegerStringConverter;

import java.io.File;
import java.time.LocalDate;
import java.time.Month;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * This class is used as a controller for the {@link PopupExportView} class.
 *
 * @author lesaffrefreres
 * @version 1.0
 * @since 1.0
 *
 * @see PopupExportView
 */
public class PopupExportViewController {

    @FXML
    private TextField yearTextField;
    @FXML
    private ChoiceBox<Month> monthComboBox;

    private PopupExportView view;

    /**
     * This method is used to set the view linked to this controller
     * this method must be called after the view is created.
     * @param view the view linked to this controller
     */
    public void setView(PopupExportView view) {
        this.view = view;
    }

    /**
     * This method is called by the FXML loader when the view is loaded.
     * It initializes the view.
     */
    @FXML
    private void initialize() {
        yearTextField.setTextFormatter(new TextFormatter<>(new IntegerStringConverter()));
        yearTextField.setText(LocalDate.now().getYear() + "");
        monthComboBox.setItems(FXCollections.observableList(List.of(Month.values())));
        monthComboBox.getSelectionModel().select(LocalDate.now().getMonth());
    }

    /**
     * This method is called when the user clicks on the export button.
     * It close the popup to let the {@link ImportExportViewController} export the data.
     * @param event the event that triggered the method
     */
    @FXML
    private void onButtonValidateExport(ActionEvent event) {

        view.setSelectedMonth(monthComboBox.getValue().getValue());
        view.setSelectedYear(Integer.parseInt(yearTextField.getText()));

        Window window = ((Button)event.getSource()).getScene().getWindow();
        window.hide();
    }
}
