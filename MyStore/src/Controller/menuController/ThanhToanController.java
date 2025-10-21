package Controller.menuController;

import Database.Database;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import Model.*;

import java.sql.*;
import java.time.LocalDateTime;

public class ThanhToanController {

    @FXML private TableView<Product> tableSanPham;
    @FXML private TableColumn<Product, String> colId, colName;
    @FXML private TableColumn<Product, Double> colPrice;
    @FXML private TableColumn<Product, Integer> colStock;

    @FXML private TableView<CartItem> tableGioHang;
    @FXML private TableColumn<CartItem, String> colGHName;
    @FXML private TableColumn<CartItem, Integer> colGHSoLuong;
    @FXML private TableColumn<CartItem, Double> colGHDGia, colGHThanhTien;

    @FXML private Spinner<Integer> spSoLuong;
    @FXML private TextField txtTongTien;

    private ObservableList<Product> dsSanPham = FXCollections.observableArrayList();
    private ObservableList<CartItem> dsGioHang = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        // Gán dữ liệu cột cho bảng sản phẩm
        colId.setCellValueFactory(data -> data.getValue().idProperty());
        colName.setCellValueFactory(data -> data.getValue().nameProperty());
        colPrice.setCellValueFactory(data -> data.getValue().priceProperty().asObject());
        colStock.setCellValueFactory(data -> data.getValue().stockProperty().asObject());

        // Gán dữ liệu cột cho bảng giỏ hàng
        colGHName.setCellValueFactory(data -> data.getValue().nameProperty());
        colGHSoLuong.setCellValueFactory(data -> data.getValue().soLuongProperty().asObject());
        colGHDGia.setCellValueFactory(data -> data.getValue().donGiaProperty().asObject());
        colGHThanhTien.setCellValueFactory(data -> data.getValue().thanhTienProperty().asObject());

        tableSanPham.setItems(dsSanPham);
        tableGioHang.setItems(dsGioHang);

        // Spinner chọn số lượng
        spSoLuong.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 100, 1));

        // Load sản phẩm từ DB
        loadSanPham();
    }

    /** 📦 Load danh sách sản phẩm từ DB */
    private void loadSanPham() {
        dsSanPham.clear();
        String sql = "SELECT id, categoryId, name, price, stock FROM sanpham";
        try (Connection conn = Database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                dsSanPham.add(new Product(
                        rs.getString("id"),
                        rs.getString("categoryId"),  // 🔹 thêm categoryId
                        rs.getString("name"),
                        rs.getDouble("price"),       // 🔹 price trước
                        rs.getInt("stock")           // 🔹 stock sau
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /** ➕ Thêm vào giỏ hàng */
    @FXML
    private void themVaoGio() {
        Product sp = tableSanPham.getSelectionModel().getSelectedItem();
        if (sp == null) {
            showAlert("Vui lòng chọn sản phẩm!");
            return;
        }

        int soLuong = spSoLuong.getValue();
        if (soLuong > sp.getStock()) {
            showAlert("Không đủ tồn kho!");
            return;
        }

        // Cập nhật tồn kho trong DB
        try (Connection conn = Database.getConnection()) {
            PreparedStatement stmt = conn.prepareStatement(
                    "UPDATE sanpham SET stock = stock - ? WHERE id = ?");
            stmt.setInt(1, soLuong);
            stmt.setString(2, sp.getId());
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            return;
        }

        // Cập nhật tồn kho trong danh sách tạm
        sp.setStock(sp.getStock() - soLuong);

        // Thêm vào giỏ
        CartItem item = new CartItem(sp.getName(), soLuong, sp.getPrice());
        dsGioHang.add(item);
        capNhatTongTien();
        tableSanPham.refresh();
    }

    /** 🧹 Xóa giỏ hàng (và hoàn tồn kho) */
    @FXML
    private void xoaGioHang() {
        try (Connection conn = Database.getConnection()) {
            for (CartItem item : dsGioHang) {
                PreparedStatement stmt = conn.prepareStatement(
                        "UPDATE sanpham SET stock = stock + ? WHERE name = ?");
                stmt.setInt(1, item.getSoLuong());
                stmt.setString(2, item.getName());
                stmt.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        dsGioHang.clear();
        capNhatTongTien();
        loadSanPham();
    }

    /** 💰 Thanh toán (lưu hóa đơn và chi tiết) */
    @FXML
    private void thanhToan() {
        if (dsGioHang.isEmpty()) {
            showAlert("Giỏ hàng trống!");
            return;
        }

        try (Connection conn = Database.getConnection()) {
            conn.setAutoCommit(false);

            // Lưu hóa đơn
            PreparedStatement insertHD = conn.prepareStatement(
                    "INSERT INTO hoadon (ngaylap, tongtien) VALUES (?, ?)",
                    Statement.RETURN_GENERATED_KEYS);
            insertHD.setTimestamp(1, Timestamp.valueOf(LocalDateTime.now()));
            insertHD.setDouble(2, tinhTongTien());
            insertHD.executeUpdate();

            ResultSet rs = insertHD.getGeneratedKeys();
            rs.next();
            int maHD = rs.getInt(1);

            // Lưu chi tiết hóa đơn
            PreparedStatement insertCT = conn.prepareStatement(
                    "INSERT INTO chitiethoadon (mahd, tensp, soluong, dongia, thanhtien) VALUES (?, ?, ?, ?, ?)");
            for (CartItem item : dsGioHang) {
                insertCT.setInt(1, maHD);
                insertCT.setString(2, item.getName());
                insertCT.setInt(3, item.getSoLuong());
                insertCT.setDouble(4, item.getDonGia());
                insertCT.setDouble(5, item.getThanhTien());
                insertCT.addBatch();
            }
            insertCT.executeBatch();

            conn.commit();
            showAlert("Thanh toán thành công!");
            dsGioHang.clear();
            capNhatTongTien();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        loadSanPham();
    }

    /** 🧮 Cập nhật tổng tiền */
    private void capNhatTongTien() {
        txtTongTien.setText(String.format("%.0f", tinhTongTien()));
    }

    private double tinhTongTien() {
        return dsGioHang.stream().mapToDouble(CartItem::getThanhTien).sum();
    }

    private void showAlert(String msg) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }
}
