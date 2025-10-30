package Controller.menuController;

import Database.Database;
import Model.KhachHang;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.sql.*;

public class KhachHangFormController {

    @FXML private TextField txtTimKiem;
    @FXML private TableView<KhachHang> tableKhachHang;
    @FXML private TableColumn<KhachHang, String> colTen;
    @FXML private TableColumn<KhachHang, String> colSdt;
    @FXML private TableColumn<KhachHang, Number> colDiem;

    // Form thêm khách
    @FXML private VBox formThem;
    @FXML private TextField txtTenMoi;
    @FXML private TextField txtSdtMoi;
    @FXML private TextField txtDiemMoi;
    @FXML private Button btnThemKhach;

    private final ObservableList<KhachHang> danhSachKhach = FXCollections.observableArrayList();
    private FilteredList<KhachHang> filteredList;
    private KhachHang khachHangDaChon;

    private boolean choPhepThemKhach = true;   // true: show form, false: ẩn
    private boolean choPhepChonKhach = true;   // true: double click chọn khách

    public KhachHang getKhachHangDaChon() {
        return khachHangDaChon;
    }

    @FXML
    private void initialize() {
        colTen.setCellValueFactory(c -> c.getValue().tenProperty());
        colSdt.setCellValueFactory(c -> c.getValue().sdtProperty());
        colDiem.setCellValueFactory(c -> c.getValue().diemProperty());

        napKhachHang();

        filteredList = new FilteredList<>(danhSachKhach, p -> true);
        tableKhachHang.setItems(filteredList);

        txtTimKiem.textProperty().addListener((obs, old, val) -> {
            String lower = val.toLowerCase();
            filteredList.setPredicate(k ->
                    k.getTen().toLowerCase().contains(lower) ||
                            k.getSdt().toLowerCase().contains(lower)
            );
        });

        // Double click để chọn khách hàng
        tableKhachHang.setRowFactory(tv -> {
            TableRow<KhachHang> row = new TableRow<>();
            row.setOnMouseClicked(e -> {
                if (!choPhepChonKhach) return; // hủy double click
                if (e.getClickCount() == 2 && !row.isEmpty()) {
                    khachHangDaChon = row.getItem();
                    dongCuaSo();
                }
            });
            return row;
        });

    }

    public void disableAddButton() {
        choPhepThemKhach = false;
        if (formThem != null) {
            formThem.setVisible(false);
            formThem.setManaged(false); // layout tự thu gọn
        }
        if (btnThemKhach!=null){
            btnThemKhach.setVisible(false);
            btnThemKhach.setManaged(false);
        }
    }

    public void disableDoubleClickSelect() {
        choPhepChonKhach = false;
    }


    private void napKhachHang() {
        danhSachKhach.clear();
        try (Connection conn = Database.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery("SELECT * FROM khachhang")) {
            while (rs.next()) {
                danhSachKhach.add(new KhachHang(
                        rs.getInt("id"),
                        rs.getString("ten"),
                        rs.getString("sdt"),
                        rs.getInt("diem")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void toggleThemKhach() {
        if (!choPhepThemKhach) return;
        formThem.setVisible(!formThem.isVisible());
    }


    @FXML
    private void luuKhachHang() {
        String ten = txtTenMoi.getText().trim();
        String sdt = txtSdtMoi.getText().trim();
        int diem = 0;
        try {
            diem = Integer.parseInt(txtDiemMoi.getText());
        } catch (NumberFormatException ignored) {}

        if (ten.isEmpty() || sdt.isEmpty()) {
            new Alert(Alert.AlertType.WARNING, "Vui lòng nhập đủ thông tin!").show();
            return;
        }

        try (Connection conn = Database.getConnection()) {
            PreparedStatement ps = conn.prepareStatement(
                    "INSERT INTO khachhang (ten, sdt, diem) VALUES (?, ?, ?)",
                    Statement.RETURN_GENERATED_KEYS
            );
            ps.setString(1, ten);
            ps.setString(2, sdt);
            ps.setInt(3, diem);
            ps.executeUpdate();

            ResultSet rs = ps.getGeneratedKeys();
            if (rs.next()) {
                KhachHang khMoi = new KhachHang(rs.getInt(1), ten, sdt, diem);
                danhSachKhach.add(khMoi); // auto cập nhật vào TableView
                formThem.setVisible(false);
                txtTenMoi.clear();
                txtSdtMoi.clear();
                txtDiemMoi.setText("0");
            }
        } catch (SQLException e) {
            new Alert(Alert.AlertType.ERROR, "Lỗi khi thêm khách hàng: " + e.getMessage()).show();
        }
    }

    @FXML
    private void dongCuaSo() {
        ((Stage) txtTimKiem.getScene().getWindow()).close();
    }
}
