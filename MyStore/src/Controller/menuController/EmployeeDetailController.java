package Controller.menuController;

import Model.Employee;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import java.time.format.DateTimeFormatter;

public class EmployeeDetailController {

    @FXML private Label lbId, lbName, lbGender, lbBirth, lbShift, lbSalary;

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    public void setEmployee(Employee emp) {
        lbId.setText(String.valueOf(emp.getId()));
        lbName.setText(emp.getName());
        lbGender.setText(emp.getGender());
        lbBirth.setText(emp.getBirth() != null ? emp.getBirth().format(DATE_FORMATTER) : "");
        lbShift.setText(emp.getShift());
        lbSalary.setText(String.format("%,.0f VND", emp.getSalary()));
    }

    @FXML
    private void onClose() {
        ((Stage) lbId.getScene().getWindow()).close();
    }
}
