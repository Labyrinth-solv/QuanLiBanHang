package Controller.menuController;

import Database.Database;
import Model.CartItem;
import Model.KhachHang;
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

    //  Bảng sản phẩm
    @FXML private TableView<Product> tableSanPham;
    @FXML private TableColumn<Product, String> colId, colName;
    @FXML private TableColumn<Product, Double> colPrice;
    @FXML private TableColumn<Product, Integer> colStock;

    //  Bảng giỏ hàng
    @FXML private TableView<CartItem> tableGioHang;
    @FXML private TableColumn<CartItem, String> colGHName;
    @FXML private TableColumn<CartItem, Integer> colGHSoLuong;
    @FXML private TableColumn<CartItem, Double> colGHDGia, colGHThanhTien;

    // Thành phần khác
    @FXML private TextField txtSoLuong;
    @FXML private TextField txtTongTien;
    @FXML private TextField txtKhachHang;
    //  Thêm biến FXML cho giảm giá
    @FXML private TextField txtGiamGia;

    //  Dữ liệu chính
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

        // Cấu hình TextField số lượng - chỉ cho phép nhập số
        txtSoLuong.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d*")) {
                txtSoLuong.setText(newValue.replaceAll("[^\\d]", ""));
            }
        });
        
        // Set giá trị mặc định
        txtSoLuong.setText("1");

        // Load danh sách sản phẩm mặc định
        loadTatCaSanPham();

    }

    //  Load tất cả sản phẩm ban đầu
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

    //  Mở cửa sổ tìm kiếm nâng cao (tái sử dụng SearchProduct.fxml)
    @FXML
    private void moTimKiemNangCao() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/View/menuView/SearchProduct.fxml"));
            Parent root = loader.load();

            SearchProductController controller = loader.getController();

            // callback: khi tìm kiếm xong, nhận dữ liệu và gán vào bảng sản phẩm
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

    // Cập nhật danh sách sản phẩm sau khi tìm kiếm
    public void updateProductTable(ObservableList<Product> newList) {
        dsSanPham.setAll(newList);
    }

    // ➕ Thêm vào giỏ hàng
    @FXML
    private void themVaoGio() {
        Product sp = tableSanPham.getSelectionModel().getSelectedItem();
        if (sp == null) {
            showAlert("Vui lòng chọn sản phẩm!");
            return;
        }

        // Lấy số lượng từ TextField
        String soLuongText = txtSoLuong.getText().trim();
        if (soLuongText.isEmpty()) {
            showAlert("Vui lòng nhập số lượng!");
            txtSoLuong.setText("1");
            return;
        }

        int soLuong;
        try {
            soLuong = Integer.parseInt(soLuongText);
        } catch (NumberFormatException e) {
            showAlert("Số lượng không hợp lệ!");
            txtSoLuong.setText("1");
            return;
        }

        if (soLuong <= 0) {
            showAlert("Số lượng phải lớn hơn 0!");
            txtSoLuong.setText("1");
            return;
        }

        if (soLuong > sp.getStock()) {
            showAlert("Không đủ tồn kho! Tồn kho hiện tại: " + sp.getStock());
            txtSoLuong.setText("1");
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
        
        // Reset số lượng về 1 sau khi thêm thành công
        txtSoLuong.setText("1");
    }

    // Xóa giỏ hàng
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

    private KhachHang khachHangDangChon;



    //  Thanh toán (có tính điểm khách hàng)
    @FXML
    private void thanhToan() {
        if (dsGioHang.isEmpty()) {
            showAlert("Giỏ hàng trống!");
            return;
        }

        double tongTien = dsGioHang.stream().mapToDouble(CartItem::getThanhTien).sum();
        capNhatTongTien();

        // Lấy khách hàng nếu có
        KhachHang khachHang = khachHangDangChon;
        double giamGia = tinhGiamGiaTheoDiem(khachHangDangChon, tongTien);

        // Cập nhật điểm khách hàng thực tế
        if (khachHangDangChon != null) {
            int diemMoi = khachHangDangChon.getDiem() + ((tongTien >= 50_000) ? 20 : 10);
            if (diemMoi >= 50) {
                diemMoi -= 50; // trừ điểm để áp giảm giá
            }
            khachHangDangChon.setDiem(diemMoi);

            // Lưu điểm mới vào DB
            try (Connection conn = Database.getConnection();
                 PreparedStatement ps = conn.prepareStatement("UPDATE khachhang SET diem=? WHERE id=?")) {
                ps.setInt(1, diemMoi);
                ps.setInt(2, khachHangDangChon.getId());
                ps.executeUpdate();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        // Lưu hóa đơn và chi tiết vào DB
        try (Connection conn = Database.getConnection()) {
            conn.setAutoCommit(false);

            // Thêm hóa đơn
            PreparedStatement insertHD = conn.prepareStatement(
                    "INSERT INTO hoadon (ngaylap, tongtien, sdt, giamgia) VALUES (?, ?, ?, ?)",
                    Statement.RETURN_GENERATED_KEYS);
            insertHD.setTimestamp(1, Timestamp.valueOf(LocalDateTime.now()));
            insertHD.setDouble(2, tongTien - giamGia);
            insertHD.setString(3, khachHang != null ? khachHang.getSdt() : null);
            insertHD.setDouble(4, giamGia);
            insertHD.executeUpdate();

            ResultSet rs = insertHD.getGeneratedKeys();
            rs.next();
            int maHD = rs.getInt(1);

            // Thêm chi tiết hóa đơn
            PreparedStatement insertCT = conn.prepareStatement(
                    "INSERT INTO chitiethoadon (mahd, tensp, soluong, dongia, thanhtien) VALUES (?, ?, ?, ?, ?)");
            for (CartItem item : dsGioHang) {
                double itemGiamGia = (giamGia > 0) ? giamGia / dsGioHang.size() : 0; // chỉ để hiển thị
                insertCT.setInt(1, maHD);
                insertCT.setString(2, item.getName());
                insertCT.setInt(3, item.getSoLuong());
                insertCT.setDouble(4, item.getDonGia());
                insertCT.setDouble(5, item.getThanhTien() - itemGiamGia);
                insertCT.addBatch();

                // Cập nhật tạm giảm giá trong TableView
                item.setGiamGia(itemGiamGia);
            }
            insertCT.executeBatch();

            conn.commit();

            // Hiển thị alert
            StringBuilder msg = new StringBuilder("Thanh toán thành công!\n");
            msg.append("Tổng tiền: ").append(String.format("%,.0f₫", tongTien ));
            if (giamGia > 0) msg.append("\nGiảm giá: -").append(String.format("%,.0f₫", giamGia));
            msg.append("\nThành tiền: ").append(String.format("%,.0f₫", tongTien-giamGia));
            if (khachHang != null) {
                msg.append("\nKhách hàng: ").append(khachHang.getTen())
                        .append("\nĐiểm hiện tại: ").append(khachHang.getDiem());
            }
            showAlert(msg.toString());

            // Xóa giỏ hàng và cập nhật view
            dsGioHang.clear();
            loadTatCaSanPham();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void chonKhachHang() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/View/menuView/KhachHangForm.fxml"));
            Stage stage = new Stage();
            stage.setTitle("Chọn khách hàng");
            stage.setScene(new Scene(loader.load()));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();

            KhachHangFormController controller = loader.getController();
            KhachHang kh = controller.getKhachHangDaChon();

            if (kh != null) {
                khachHangDangChon = kh; // Lưu lại khách hàng đã chọn
                //HIEN THI TEN KH LEN MAN HINH
                txtKhachHang.setText(kh.getTen() + " - " + kh.getSdt() + " (điểm: " + kh.getDiem() + ")");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    //  Cập nhật tổng tiền
    private void capNhatTongTien() {
        double tongTien = tinhTongTien();
        double giamGia = tinhGiamGiaTheoDiem(khachHangDangChon, tongTien);

        // Trừ giảm giá
        tongTien -= giamGia;

        txtTongTien.setText(String.format("%,.0f₫", tongTien));
        txtGiamGia.setText(String.format("%,.0f₫", giamGia));
    }



    private double tinhGiamGiaTheoDiem(KhachHang kh, double tongTien) {
        if (kh == null) return 0;

        int diemMoi = kh.getDiem() + ((tongTien >= 50_000) ? 20 : 10);
        if (diemMoi >= 50) {
            return 10_000;
        }
        return 0;
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
