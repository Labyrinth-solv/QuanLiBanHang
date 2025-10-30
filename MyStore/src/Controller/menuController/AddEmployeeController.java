package Controller.menuController;

import Database.Database;
import Model.Employee;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.util.converter.NumberStringConverter;
import java.sql.*;
import java.time.LocalDate;

public class AddEmployeeController {

    @FXML private TextField tfName;
    @FXML private DatePicker dpBirth;
    @FXML private ComboBox<String> cbGender;
    @FXML private ComboBox<String> cbShift;
    @FXML private TextField tfSalary;
    @FXML private Label lbHeader;
    @FXML private Button btnSave;

    private Runnable onSaveCallback;
    private Employee employee;

    /** Dùng cho cả thêm mới và sửa */
    @FXML
    public void initialize() {
        cbGender.getItems().addAll("Nam", "Nữ", "Khác");
        cbShift.getItems().addAll("Ca sáng", "Ca chiều", "Ca tối");
    }

    /** Thiết lập callback sau khi lưu */
    public void setOnSave(Runnable callback) {
        this.onSaveCallback = callback;
    }

    /** Thiết lập nhân viên hiện tại (nếu là sửa) */
    public void setEmployee(Employee emp) {
        this.employee = emp;

        if (emp != null) {
            // Hiển thị thông tin cũ
            tfName.setText(emp.getName());
            dpBirth.setValue(emp.getBirth());
            cbGender.setValue(emp.getGender());
            cbShift.setValue(emp.getShift());
            tfSalary.setText(String.valueOf(emp.getSalary()));

            // Đổi tiêu đề và nút
            if (lbHeader != null)
                lbHeader.setText("✏️ Sửa Thông Tin Nhân Viên");
            if (btnSave != null)
                btnSave.setText("💾 Cập nhật");
        } else {
            this.employee = new Employee(); // thêm mới
        }

        // Dùng binding hai chiều để form cập nhật trực tiếp vào model
        tfName.textProperty().bindBidirectional(this.employee.nameProperty());
        dpBirth.valueProperty().bindBidirectional(this.employee.birthProperty());
        cbGender.valueProperty().bindBidirectional(this.employee.genderProperty());
        cbShift.valueProperty().bindBidirectional(this.employee.shiftProperty());
        tfSalary.textProperty().bindBidirectional(this.employee.salaryProperty(), new NumberStringConverter());
    }

    /** Xử lý lưu / cập nhật */
    @FXML
    public void onSave() {
        try (Connection conn = Database.getConnection()) {
            if (employee.getName() == null || employee.getName().isBlank()) {
                new Alert(Alert.AlertType.WARNING, "Vui lòng nhập tên nhân viên!").showAndWait();
                return;
            }
            if (employee.getBirth() == null) {
                new Alert(Alert.AlertType.WARNING, "Vui lòng chọn ngày sinh!").showAndWait();
                return;
            }

            if (employee.getId() > 0) {
                // --- UPDATE ---
                String sql = "UPDATE employee SET name=?, birth=?, gender=?, shift=?, salary=? WHERE id=?";
                try (PreparedStatement ps = conn.prepareStatement(sql)) {
                    ps.setString(1, employee.getName());
                    ps.setDate(2, Date.valueOf(employee.getBirth()));
                    ps.setString(3, employee.getGender());
                    ps.setString(4, employee.getShift());
                    ps.setDouble(5, employee.getSalary());
                    ps.setInt(6, employee.getId());
                    ps.executeUpdate();
                }
                new Alert(Alert.AlertType.INFORMATION, "Cập nhật nhân viên thành công!").showAndWait();
            } else {
                // --- INSERT ---
                String sql = "INSERT INTO employee (name, birth, gender, shift, salary) VALUES (?, ?, ?, ?, ?)";
                try (PreparedStatement ps = conn.prepareStatement(sql)) {
                    ps.setString(1, employee.getName());
                    ps.setDate(2, Date.valueOf(employee.getBirth()));
                    ps.setString(3, employee.getGender());
                    ps.setString(4, employee.getShift());
                    ps.setDouble(5, employee.getSalary());
                    ps.executeUpdate();
                }
                new Alert(Alert.AlertType.INFORMATION, "Thêm nhân viên mới thành công!").showAndWait();
            }

            if (onSaveCallback != null) onSaveCallback.run();

        } catch (SQLException e) {
            e.printStackTrace();
            new Alert(Alert.AlertType.ERROR, "Lỗi khi lưu vào database:\n" + e.getMessage()).showAndWait();
        }
    }

    @FXML
    public void onCancel() {
        tfName.getScene().getWindow().hide();
    }
}
