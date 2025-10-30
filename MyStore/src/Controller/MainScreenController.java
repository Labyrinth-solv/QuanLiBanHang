package Controller;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Objects;
import java.util.ResourceBundle;

import Controller.menuController.KhachHangFormController;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Cursor;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.SingleSelectionModel;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.input.MouseEvent;
import javafx.scene.text.Text;

public class MainScreenController implements Initializable {

    /**
     * Khai bao
     */
    @FXML
    private TabPane tab;
    private SingleSelectionModel<Tab> selectionModel;
    private static int flagForTab = -1;
    private static int flagForDSSP_DichVu, flagForHoaDon,
            flagForDSNhanVien, flagForThemNhanVien, flagForDoanhThu, flagForDSHoaDon, flagForDSKhachHang;

    @FXML
    private Button btnDanhSachSanPham_DichVu, btnHoaDon_DichVu, btnDanhSach_NhanVien, btnThemNhanVien, btnBaoCaoDoanhThu,
            btnDanhSachHoaDon, btnDanhSachKhachHang;

    @FXML
    private Text txtNgay, txtTenNhanVien;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        addControls();
        addEvents();
    }

    private void addControls() {
        // Khoi tao tat ca cac tab deu la -1 - nghia la chua tab nao duoc chon
        selectionModel = tab.getSelectionModel();
        flagForDSSP_DichVu = -1;
        flagForHoaDon = -1;
        flagForDSNhanVien = -1;
        flagForThemNhanVien = -1;
        flagForDoanhThu = -1;
        flagForDSHoaDon = -1;
        flagForDSKhachHang=-1;

        // Tien hanh cap nhat ngay thang va ho ten nhan vien
        LocalDate localDate = LocalDate.now();
        String ngay = localDate.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
        txtNgay.setText(ngay);

        txtTenNhanVien.setText(Constants.staffName);

//        btnDanhSachSanPham_DichVu.setDisable(true);
//        btnThemNhanVien.setDisable(true);

        if (5== Constants.accountID) {
            btnThemNhanVien.setDisable(true);
            btnDanhSach_NhanVien.setDisable(true);
            btnBaoCaoDoanhThu.setDisable(true);
            btnDanhSachHoaDon.setDisable(true);
        } else if (6== Constants.accountID) {
            btnDanhSachSanPham_DichVu.setDisable(true);
            btnHoaDon_DichVu.setDisable(true);
            btnThemNhanVien.setDisable(true);
            btnDanhSach_NhanVien.setDisable(true);
            btnBaoCaoDoanhThu.setDisable(true);
            btnDanhSachHoaDon.setDisable(true);
        }

    }

    private void addEvents() {
        btnDanhSachSanPham_DichVu.setOnMouseClicked(e -> {
            try {
                hienThiDSSP_DichVu();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        });

        btnDanhSachSanPham_DichVu.setOnMouseEntered(this::mouseEnter);

        btnDanhSachSanPham_DichVu.setOnMouseExited(this::mouseExit);

        btnHoaDon_DichVu.setOnMouseClicked(e -> {
            try {
                hienThiHoaDon();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        });

        btnHoaDon_DichVu.setOnMouseEntered(this::mouseEnter);

        btnHoaDon_DichVu.setOnMouseExited(this::mouseExit);


        btnDanhSach_NhanVien.setOnMouseClicked(e -> {
            try {
                hienThiDSNhanVien();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        });

        btnDanhSach_NhanVien.setOnMouseEntered(this::mouseEnter);

        btnDanhSach_NhanVien.setOnMouseExited(this::mouseExit);

        btnThemNhanVien.setOnMouseClicked(e -> {
            try {
                hienThiThemNhanVien();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        });

        btnThemNhanVien.setOnMouseEntered(this::mouseEnter);

        btnThemNhanVien.setOnMouseExited(this::mouseExit);

        btnDanhSachKhachHang.setOnMouseClicked(e -> {
            try {
                hienThiDSKhachHang();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        });

        btnDanhSachKhachHang.setOnMouseEntered(this::mouseEnter);

        btnDanhSachKhachHang.setOnMouseExited(this::mouseExit);


        btnBaoCaoDoanhThu.setOnMouseClicked(e -> {
            try {
                hienThiBaoCaoDoanhThu();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        });

        btnBaoCaoDoanhThu.setOnMouseEntered(this::mouseEnter);

        btnBaoCaoDoanhThu.setOnMouseExited(this::mouseExit);

        btnDanhSachHoaDon.setOnMouseClicked(e -> {
            try {
                hienThiDSHoaDon();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        });

        btnDanhSachHoaDon.setOnMouseEntered(this::mouseEnter);

        btnDanhSachHoaDon.setOnMouseExited(this::mouseExit);
    }

    /**
     * Xu ly hien thi cac man hinh rieng biet len cac tab
     * @throws IOException
     */

    private void hienThiDSHoaDon() throws IOException {
        if(flagForDSHoaDon == -1) {
            Tab tab1 = new Tab();
            tab1.setText("Danh sách hóa đơn");

            Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/View/menuView/DSHoaDon.fxml")));
            tab1.setContent(root);
            tab.getTabs().add(tab1);

            flagForTab++;
            flagForDSHoaDon = flagForTab;

            tab1.setOnCloseRequest(e->{
                flagForTab --;
                flagForDSHoaDon = -1;
            });
        }

        selectionModel.select(flagForDSHoaDon);

    }
    private void hienThiBaoCaoDoanhThu() throws IOException {
        if(flagForDoanhThu == -1) {
            Tab tab1 = new Tab();
            tab1.setText("Báo cáo doanh thu");

            Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/View/menuView/DoanhThu.fxml")));
            tab1.setContent(root);
            tab.getTabs().add(tab1);

            flagForTab++;
            flagForDoanhThu = flagForTab;

            tab1.setOnCloseRequest(e->{
                flagForTab --;
                flagForDoanhThu = -1;
            });
        }

        selectionModel.select(flagForDoanhThu);

    }

    private void hienThiThemNhanVien() throws IOException {
        if(flagForThemNhanVien == -1) {
            Tab tab1 = new Tab();
            tab1.setText("Thêm nhân viên");

            Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/View/menuView/AddEmployee.fxml")));
            tab1.setContent(root);
            tab.getTabs().add(tab1);

            flagForTab++;
            flagForThemNhanVien = flagForTab;

            tab1.setOnCloseRequest(e->{
                flagForTab --;
                flagForThemNhanVien = -1;
            });
        }

        selectionModel.select(flagForThemNhanVien);

    }

    private void hienThiDSNhanVien() throws IOException {
        if(flagForDSNhanVien == -1) {
            Tab tab1 = new Tab();
            tab1.setText("Danh sách nhân viên");

            Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/View/menuView/EmployeeList.fxml")));
            tab1.setContent(root);
            tab.getTabs().add(tab1);

            flagForTab++;
            flagForDSNhanVien = flagForTab;

            tab1.setOnCloseRequest(e->{
                flagForTab --;
                flagForDSNhanVien = -1;
            });
        }

        selectionModel.select(flagForDSNhanVien);

    }
    private void hienThiDSKhachHang() throws IOException {
        if(flagForDSKhachHang == -1) {
            Tab tab1 = new Tab();
            tab1.setText("Danh sách khách hàng");

            // Tạo FXMLLoader riêng
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/View/menuView/KhachHangForm.fxml"));
            Parent root = loader.load();

            // Lấy controller
            KhachHangFormController controller = loader.getController();

            // Gọi method để ẩn nút thêm khách hàng và hủy double click
            controller.disableAddButton();
            controller.disableDoubleClickSelect();

            tab1.setContent(root);
            tab.getTabs().add(tab1);

            flagForTab++;
            flagForDSKhachHang = flagForTab;

            tab1.setOnCloseRequest(e -> {
                flagForTab--;
                flagForDSKhachHang = -1;
            });
        }

        selectionModel.select(flagForDSKhachHang);
    }


        // TƯƠNG ỨNG VỚI ThanhToan.fxml
    private void hienThiHoaDon() throws IOException {
        if (flagForHoaDon == -1) {
            Tab tab1 = new Tab();
            tab1.setText("Thanh toán"); // Tên tab hiển thị trên giao diện

            // ✅ Đường dẫn đến file FXML của giao diện thanh toán
            Parent root = FXMLLoader.load(Objects.requireNonNull(
                    getClass().getResource("/View/menuView/ThanhToan.fxml")
            ));
            tab1.setContent(root);
            tab.getTabs().add(tab1);

            flagForTab++;
            flagForHoaDon = flagForTab;

            tab1.setOnCloseRequest(e -> {
                flagForTab--;
                flagForHoaDon = -1;
            });
        }

        // ✅ Chuyển sang tab Thanh toán
        selectionModel.select(flagForHoaDon);
    }


    private void hienThiDSSP_DichVu() throws IOException {
        if(flagForDSSP_DichVu == -1) {
            Tab tab1 = new Tab();
            tab1.setText("Danh sách sản phẩm");

            Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/View/menuView/DSSP_DichVu.fxml")));
            tab1.setContent(root);
            tab.getTabs().add(tab1);

            flagForTab++;
            flagForDSSP_DichVu = flagForTab;

            tab1.setOnCloseRequest(e->{
                flagForTab --;
                flagForDSSP_DichVu = -1;
            });
        }

        selectionModel.select(flagForDSSP_DichVu);
    }

    /**
     * Xu ly mouse hover qua tung button
     *
     * @param e
     */
    private void mouseEnter(MouseEvent e) {
        Button btn = (Button) e.getSource();
        btn.setCursor(Cursor.HAND);
        btn.setStyle("-fx-background-color: #00CCFF");
    }

    private void mouseExit(MouseEvent e) {
        Button btn = (Button) e.getSource();
        btn.setStyle("-fx-background-color: transparent");
    }

}