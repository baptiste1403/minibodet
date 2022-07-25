package fr.lesaffrefreres.rh.minibodet.controller;

import fr.lesaffrefreres.rh.minibodet.model.DayLabel;
import fr.lesaffrefreres.rh.minibodet.model.SQLEmployeeManager;
import fr.lesaffrefreres.rh.minibodet.model.XLSXManager;
import fr.lesaffrefreres.rh.minibodet.view.ImportExportView;
import fr.lesaffrefreres.rh.minibodet.view.PopupExportView;
import fr.lesaffrefreres.rh.minibodet.view.PopupPlanningView;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.Window;
import org.apache.ibatis.annotations.Property;

import java.io.File;
import java.nio.file.FileSystems;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.util.Map;

/**
 * This class is used as the controller of the {@link ImportExportView} class.
 *
 * @author lesaffrefreres
 * @version 1.0
 * @since 1.0
 *
 * @see ImportExportView
 */
public class ImportExportViewController {

    /**
     * This method is called when the user clicks on the "Import" button.
     * it opens a file chooser and imports the data from the selected file by calling the {@link XLSXManager#loadFile(File)} class.
     * @param event the event that triggered the method.
     */
    @FXML
    private void onImportButtonClicked(ActionEvent event) {
        FileChooser fc = new FileChooser();
        FileChooser.ExtensionFilter filter = new FileChooser.ExtensionFilter("Fichiers Excel", "*.xlsx");
        fc.getExtensionFilters().add(filter);
        fc.setSelectedExtensionFilter(filter);

        Stage stage = (Stage) ((Button)event.getSource()).getScene().getWindow();
        stage.setTitle("Importer un fichier Excel");

        File f = fc.showOpenDialog(stage);

        if(f != null) {
            XLSXManager.loadFile(f);
        }
    }

    /**
     * This method is called when the user clicks on the "Export" button.
     * it opens a file chooser and exports the data to the selected file by calling the {@link XLSXManager#exportFile(File, int, int)} class.
     * @param event the event that triggered the method
     */
    @FXML
    private void onExportButtonClicked(ActionEvent event) {
        FileChooser fc = new FileChooser();
        FileChooser.ExtensionFilter filter = new FileChooser.ExtensionFilter("Fichiers Excel", "*.xlsx");
        fc.getExtensionFilters().add(filter);
        fc.setSelectedExtensionFilter(filter);

        Stage stage = (Stage) ((Button)event.getSource()).getScene().getWindow();

        Stage popup = new Stage();
        popup.initModality(Modality.WINDOW_MODAL);

        PopupExportView pev = new PopupExportView();

        Scene scene = new Scene(pev);
        popup.setScene(scene);

        popup.setTitle("Exporter un mois");

        popup.showAndWait();

        DecimalFormat df = new DecimalFormat("00");

        fc.setInitialFileName("FERME POINTAGES TR PAIE " + df.format(pev.getSelectedMonth()) + " " + pev.getSelectedYear());

        File file = fc.showSaveDialog(stage);

        if(file != null) {
            XLSXManager.exportFile(file, pev.getSelectedMonth(), pev.getSelectedYear());
        }
    }
}
