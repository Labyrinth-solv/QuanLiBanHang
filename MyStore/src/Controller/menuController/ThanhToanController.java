package Controller.menuController;

import Database.Database;
import Model.CartItem;
import Model.Product;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.*;
import java.time.LocalDateTime;

public class ThanhToanController {

    // 🔹 Bảng sản phẩm
    @FXML private TableView<Product> tableSanPham;
    @FXML private TableColumn<Product, String> colId, colName;
    @FXML private TableColumn<Product, Double> colPrice;
    @FXML private TableColumn<Product, Integer> colStock;

    // 🔹 Bảng giỏ hàng
    @FXML private TableView<CartItem> tableGioHang;
    @FXML private TableColumn<CartItem, String> colGHName;
    @FXML private TableColumn<CartItem, Integer> colGHSoLuong;
    @FXML private TableColumn<CartItem, Double> colGHDGia, colGHThanhTien;

    // 🔹 Thành phần khác
    @FXML private Spinner<Integer> spSoLuong;
    @FXML private TextField txtTongTien;

    // 🔹 Dữ liệu chính
    private final ObservableList<Product> dsSanPham = FXCollections.observableArrayList();
    private final ObservableList<CartItem> dsGioHang = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        // Cấu hình bảng sản phẩm
        colId.setCellValueFactory(data -> data.getValue().idProperty());
        colName.setCellValueFactory(data -> data.getValue().nameProperty());
        colPrice.setCellValueFactory(data -> data.getValue().priceProperty().asObject());
        colStock.setCellValueFactory(data -> data.getValue().stockProperty().asObject());
        tableSanPham.setItems(dsSanPham);

        // Cấu hình bảng giỏ hàng
        colGHName.setCellValueFactory(data -> data.getValue().nameProperty());
        colGHSoLuong.setCellValueFactory(data -> data.getValue().soLuongProperty().asObject());
        colGHDGia.setCellValueFactory(data -> data.getValue().donGiaProperty().asObject());
        colGHThanhTien.setCellValueFactory(data -> data.getValue().thanhTienProperty().asObject());
        tableGioHang.setItems(dsGioHang);

        // Cấu hình spinner
        spSoLuong.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 100, 1));

        // Load danh sách sản phẩm mặc định
        loadTatCaSanPham();
    }

    /** 📦 Load tất cả sản phẩm ban đầu */
    private void loadTatCaSanPham() {
        dsSanPham.clear();
        try (Connection conn = Database.getConnection();
             PreparedStatement stmt = conn.prepareStatement("SELECT id, categoryId, name, price, stock FROM sanpham");
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                dsSanPham.add(new Product(
                        rs.getString("id"),
                        rs.getString("categoryId"),
                        rs.getString("name"),
                        rs.getDouble("price"),
                        rs.getInt("stock")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /** 🔍 Mở cửa sổ tìm kiếm nâng cao (tái sử dụng SearchProduct.fxml) */
    @FXML
    private void moTimKiemNangCao() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/View/menuView/SearchProduct.fxml"));
            Parent root = loader.load();

            SearchProductController controller = loader.getController();

            // ✅ callback: khi tìm kiếm xong, nhận dữ liệu và gán vào bảng sản phẩm
            controller.setOnSearchComplete(result -> {
                dsSanPham.setAll(result);
                tableSanPham.refresh();
            });

            Stage stage = new Stage();
            stage.setTitle("Tìm kiếm nâng cao");
            stage.setScene(new Scene(root));
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /** 📋 Cập nhật danh sách sản phẩm sau khi tìm kiếm */
    public void updateProductTable(ObservableList<Product> newList) {
        dsSanPham.setAll(newList);
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

        sp.setStock(sp.getStock() - soLuong);
        dsGioHang.add(new CartItem(sp.getName(), soLuong, sp.getPrice()));
        capNhatTongTien();
        tableSanPham.refresh();
    }

    /** 🧹 Xóa giỏ hàng */
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
        loadTatCaSanPham();
    }

    /** 💳 Thanh toán */
    @FXML
    private void thanhToan() {
        if (dsGioHang.isEmpty()) {
            showAlert("Giỏ hàng trống!");
            return;
        }

        try (Connection conn = Database.getConnection()) {
            conn.setAutoCommit(false);

            PreparedStatement insertHD = conn.prepareStatement(
                    "INSERT INTO hoadon (ngaylap, tongtien) VALUES (?, ?)",
                    Statement.RETURN_GENERATED_KEYS);
            insertHD.setTimestamp(1, Timestamp.valueOf(LocalDateTime.now()));
            insertHD.setDouble(2, tinhTongTien());
            insertHD.executeUpdate();

            ResultSet rs = insertHD.getGeneratedKeys();
            rs.next();
            int maHD = rs.getInt(1);

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
            loadTatCaSanPham();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /** 🧮 Cập nhật tổng tiền */
    private void capNhatTongTien() {
        txtTongTien.setText(String.format("%,.0f₫", tinhTongTien()));
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
