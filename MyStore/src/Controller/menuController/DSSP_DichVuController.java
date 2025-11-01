package Controller.menuController;

import Database.Database;
import Model.Product;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.*;

public class DSSP_DichVuController {

    @FXML
    private TableView<Product> tableSanPham;

    @FXML
    private TableColumn<Product, String> colId, colCategoryId, colName;
    @FXML
    private TableColumn<Product, Double> colPrice;
    @FXML
    private TableColumn<Product, Integer> colStock;

    @FXML
    private TextField txtId, txtCategoryId, txtName, txtPrice, txtStock, txtTimKiem;

    @FXML
    private Label lblTongSP;

    private ObservableList<Product> productList = FXCollections.observableArrayList();
    private boolean isEditing = false;

    // ------------------ Khởi tạo TableView ------------------
    @FXML
    public void initialize() {
        colId.setCellValueFactory(cell -> cell.getValue().idProperty());
        colCategoryId.setCellValueFactory(cell -> cell.getValue().categoryIdProperty());
        colName.setCellValueFactory(cell -> cell.getValue().nameProperty());
        colPrice.setCellValueFactory(cell -> cell.getValue().priceProperty().asObject()); // double
        colStock.setCellValueFactory(cell -> cell.getValue().stockProperty().asObject()); // int

        loadProducts();
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
                        rs.getDouble("price"),  // price trước
                        rs.getInt("stock")      // stock sau
                ));
            }

            tableSanPham.setItems(productList);
            lblTongSP.setText("Tổng sản phẩm: " + productList.size());

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    //  Thêm sản phẩm
    @FXML
    private void themSanPham() {
        try {
            String id = txtId.getText();
            String categoryId = txtCategoryId.getText();
            String name = txtName.getText();
            double price = Double.parseDouble(txtPrice.getText());
            int stock = Integer.parseInt(txtStock.getText());

            String sql = "INSERT INTO sanpham(id, categoryId, name, price, stock) VALUES (?, ?, ?, ?, ?)";

            try (Connection conn = Database.getConnection();
                 PreparedStatement pst = conn.prepareStatement(sql)) {

                pst.setString(1, id);
                pst.setString(2, categoryId);
                pst.setString(3, name);
                pst.setDouble(4, price);
                pst.setInt(5, stock);

                int result = pst.executeUpdate();
                if (result > 0) {
                    loadProducts();
                    clearFields();
                    System.out.println("Thêm sản phẩm thành công");
                }

            }
        } catch (NumberFormatException e) {
            showAlert("Vui lòng nhập đúng định dạng số cho giá và số lượng!");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Sửa sản phẩm

    @FXML
    private void suaSanPham() {
        Product selected = tableSanPham.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("Vui lòng chọn sản phẩm để sửa!");
            return;
        }

        if (!isEditing) {
            txtId.setText(selected.getId());
            txtId.setEditable(false);
            txtCategoryId.setText(selected.getCategoryId());
            txtName.setText(selected.getName());
            txtPrice.setText(String.valueOf(selected.getPrice()));
            txtStock.setText(String.valueOf(selected.getStock()));

            isEditing = true;
            showAlert("Nhập lại thông tin mới");
        } else {
            try {
                String categoryId = txtCategoryId.getText();
                String name = txtName.getText();
                double price = Double.parseDouble(txtPrice.getText());
                int stock = Integer.parseInt(txtStock.getText());

                String sql = "UPDATE sanpham SET categoryId=?, name=?, price=?, stock=? WHERE id=?";

                try (Connection conn = Database.getConnection();
                     PreparedStatement pst = conn.prepareStatement(sql)) {

                    pst.setString(1, categoryId);
                    pst.setString(2, name);
                    pst.setDouble(3, price);
                    pst.setInt(4, stock);
                    pst.setString(5, selected.getId());

                    int result = pst.executeUpdate();
                    if (result > 0) {
                        selected.setCategoryId(categoryId);
                        selected.setName(name);
                        selected.setPrice(price);
                        selected.setStock(stock);
                        tableSanPham.refresh();
                        clearFields();
                        System.out.println("Sửa sản phẩm thành công");
                    }
                }
            } catch (NumberFormatException e) {
                showAlert("Giá hoặc số lượng không hợp lệ!");
            } catch (SQLException e) {
                e.printStackTrace();
            }
            isEditing = false;
        }
    }

    // Xóa sản phẩm

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

    //  Tìm kiếm sản phẩm

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
                        rs.getDouble("price"),
                        rs.getInt("stock")
                ));
            }

            tableSanPham.setItems(list);
            lblTongSP.setText("Tổng sản phẩm: " + list.size());

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void moTimKiemNangCao() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/View/menuView/SearchProduct.fxml"));
            Parent root = loader.load();

            SearchProductController controller = loader.getController();

            //  Gán callback để cập nhật TableView khi tìm kiếm xong
            controller.setOnSearchComplete(products -> {
                tableSanPham.setItems(products);
            });

            Stage stage = new Stage();
            stage.setTitle("Tìm kiếm nâng cao");
            stage.setScene(new Scene(root));
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }



    // Xóa trắng TextField
    private void clearFields() {
        txtId.clear();
        txtCategoryId.clear();
        txtName.clear();
        txtPrice.clear();
        txtStock.clear();
        txtId.setEditable(true);
        isEditing = false;
    }

    //  Thông báo
    private void showAlert(String msg) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }
}
