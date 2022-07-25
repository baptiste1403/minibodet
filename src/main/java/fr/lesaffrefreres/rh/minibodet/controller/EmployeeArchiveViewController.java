package fr.lesaffrefreres.rh.minibodet.controller;

import fr.lesaffrefreres.rh.minibodet.model.Employee;
import fr.lesaffrefreres.rh.minibodet.model.SQLEmployeeManager;
import fr.lesaffrefreres.rh.minibodet.view.EmployeeArchiveView;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;

/**
 * This class is used as the controller for the EmployeeArchiveView @see {@link EmployeeArchiveView}
 *
 * @author lesaffrefreres
 * @version 1.0
 * @since 1.0
 *
 * @see EmployeeArchiveView
 */
public class EmployeeArchiveViewController {
    @FXML
    private ListView<Employee> employeeList;

    /**
     * This method is called by the FXML loader when the view is loaded.
     * It si used to initialize the view.
     */
    @FXML
    private void initialize() {
        SQLEmployeeManager sem = SQLEmployeeManager.getInstance();
        employeeList.setItems(sem.getArchivedEmployeesList());
        employeeList.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        employeeList.getSelectionModel().selectFirst();
    }

    /**
     * This method is called when the user clicks on the "Restore" button.
     * if an employee from the archive is selected, it is restored if an employee with the same name is not already in the database.
     * otherwise, an error message is displayed.
     * @param event the event that triggered the method.
     */
    @FXML
    private void onRestoreButtonCLick(ActionEvent event) {
        if(employeeList.getSelectionModel().isEmpty()) {
            return;
        }
        SQLEmployeeManager sem = SQLEmployeeManager.getInstance();
        if(sem.employeeExist(employeeList.getSelectionModel().getSelectedItem().getFirstName(), employeeList.getSelectionModel().getSelectedItem().getLastName())) {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Un employé portant ce nom existe déjà", ButtonType.OK);
            alert.showAndWait();
            return;
        }
        sem.restoreEmployee(employeeList.getSelectionModel().getSelectedItem());
    }

    @FXML
    private void onDeleteButtonCLick(ActionEvent event) {
        if(employeeList.getSelectionModel().isEmpty()) {
            return;
        }

        Employee emp = employeeList.getSelectionModel().getSelectedItem();

        ButtonType btnDelete = new ButtonType("supprimer");
        ButtonType btnCancel = new ButtonType("annuler");

        SQLEmployeeManager sem = SQLEmployeeManager.getInstance();
        Alert alert = new Alert(Alert.AlertType.WARNING, "Vous êtes sur le point de supprimer définitivement l'employé " + emp.getFirstName() + " " + emp.getLastName() +
                ", cette action va supprimer toutes les données associé à cette employé.", btnDelete, btnCancel);
        alert.showAndWait();
        if(alert.getResult().equals(btnDelete)) {
            sem.deleteEmployee(emp);
        }
    }
}
