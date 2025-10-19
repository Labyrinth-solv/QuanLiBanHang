package Controller.menuController;

import javafx.fxml.FXML;
import javafx.scene.control.*;

public class DSSP_DichVuController {

    @FXML
    private TableView<?> tableSanPham;

    @FXML
    private TableColumn<?, ?> colMaSP, colTenSP, colLoai, colGia, colSoLuong;

    @FXML
    private TextField txtTimKiem;

    @FXML
    private Label lblTongSP;

    // Khi nhấn nút "Tìm"
    @FXML
    private void timKiemSanPham() {
        System.out.println("Đang tìm sản phẩm: " + txtTimKiem.getText());
    }

    @FXML
    private void themSanPham() {
        System.out.println("Thêm sản phẩm mới");
    }

    @FXML
    private void suaSanPham() {
        System.out.println("Sửa sản phẩm được chọn");
    }

    @FXML
    private void xoaSanPham() {
        System.out.println("Xóa sản phẩm được chọn");
    }
}

