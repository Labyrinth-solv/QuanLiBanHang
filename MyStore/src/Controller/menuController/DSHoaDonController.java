package Controller.menuController;

import Database.Database;
import Model.ChiTietHoaDon;
import Model.HoaDon;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class DSHoaDonController {

    @FXML private TableView<HoaDon> tableHoaDon;
    @FXML private TableColumn<HoaDon, Integer> colMaHD;
    @FXML private TableColumn<HoaDon, String> colNgayLap;
    @FXML private TableColumn<HoaDon, Double> colTongTien;
    @FXML private TableColumn<HoaDon, String> colSDT;

    private ObservableList<HoaDon> danhSachHoaDon = FXCollections.observableArrayList();

    @FXML
    private void initialize() {
        colMaHD.setCellValueFactory(data -> data.getValue().maHDProperty().asObject());
        colSDT.setCellValueFactory(data -> data.getValue().SDTProperty());

        // Hiển thị LocalDateTime dưới dạng String
        colNgayLap.setCellValueFactory(data -> {
            LocalDateTime ngay = data.getValue().getNgayLap();
            String formatted = ngay.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));
            return new SimpleStringProperty(formatted);
        });

        colTongTien.setCellValueFactory(data -> data.getValue().tongTienProperty().asObject());

        loadDanhSachHoaDon();

        // Double click để xem chi tiết hóa đơn
        tableHoaDon.setOnMouseClicked((MouseEvent event) -> {
            if (event.getClickCount() == 2 && !tableHoaDon.getSelectionModel().isEmpty()) {
                HoaDon selected = tableHoaDon.getSelectionModel().getSelectedItem();
                if (selected != null) {
                    openChiTietHoaDon(selected.getMaHD());
                }
            }
        });
    }

    // Load danh sách tất cả hóa đơn
    private void loadDanhSachHoaDon() {
        danhSachHoaDon.clear();
        try (Connection conn = Database.getConnection()) {
            String sql = "SELECT mahd, ngaylap, tongtien, sdt FROM hoadon ORDER BY mahd DESC";
            PreparedStatement ps = conn.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                int mahd = rs.getInt("mahd");
                Timestamp ts = rs.getTimestamp("ngaylap");
                LocalDateTime ngayLap = ts.toLocalDateTime();
                double tongTien = rs.getDouble("tongtien");
                String sdt=rs.getString("sdt");

                danhSachHoaDon.add(new HoaDon(mahd, ngayLap, tongTien, sdt));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        tableHoaDon.setItems(danhSachHoaDon);
    }

    // Mở cửa sổ chi tiết hóa đơn
    private void openChiTietHoaDon(int maHD) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/View/menuView/ChiTietHoaDon.fxml"));
            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setTitle("Chi tiết hóa đơn: " + maHD);
            stage.setScene(new Scene(loader.load()));

            // Lấy controller của cửa sổ chi tiết và load dữ liệu
            ChiTietHoaDonController controller = loader.getController();
            controller.loadChiTietHoaDon(maHD); // load dữ liệu từ DB

            stage.showAndWait();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void xoaHoaDon() {
        HoaDon selected = tableHoaDon.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("Vui lòng chọn hóa đơn để xóa!");
            return;
        }

        // Xác nhận trước khi xóa
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Xác nhận xóa");
        confirm.setHeaderText(null);
        confirm.setContentText("Bạn có chắc chắn muốn xóa hóa đơn #" + selected.getMaHD() + " không?");
        if (confirm.showAndWait().orElse(ButtonType.CANCEL) != ButtonType.OK) {
            return;
        }

        // Thực hiện xóa trong DB
        try (Connection conn = Database.getConnection()) {
            conn.setAutoCommit(false);

            // Xóa chi tiết hóa đơn trước
            PreparedStatement psCT = conn.prepareStatement("DELETE FROM chitiethoadon WHERE mahd = ?");
            psCT.setInt(1, selected.getMaHD());
            psCT.executeUpdate();

            // Xóa hóa đơn
            PreparedStatement psHD = conn.prepareStatement("DELETE FROM hoadon WHERE mahd = ?");
            psHD.setInt(1, selected.getMaHD());
            psHD.executeUpdate();

            conn.commit();

            // Xóa khỏi TableView
            danhSachHoaDon.remove(selected);
            showAlert("Xóa hóa đơn thành công!");
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Xóa hóa đơn thất bại!");
        }
    }

    // Hàm hiển thị Alert đơn giản
    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Thông báo");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

}
