package Controller.menuController;

import Model.DoanhThu;
import Database.Database;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.chart.*;
import javafx.scene.control.*;

import java.sql.*;
import java.time.LocalDate;

public class DoanhThuController {

    @FXML
    private DatePicker dateFrom, dateTo;
    @FXML
    private Button btnThongKe, btnLamMoi;
    @FXML
    private Label lblTongDoanhThu;
    @FXML
    private TableView<DoanhThu> tableDoanhThu;
    @FXML
    private TableColumn<DoanhThu, String> colNgay;
    @FXML
    private TableColumn<DoanhThu, Integer> colSoHoaDon;
    @FXML
    private TableColumn<DoanhThu, Double> colTongTien;
    @FXML
    private BarChart<String, Number> barChartDoanhThu;

    private ObservableList<DoanhThu> dataList = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        colNgay.setCellValueFactory(cellData -> cellData.getValue().ngayProperty());
        colSoHoaDon.setCellValueFactory(cellData -> cellData.getValue().soHoaDonProperty().asObject());
        colTongTien.setCellValueFactory(cellData -> cellData.getValue().tongTienProperty().asObject());

        btnThongKe.setOnAction(e -> thongKe());
        btnLamMoi.setOnAction(e -> lamMoi());
    }

    private void thongKe() {
        LocalDate from = dateFrom.getValue();
        LocalDate to = dateTo.getValue();

        if (from == null || to == null) {
            new Alert(Alert.AlertType.WARNING, "Vui lòng chọn khoảng thời gian!").show();
            return;
        }

        dataList.clear();

        String sql = """
            SELECT DATE(ngaylap) AS ngay_lap,
                   COUNT(mahd) AS ma_hoa_don,
                   SUM(tongtien) AS tong_tien
            FROM hoadon
            WHERE ngay_lap BETWEEN ? AND ?
            GROUP BY DATE(ngay_lap)
            ORDER BY ngay_lap;
        """;

        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setDate(1, Date.valueOf(from));
            ps.setDate(2, Date.valueOf(to));

            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                String ngay = rs.getString("ngay_lap");
                int soHD = rs.getInt("ma_hoa_don");
                double tong = rs.getDouble("tong_tien");
                dataList.add(new DoanhThu(ngay, soHD, tong));
            }

            tableDoanhThu.setItems(dataList);
            updateChart();
            updateTotal();

        } catch (SQLException ex) {
            new Alert(Alert.AlertType.ERROR, "Lỗi tải dữ liệu: " + ex.getMessage()).show();
            ex.printStackTrace();
        }
    }

    private void lamMoi() {
        dateFrom.setValue(null);
        dateTo.setValue(null);
        dataList.clear();
        tableDoanhThu.setItems(dataList);
        barChartDoanhThu.getData().clear();
        lblTongDoanhThu.setText("0 VNĐ");
    }

    private void updateChart() {
        barChartDoanhThu.getData().clear();

        XYChart.Series<String, Number> series = new XYChart.Series<>();
        for (DoanhThu d : dataList) {
            series.getData().add(new XYChart.Data<>(d.getNgay(), d.getTongTien()));
        }
        barChartDoanhThu.getData().add(series);
    }

    private void updateTotal() {
        double total = dataList.stream().mapToDouble(DoanhThu::getTongTien).sum();
        lblTongDoanhThu.setText(String.format("%,.0f VNĐ", total));
    }
}
