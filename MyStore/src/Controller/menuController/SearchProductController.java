package Controller.menuController;

import Database.Database;
import Model.Product;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.function.Consumer;

public class SearchProductController {

    @FXML private ComboBox<String> cbCategory;
    @FXML private Slider priceSlider;
    @FXML private Label lblMinPrice;
    @FXML private Label lblMaxPrice;
    @FXML private ComboBox<String> cbStock;

    private ObservableList<Product> searchResult = FXCollections.observableArrayList();

    // ✅ callback để gửi kết quả về ThanhToanController
    private Consumer<ObservableList<Product>> onSearchComplete;

    public void setOnSearchComplete(Consumer<ObservableList<Product>> callback) {
        this.onSearchComplete = callback;
    }

    @FXML
    private void initialize() {
        loadCategories();
        cbStock.setItems(FXCollections.observableArrayList("Tất cả", "Còn hàng", "Hết hàng"));
        cbStock.setValue("Tất cả");

        priceSlider.valueProperty().addListener((obs, oldVal, newVal) -> {
            lblMinPrice.setText("0₫");
            lblMaxPrice.setText(String.format("%,.0f₫", newVal.doubleValue()));
        });
    }

    private void loadCategories() {
        ObservableList<String> categories = FXCollections.observableArrayList();
        try (Connection conn = Database.getConnection();
             PreparedStatement pst = conn.prepareStatement("SELECT DISTINCT categoryId FROM sanpham");
             ResultSet rs = pst.executeQuery()) {
            while (rs.next()) {
                categories.add(rs.getString("categoryId"));
            }
            cbCategory.setItems(categories);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void search() {
        String sql = "SELECT * FROM sanpham WHERE 1=1";
        ObservableList<Object> params = FXCollections.observableArrayList();

        if (cbCategory.getValue() != null && !cbCategory.getValue().isEmpty()) {
            sql += " AND categoryId = ?";
            params.add(cbCategory.getValue());
        }

        sql += " AND price <= ?";
        params.add(priceSlider.getValue());

        if (cbStock.getValue() != null && !cbStock.getValue().equals("Tất cả")) {
            if (cbStock.getValue().equals("Còn hàng"))
                sql += " AND stock > 0";
            else
                sql += " AND stock = 0";
        }

        try (Connection conn = Database.getConnection();
             PreparedStatement pst = conn.prepareStatement(sql)) {

            for (int i = 0; i < params.size(); i++)
                pst.setObject(i + 1, params.get(i));

            ResultSet rs = pst.executeQuery();
            searchResult.clear();

            while (rs.next()) {
                searchResult.add(new Product(
                        rs.getString("id"),
                        rs.getString("categoryId"),
                        rs.getString("name"),
                        rs.getDouble("price"),
                        rs.getInt("stock")
                ));
            }

            // ✅ Gửi kết quả về controller chính trước khi đóng
            if (onSearchComplete != null) {
                onSearchComplete.accept(searchResult);
            }

            // ✅ Đóng cửa sổ tìm kiếm
            Stage stage = (Stage) cbCategory.getScene().getWindow();
            stage.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
