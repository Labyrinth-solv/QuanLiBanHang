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
    private ChoiceBox<Integer> cbThang;

    @FXML
    private Button btnThongKe, btnLamMoi, btnXuatExcel;

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
    private LineChart<String, Number> lineChartDoanhThu;

    private ObservableList<DoanhThu> dataList = FXCollections.observableArrayList();

    @FXML
    public void initialize() {

        // Render cột
        colNgay.setCellValueFactory(cell -> cell.getValue().ngayProperty());
        colSoHoaDon.setCellValueFactory(cell -> cell.getValue().soHoaDonProperty().asObject());
        colTongTien.setCellValueFactory(cell -> cell.getValue().tongTienProperty().asObject());

        // Thêm tháng 1 → 12
        for (int i = 1; i <= 12; i++)
            cbThang.getItems().add(i);

        // Chọn tháng hiện tại
        cbThang.setValue(LocalDate.now().getMonthValue());

        // Sự kiện
        btnThongKe.setOnAction(e -> thongKe());
        btnLamMoi.setOnAction(e -> lamMoi());

        // Load mặc định
        thongKe();
    }

    private void thongKe() {
        Integer month = cbThang.getValue();

        if (month == null) {
            new Alert(Alert.AlertType.WARNING, "Vui lòng chọn tháng!").show();
            return;
        }

        dataList.clear();

        String sql = """
            SELECT DAY(ngaylap) AS ngay,
                   COUNT(mahd) AS sohd,
                   SUM(tongtien) AS tong
            FROM hoadon
            WHERE MONTH(ngaylap) = ?
            GROUP BY DAY(ngaylap)
            ORDER BY ngay;
        """;

        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, month);

            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                String ngay = String.valueOf(rs.getInt("ngay"));
                int soHD = rs.getInt("sohd");
                double tong = rs.getDouble("tong");

                dataList.add(new DoanhThu(ngay, soHD, tong));
            }

            tableDoanhThu.setItems(dataList);
            updateChart();
            updateTotal();

        } catch (SQLException ex) {
            new Alert(Alert.AlertType.ERROR, "Không thể tải dữ liệu: " + ex.getMessage()).show();
            ex.printStackTrace();
        }
    }

    private void lamMoi() {
        cbThang.setValue(null);
        dataList.clear();
        tableDoanhThu.setItems(dataList);
        lineChartDoanhThu.getData().clear();
        lblTongDoanhThu.setText("0 VNĐ");
    }

    private void updateChart() {
        lineChartDoanhThu.getData().clear();

        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Doanh thu");

        for (DoanhThu d : dataList) {
            series.getData().add(new XYChart.Data<>(d.getNgay(), d.getTongTien()));
        }

        lineChartDoanhThu.getData().add(series);
    }


    private void updateTotal() {
        double total = dataList.stream().mapToDouble(DoanhThu::getTongTien).sum();
        lblTongDoanhThu.setText(String.format("%,.0f VNĐ", total));
    }
}
