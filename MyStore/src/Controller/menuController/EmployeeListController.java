package Controller.menuController;

import Model.Employee;
import Database.Database;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import java.io.IOException;
import java.sql.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class EmployeeListController {

    @FXML private TableView<Employee> tableEmployees;
    @FXML private TableColumn<Employee, Number> colId;
    @FXML private TableColumn<Employee, String> colName;
    @FXML private TableColumn<Employee, String> colGender;
    @FXML private TableColumn<Employee, String> colShift;
    @FXML private TableColumn<Employee, String> colBirth;
    @FXML private TableColumn<Employee, Number> colSalary;

    private final ObservableList<Employee> employeeList = FXCollections.observableArrayList();

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    @FXML
    public void initialize() {
        // Cấu hình cột hiển thị theo Property
        colId.setCellValueFactory(c -> c.getValue().idProperty());
        colName.setCellValueFactory(c -> c.getValue().nameProperty());
        colGender.setCellValueFactory(c -> c.getValue().genderProperty());
        colShift.setCellValueFactory(c -> c.getValue().shiftProperty());
        colSalary.setCellValueFactory(c -> c.getValue().salaryProperty());

        // Hiển thị ngày sinh dưới dạng chuỗi (dd/MM/yyyy)
        colBirth.setCellValueFactory(c -> {
            LocalDate date = c.getValue().getBirth();
            String formatted = (date != null) ? date.format(DATE_FORMATTER) : "";
            return new javafx.beans.property.SimpleStringProperty(formatted);
        });

        loadEmployees();

        // Double-click để xem chi tiết
        tableEmployees.setOnMouseClicked(this::onRowDoubleClick);
    }

    /** Nạp danh sách nhân viên từ database MySQL */
    private void loadEmployees() {
        employeeList.clear();
        try (Connection conn = Database.getConnection()) {
            String sql = "SELECT * FROM employee";
            PreparedStatement ps = conn.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                Employee emp = new Employee();
                emp.setId(rs.getInt("id"));
                emp.setName(rs.getString("name"));
                emp.setGender(rs.getString("gender"));
                emp.setShift(rs.getString("shift"));
                emp.setSalary(rs.getDouble("salary"));

                Date birthDate = rs.getDate("birth");
                if (birthDate != null)
                    emp.setBirth(birthDate.toLocalDate());

                employeeList.add(emp);
            }

            tableEmployees.setItems(employeeList);
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Lỗi", "Không thể tải dữ liệu nhân viên:\n" + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    @FXML
    private void onRefresh() {
        loadEmployees();
    }

    @FXML
    private void onEditEmployee() {
        Employee selected = tableEmployees.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("Chú ý", "Vui lòng chọn nhân viên cần sửa.", Alert.AlertType.WARNING);
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/View/menuView/AddEmployee.fxml"));
            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setTitle("Sửa thông tin nhân viên");
            stage.setScene(new Scene(loader.load()));

            AddEmployeeController controller = loader.getController();
            controller.setEmployee(selected);
            controller.setOnSave(this::loadEmployees);

            stage.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Lỗi", "Không thể mở form sửa nhân viên:\n" + e.getMessage(), Alert.AlertType.ERROR);
        }
    }


    /** Xử lý double-click trên bảng để mở cửa sổ chi tiết */
    private void onRowDoubleClick(MouseEvent event) {
        if (event.getClickCount() == 2 && tableEmployees.getSelectionModel().getSelectedItem() != null) {
            Employee selected = tableEmployees.getSelectionModel().getSelectedItem();
            showEmployeeDetail(selected);
        }
    }

    /** Hiển thị cửa sổ chi tiết nhân viên */
    private void showEmployeeDetail(Employee emp) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/View/menuView/EmployeeDetail.fxml"));
            Stage detailStage = new Stage();
            detailStage.setScene(new Scene(loader.load()));
            detailStage.initModality(Modality.APPLICATION_MODAL);
            detailStage.setTitle("Chi tiết nhân viên");

            EmployeeDetailController controller = loader.getController();
            controller.setEmployee(emp);

            detailStage.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Lỗi", "Không thể mở chi tiết nhân viên:\n" + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    /** Hiển thị thông báo đơn giản */
    private void showAlert(String title, String msg, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }
}
