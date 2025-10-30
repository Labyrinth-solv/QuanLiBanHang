package Controller.menuController;

import Database.Database;
import Model.ChiTietHoaDon;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class ChiTietHoaDonController {

    @FXML
    private TextField txtMaHD, txtNgayLap, txtTongTien, txtGiamGia;

    @FXML
    private TableView<ChiTietHoaDon> tableChiTiet;

    @FXML
    private TableColumn<ChiTietHoaDon, String> colTenSP;

    @FXML
    private TableColumn<ChiTietHoaDon, Integer> colSoLuong;

    @FXML
    private TableColumn<ChiTietHoaDon, Double> colDonGia, colThanhTien;

    private ObservableList<ChiTietHoaDon> danhSachChiTiet = FXCollections.observableArrayList();

    @FXML
    private void initialize() {
        // Liên kết cột với property
        colTenSP.setCellValueFactory(data -> data.getValue().tenSPProperty());
        colSoLuong.setCellValueFactory(data -> data.getValue().soLuongProperty().asObject());
        colDonGia.setCellValueFactory(data -> data.getValue().donGiaProperty().asObject());
        colThanhTien.setCellValueFactory(data -> data.getValue().thanhTienProperty().asObject());
    }

    /**
     * Load chi tiết hóa đơn theo mã hóa đơn
     */
    public void loadChiTietHoaDon(int maHD) {
        danhSachChiTiet.clear();

        try (Connection conn = Database.getConnection()) {
            // Lấy thông tin hóa đơn
            String sqlHD = "SELECT mahd, ngaylap, tongtien, giamgia FROM hoadon WHERE mahd = ?";
            PreparedStatement psHD = conn.prepareStatement(sqlHD);
            psHD.setInt(1, maHD);
            ResultSet rsHD = psHD.executeQuery();

            LocalDateTime ngayLap = null;
            double tongTien = 0;
            double giamGia =0;

            if (rsHD.next()) {
                txtMaHD.setText(String.valueOf(maHD));
                ngayLap = rsHD.getTimestamp("ngaylap").toLocalDateTime();
                tongTien = rsHD.getDouble("tongtien");
                giamGia=rsHD.getDouble("giamgia");
                txtNgayLap.setText(ngayLap.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")));
                txtTongTien.setText(String.format("%.0f", tongTien));
                txtGiamGia.setText(String.format("%.1f", giamGia));
            }

            // Lấy chi tiết sản phẩm
            String sqlCT = "SELECT tensp, soluong, dongia FROM chitiethoadon WHERE mahd = ?";
            PreparedStatement psCT = conn.prepareStatement(sqlCT);
            psCT.setInt(1, maHD);
            ResultSet rsCT = psCT.executeQuery();

            while (rsCT.next()) {
                String tenSP = rsCT.getString("tensp");
                int soLuong = rsCT.getInt("soluong");
                double donGia = rsCT.getDouble("dongia");

                danhSachChiTiet.add(new ChiTietHoaDon(tenSP, soLuong, donGia));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        tableChiTiet.setItems(danhSachChiTiet);
    }

    /**
     * Đóng cửa sổ chi tiết
     */
    @FXML
    private void dongCuaSo() {
        Stage stage = (Stage) txtMaHD.getScene().getWindow();
        stage.close();
    }

    /**
     * In hóa đơn (demo)
     */
    @FXML
    private void inHoaDon() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("In hóa đơn");
        alert.setHeaderText(null);
        alert.setContentText("Hóa đơn đã được gửi đến máy in (demo)!");
        alert.showAndWait();
    }
}
