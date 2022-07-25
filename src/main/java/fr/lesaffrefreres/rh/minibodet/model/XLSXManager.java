package fr.lesaffrefreres.rh.minibodet.model;

import fr.lesaffrefreres.rh.minibodet.controller.WorkCalendarOperation;
import fr.lesaffrefreres.rh.minibodet.view.PopupPlanningView;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.Month;
import java.time.Year;
import java.time.format.TextStyle;
import java.time.temporal.TemporalAdjusters;
import java.util.List;
import java.util.Locale;

/**
 * This class is used to provide data from an Excel file to the program and to save data from the program to an Excel file.
 * This class is able to read and write data from Excel Files of the 2010+ version of Microsoft Excel. (XLSX)
 */
public class XLSXManager {

    /**
     * Load data from an excel file and fill the corresponding employee calendar.
     * If the employee does not exist in the database, it ask the user if he wants to create a new employee.
     * If the file is not a valid Excel file or if it as not the correct format, an error message is displayed.
     * The template file can be found in the resource folder of the application. in excel/template.xlsx
     * If this solution is not possible, you can use the method {@link #exportFile(File, int, int)} to get a valid file.
     * @param file the file to load
     */
    public static void loadFile(File file) {

        try(OPCPackage pkg = OPCPackage.open(file);
             XSSFWorkbook wb = new XSSFWorkbook(pkg)) { // load the file

            FormulaEvaluator evaluator = wb.getCreationHelper().createFormulaEvaluator(); // create the formula evaluator
            SQLEmployeeManager emp = SQLEmployeeManager.getInstance(); // get the employee manager

            for(Sheet sheet : wb) { // for each sheet (employee)
                if(sheet.getSheetName().split(" ").length < 2) {
                    continue;
                }

                String[] employeeNames = sheet.getRow(3).getCell(2).getStringCellValue().split(" "); // get the employee name and first name

                Employee employee;

                if(!emp.employeeExist(employeeNames[1], employeeNames[0])) { // if the employee does not exist in the database
                    ButtonType btnYes = new ButtonType("Oui");
                    ButtonType btnNo = new ButtonType("Non");
                    Alert alert = new Alert(Alert.AlertType.INFORMATION, "L'employée : " + employeeNames[1] + " " + employeeNames[0] + " n'existe pas voulez vous le créer ?",
                            btnYes,
                            btnNo);
                    alert.setTitle("Employé non trouvé");
                    alert.showAndWait(); // ask the user if he wants to create the employee
                    if(alert.getResult() == btnYes) { // if the user wants to create the employee
                        employee = emp.createEmployee(employeeNames[1], employeeNames[0]); // create the employee
                        Stage popup = new Stage();
                        popup.initModality(Modality.WINDOW_MODAL);
                        popup.setTitle("Planning de " + employee.getFirstName() + " " + employee.getLastName());
                        Scene scene = new Scene(new PopupPlanningView(employee));
                        popup.setScene(scene);
                        popup.showAndWait();
                    } else {
                        continue; // if the user does not want to create the employee, continue to the next employee
                    }
                } else {
                    employee = emp.getEmployee(employeeNames[1], employeeNames[0]); // get the employee if it exists in the database
                }

                Calendar<WorkDay> calendar = employee.getCalendar((int)sheet.getRow(1).getCell(2).getNumericCellValue()); // get the calendar of the employee

                int month = getMonth(sheet.getRow(2).getCell(2).getStringCellValue()); // get the month specified in the file

                double value = 0; // the value of the current cell

                int rowFirstDayOfMonth = LocalDate.of(calendar.getYear(), month, 1).getDayOfWeek().getValue() +8; // row of the first day of the month

                for(int i = rowFirstDayOfMonth; i < 56; i++) { // for each row in the sheet

                    Row row = sheet.getRow(i);

                    // we get the value of the cell corresponding to the hours worked and add it to the employee calendar
                    if(row.getCell(1) != null && (row.getCell(1).getCellType() == CellType.NUMERIC ||
                            row.getCell(1).getCellType() == CellType.FORMULA)) { // if the cell is not empty and if it is a numeric cell or a formula cell
                        if(row.getCell(1).getCellType() == CellType.FORMULA) { // if the cell is a formula cell
                            value = evaluator.evaluate(row.getCell(1)).getNumberValue();
                        } else if(row.getCell(1).getCellType() == CellType.NUMERIC) { // if the cell is a numeric cell
                            value = row.getCell(1).getNumericCellValue();
                        }
                        // total hours
                        double hours = row.getCell(2).getNumericCellValue(); // get the hours worked
                        WorkDay day = calendar.getDay(LocalDate.of(calendar.getYear(), month, (int)value));
                        day.getSchedule().setTotalHours(hours); // set the hours worked on the day

                        // night hours
                        hours = row.getCell(4).getNumericCellValue();
                        day.getSchedule().setNightHours(hours);

                        ((SQLWorkDay)day).create(); // create the day in the database
                    }

                    // we get the label
                    SQLDayLabelManager dlm = SQLDayLabelManager.getInstance();

                    if(row.getCell(6) != null &&
                            row.getCell(6).getCellType() == CellType.STRING &&
                            !row.getCell(6).getStringCellValue().isBlank() &&
                            value != 0.0) {
                        switch (row.getCell(6).getStringCellValue().trim().toLowerCase()) {
                            case "cp" ->
                                    calendar.getDay(LocalDate.of(calendar.getYear(), month, (int) value)).setLabelId(dlm.getHolidayDayLabelId());
                            case "am" ->
                                    calendar.getDay(LocalDate.of(calendar.getYear(), month, (int) value)).setLabelId(dlm.getSickLeaveDayLabelId());
                            case "jf" ->
                                    calendar.getDay(LocalDate.of(calendar.getYear(), month, (int) value)).setComment("jour férié");
                            case "abs inj" ->
                                    calendar.getDay(LocalDate.of(calendar.getYear(), month, (int) value)).setLabelId(dlm.getUnjustifiedAbsenceLabelId());
                        }
                    }

                    // we get the comment
                    if(row.getCell(8) != null &&
                        row.getCell(8).getCellType() == CellType.STRING &&
                    !row.getCell(8).getStringCellValue().isBlank() &&
                    value != 0.0) {
                        calendar.getDay(LocalDate.of(calendar.getYear(), month, (int) value)).setComment(row.getCell(8).getStringCellValue());
                    }
                }
            }

            Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "les données on bien été importé", ButtonType.OK);
            alert.setTitle("import réussi");
            alert.showAndWait(); // display a confirmation message
        } catch(InvalidFormatException ife) {
            ife.printStackTrace();
        } catch (IllegalStateException | NullPointerException | IllegalArgumentException exception) {
            exception.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR, "Le fichier séléctionné n'est pas au format du logiciel. Veuillez vous référer à la documentation pour en savoir plus.");
            alert.setTitle("Erreur d'importation");
            alert.showAndWait(); // display an error message if the file is not a valid Excel file or if it as not the correct format
        } catch (IOException ioe) {
            ioe.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR, "Une erreur de lecture du fichier s'est produite, vérifiez qu'il n'est pas déja ouvert dans une autre application.");
            alert.setTitle("Erreur de lecture du fichier");
            alert.showAndWait(); // display an error message if the file is owned by another application
        }
    }

    /**
     * Export the data from the database to an excel file.
     * It use a template file to create the file.
     * All the employees are exported (one sheet per employee).
     * @param file the file to export to
     * @param month the month to export
     * @param year the year to export
     */
    public static void exportFile(File file, int month, int year) {
        try {

            Workbook wb = WorkbookFactory.create(XLSXManager.class.getResourceAsStream("/excel/template.xlsx")); // load the template file

            FormulaEvaluator evaluator = wb.getCreationHelper().createFormulaEvaluator(); // create the formula evaluator

            SQLEmployeeManager sem = SQLEmployeeManager.getInstance(); // get the employee manager
            SQLDayLabelManager dlm = SQLDayLabelManager.getInstance(); // get the label manager

            if(sem.getEmployeesList().isEmpty()) { // if the employee list is empty
                Alert alert = new Alert(Alert.AlertType.INFORMATION, "Il n'y a rien à éxporter");
                alert.showAndWait(); // display an information message and return
                return;
            }

            for(Employee employee : sem.getEmployeesList()) { // for each employee
                Sheet sheet = wb.cloneSheet(0); // clone the template sheet
                wb.setSheetName(wb.getSheetIndex(sheet), employee.getFirstName() + " " + employee.getLastName()); // set the sheet name to the employee name

                sheet.getRow(1).getCell(2).setCellValue(year); // set the year in the sheet
                sheet.getRow(2).getCell(2).setCellValue(Month.of(month).getDisplayName(TextStyle.FULL, Locale.FRENCH)); // set the month in the sheet
                sheet.getRow(3).getCell(2).setCellValue(employee.getLastName() + " " + employee.getFirstName()); // set the employee name in the sheet

                LocalDate firstDate = LocalDate.of(year, month, 1).with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));

                Row startRow = sheet.getRow(9); // get the start row of the calendar that corresponds to the first day of the first template week

                List<LocalDate> allMonth = firstDate.datesUntil(firstDate.plusWeeks(1).with(TemporalAdjusters.lastDayOfMonth()).plusDays(1)).toList(); // get all the days of the month

                int row = startRow.getRowNum();

                for(LocalDate ld : allMonth) { // for each day of the month
                    sheet.getRow(row).getCell(1).setCellValue(ld.getDayOfMonth());

                    // set total hours
                    double hours = employee.getCalendar(ld.getYear()).getDay(ld).getSchedule().getTotalHours();
                    if(hours != 0.0) {
                        sheet.getRow(row).getCell(2).setCellValue(hours);
                    }

                    // set night hours
                    hours = employee.getCalendar(ld.getYear()).getDay(ld).getSchedule().getNightHours();
                    if(hours != 0.0) {
                        sheet.getRow(row).getCell(4).setCellValue(hours);
                    }

                    // set labels infos

                    long labelId = employee.getCalendar(ld.getYear()).getDay(ld).getLabelId();
                    if (labelId == dlm.getHolidayDayLabelId()) {
                        sheet.getRow(row).getCell(6).setCellValue("CP");
                    } else if (labelId == dlm.getSickLeaveDayLabelId()) {
                        sheet.getRow(row).getCell(6).setCellValue("AM");
                    } else if (labelId == dlm.getBankHolidayDayLabelId()) {
                        sheet.getRow(row).getCell(6).setCellValue("JF");
                        if(employee.getCalendar(ld.getYear()).getDay(ld).getSchedule().getTotalHours() > 0) {
                            sheet.getRow(row).getCell(5).setCellValue(1.0);
                        }
                    }

                    // set comment
                    sheet.getRow(row).getCell(8).setCellValue(employee.getCalendar(ld.getYear()).getDay(ld).getComment());

                    row++;
                    if(ld.getDayOfWeek().equals(DayOfWeek.SUNDAY)) { // if the day is a sunday we jump a row (because the template week is 7 row long and 1 empty row)
                        row++;
                    }
                }

                //update month hours formula :

                int idRowFirstDayOfMonth = LocalDate.of(year, month, 1).getDayOfWeek().getValue();
                int nbDayInMonth = Year.of(year).atMonth(month).lengthOfMonth();

                Row rowLastDayOfMonth = sheet.getRow(idRowFirstDayOfMonth+8);
                int i = 0;
                for(i = idRowFirstDayOfMonth+8; // get the row of the first day of the month and the row of the last day of the month
                    (!rowLastDayOfMonth.getCell(1).getCellType().equals(CellType.NUMERIC)
                            || rowLastDayOfMonth.getCell(1).getNumericCellValue() != nbDayInMonth)
                        ; i++)
                {
                    rowLastDayOfMonth = sheet.getRow(i);
                }

                sheet.getRow(58).getCell(3).setCellFormula("SUM(C9"+":C"+i+")"); // set the formula to sum the hours of the month

                evaluator.evaluateAll(); // evaluate the formulas

                WorkCalendarOperation<SQLWorkDay> operation = new WorkCalendarOperation<>(employee.getCalendar(year));

                LocalDate monthDate = firstDate.plusWeeks(1); // date included in the month

                sheet.getRow(62).getCell(2).setCellValue(operation.getMonthOvertimeLevel1(monthDate, employee)); // set the overtime 25% in the sheet
                sheet.getRow(63).getCell(2).setCellValue(operation.getMonthOvertimeLevel2(monthDate, employee)); // set the overtime 50% in the sheet
                sheet.getRow(64).getCell(2).setCellValue(operation.getMonthSundayOvertimeLevel1(monthDate, employee)); // set the sunday overtime 25% in the sheet
                sheet.getRow(65).getCell(2).setCellValue(operation.getMonthSundayOvertimeLevel1(monthDate, employee)); // set the sunday overtime 50% in the sheet
                sheet.getRow(66).getCell(2).setCellValue(operation.getMonthNbWorkedBankHolidayHours(monthDate)); // set the number of bank holidays in the sheet
                sheet.getRow(67).getCell(2).setCellValue(operation.getMonthNbHoliday(monthDate)); // set the number of holidays in the sheet
                sheet.getRow(68).getCell(2).setCellValue(operation.getMonthSickLeave(monthDate)); // set the number of sick leaves in the sheet
                sheet.getRow(69).getCell(2).setCellValue(operation.getMonthCompensatoryHours(monthDate, employee)); // set the number of compensatory hours in the sheet
                sheet.getRow(70).getCell(2).setCellValue(operation.getMonthNbHours(monthDate)); // set the number of hours in the month in the sheet
            }

            wb.removeSheetAt(wb.getSheetIndex("vierge")); // remove the template sheet

            FileOutputStream fos = new FileOutputStream(file);

            wb.write(fos); // write the file

            wb.close();
            fos.close();

            Alert alert = new Alert(Alert.AlertType.INFORMATION, "La fichier '"+ file.getName() +"' a été exporté avec succès");
            alert.setTitle("Export réussi");
            alert.showAndWait(); // display an information message and return

        } catch (IOException ife) {
            ife.printStackTrace();
        }
    }

    private static int getMonth(String month) {
        return switch (month.trim().toLowerCase()) {
            case "janvier" -> 1;
            case "février" -> 2;
            case "mars" -> 3;
            case "avril" -> 4;
            case "mai" -> 5;
            case "juin" -> 6;
            case "juillet" -> 7;
            case "août" -> 8;
            case "septembre" -> 9;
            case "octobre" -> 10;
            case "novembre" -> 11;
            case "décembre" -> 12;
            default -> throw new IllegalArgumentException("text : " + month + " cannot be parsed into a month");
        };
    }
}
