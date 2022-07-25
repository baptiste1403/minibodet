package fr.lesaffrefreres.rh.minibodet.view;

import fr.lesaffrefreres.rh.minibodet.controller.PopupExportViewController;
import fr.lesaffrefreres.rh.minibodet.controller.PopupPlanningViewController;
import fr.lesaffrefreres.rh.minibodet.model.Employee;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.util.Locale;
import java.util.ResourceBundle;

/**
 * This class represents the view of the popup export view.
 *
 * @author lesaffrefreres
 * @version 1.0
 * @since 1.0
 */
public class PopupExportView extends VBox {

    private int selectedMonth;
    private int selectedYear;

    /**
     * return the month selected by the user.
     * @return the month selected by the user.
     */
    public int getSelectedMonth() {
        return selectedMonth;
    }

    /**
     * return the year selected by the user.
     * @return the year selected by the user.
     */
    public int getSelectedYear() {
        return selectedYear;
    }

    /**
     * Set the month selected by the user.
     * @param selectedMonth the month selected by the user.
     */
    public void setSelectedMonth(int selectedMonth) {
        this.selectedMonth = selectedMonth;
    }

    /**
     * Set the year selected by the user.
     * @param selectedYear the year selected by the user.
     */
    public void setSelectedYear(int selectedYear) {
        this.selectedYear = selectedYear;
    }

    /**
     * Load the view from the FXML file.
     */
    public PopupExportView() {
        ResourceBundle bundle = ResourceBundle.getBundle("/strings", new Locale("fr", "FR"));
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/popup-export-view.fxml"), bundle);
        fxmlLoader.setRoot(this);

        try {
            fxmlLoader.load();
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }

        PopupExportViewController controller = fxmlLoader.getController();

        controller.setView(this);
    }
}
