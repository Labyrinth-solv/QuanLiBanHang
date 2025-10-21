package Controller.menuController;

import Database.Database;
import Model.Product;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.sql.*;

public class DSSP_DichVuController {

    @FXML
    private TableView<Product> tableSanPham;

    @FXML
    private TableColumn<Product, String> colId, colCategoryId, colName, colUnit, colPrice;

    @FXML
    private TextField txtId, txtCategoryId, txtName, txtUnit, txtPrice, txtTimKiem;

    @FXML
    private Label lblTongSP;

    private ObservableList<Product> productList = FXCollections.observableArrayList();
    private boolean isEditing = false;

    // ------------------ Khởi tạo TableView ------------------
    @FXML
    public void initialize() {
        // Bind các cột với thuộc tính của Product
        colId.setCellValueFactory(cell -> cell.getValue().idProperty());
        colCategoryId.setCellValueFactory(cell -> cell.getValue().categoryIdProperty());
        colName.setCellValueFactory(cell -> cell.getValue().nameProperty());
        colUnit.setCellValueFactory(cell -> cell.getValue().unitProperty());
        colPrice.setCellValueFactory(cell -> cell.getValue().priceProperty());
        loadProducts(); // load dữ liệu từ database
    }

    // ------------------ Load dữ liệu ------------------
    private void loadProducts() {
        productList.clear();
        String sql = "SELECT * FROM sanpham";
        try (Connection conn = Database.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                productList.add(new Product(
                        rs.getString("id"),
                        rs.getString("categoryId"),
                        rs.getString("name"),
                        rs.getString("unit"),
                        rs.getString("price")
                ));
            }

            tableSanPham.setItems(productList);
            lblTongSP.setText("Tổng sản phẩm: " + productList.size());

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // ------------------ Thêm sản phẩm ------------------
    @FXML
    private void themSanPham() {
        String id = txtId.getText();
        String categoryId = txtCategoryId.getText();
        String name = txtName.getText();
        String unit = txtUnit.getText();
        String price = txtPrice.getText();

        String sql = "INSERT INTO sanpham(id, categoryId, name, unit, price) VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = Database.getConnection();
             PreparedStatement pst = conn.prepareStatement(sql)) {

            pst.setString(1, id);
            pst.setString(2, categoryId);
            pst.setString(3, name);
            pst.setString(4, unit);
            pst.setString(5, price);

            int result = pst.executeUpdate();
            if (result > 0) {
                loadProducts();
                clearFields();
                System.out.println("Thêm sản phẩm thành công");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // ------------------ Sửa sản phẩm ------------------
    @FXML
    private void suaSanPham() {
        Product selected = tableSanPham.getSelectionModel().getSelectedItem();
        if (selected == null) {
            System.out.println("Vui lòng chọn sản phẩm để sửa!");
            return;
        }

        if (!isEditing) {
            // Bước 1: điền dữ liệu lên TextField để chỉnh sửa
            txtId.setText(selected.getId());
            txtId.setEditable(false);
            txtCategoryId.setText(selected.getCategoryId());
            txtName.setText(selected.getName());
            txtUnit.setText(selected.getUnit());
            txtPrice.setText(selected.getPrice());

            isEditing = true;
            System.out.println("Chỉnh sửa sản phẩm: thay đổi TextField và nhấn lại nút Sửa để lưu.");
        } else {
            // Bước 2: cập nhật database
            String categoryId = txtCategoryId.getText();
            String name = txtName.getText();
            String unit = txtUnit.getText();
            String price = txtPrice.getText();

            String sql = "UPDATE sanpham SET categoryId=?, name=?, unit=?, price=? WHERE id=?";

            try (Connection conn = Database.getConnection();
                 PreparedStatement pst = conn.prepareStatement(sql)) {

                pst.setString(1, categoryId);
                pst.setString(2, name);
                pst.setString(3, unit);
                pst.setString(4, price);
                pst.setString(5, selected.getId());

                int result = pst.executeUpdate();
                if (result > 0) {
                    selected.setCategoryId(categoryId);
                    selected.setName(name);
                    selected.setUnit(unit);
                    selected.setPrice(price);
                    tableSanPham.refresh();
                    clearFields();
                    System.out.println("Sửa sản phẩm thành công");
                }

            } catch (SQLException e) {
                e.printStackTrace();
            }

            isEditing = false;
        }
    }

    // ------------------ Xóa sản phẩm ------------------
    @FXML
    private void xoaSanPham() {
        Product selected = tableSanPham.getSelectionModel().getSelectedItem();
        if (selected == null) return;

        String sql = "DELETE FROM sanpham WHERE id=?";
        try (Connection conn = Database.getConnection();
             PreparedStatement pst = conn.prepareStatement(sql)) {

            pst.setString(1, selected.getId());
            int result = pst.executeUpdate();
            if (result > 0) {
                productList.remove(selected);
                tableSanPham.refresh();
                lblTongSP.setText("Tổng sản phẩm: " + productList.size());
                clearFields();
                System.out.println("Xóa sản phẩm thành công");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // ------------------ Tìm kiếm sản phẩm ------------------
    @FXML
    private void timKiemSanPham() {
        String keyword = txtTimKiem.getText();
        ObservableList<Product> list = FXCollections.observableArrayList();
        String sql = "SELECT * FROM sanpham WHERE name LIKE ?";

        try (Connection conn = Database.getConnection();
             PreparedStatement pst = conn.prepareStatement(sql)) {

            pst.setString(1, "%" + keyword + "%");
            ResultSet rs = pst.executeQuery();
            while (rs.next()) {
                list.add(new Product(
                        rs.getString("id"),
                        rs.getString("categoryId"),
                        rs.getString("name"),
                        rs.getString("unit"),
                        rs.getString("price")
                ));
            }

            tableSanPham.setItems(list);
            lblTongSP.setText("Tổng sản phẩm: " + list.size());

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // ------------------ Xóa trắng các TextField ------------------
    private void clearFields() {
        txtId.clear();
        txtCategoryId.clear();
        txtName.clear();
        txtUnit.clear();
        txtPrice.clear();
        txtId.setEditable(true);
        isEditing = false;
    }
}
