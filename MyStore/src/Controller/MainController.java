package Controller;

import java.io.IOException;
import java.net.URL;
import java.util.Objects;
import java.util.ResourceBundle;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;


import javafx.scene.control.*;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class MainController implements Initializable {

    @FXML
    private TextField txtUserName, txtPassword;

    @FXML
    private Button btnDangNhap, btnThoat;

    @FXML
    private Label txtLoiDangNhap;

    @Override
    public void initialize(URL arg0, ResourceBundle arg1) {
        addControls();
        addEvents();
    }

    private void addControls() {

    }

    private void addEvents() {
        txtUserName.addEventHandler(KeyEvent.KEY_PRESSED, e -> {
            if (e.getCode() == KeyCode.ENTER) {
                login();
            }
        });

        txtPassword.addEventHandler(KeyEvent.KEY_PRESSED, e -> {
            if (e.getCode() == KeyCode.ENTER) {
                login();
            }
        });
    }

    @FXML
    public void dangNhap(ActionEvent e) {
        login();
    }

    @FXML
    public void thoat(ActionEvent e) {
        exit(e);
    }

    private void login() {
        String loiDangNhap = "";

        // 1️⃣ Kiểm tra trống
        String username = txtUserName.getText().trim();
        String password = txtPassword.getText().trim();

        if (username.isEmpty()) {
            loiDangNhap += "Tên đăng nhập không được để trống\n";
        }
        if (password.isEmpty()) {
            loiDangNhap += "Mật khẩu không được để trống\n";
        }

        // 2️⃣ Nếu không lỗi nhập liệu, kiểm tra database
        if (loiDangNhap.isEmpty()) {
            String url = "jdbc:mysql://localhost:3306/mydata"; // tên database
            String dbUser = "root"; // user MySQL của bạn
            String dbPass = "17012005"; // mật khẩu MySQL

            try (Connection conn = DriverManager.getConnection(url, dbUser, dbPass)) {
                String sql = "SELECT * FROM user WHERE account = ? AND password = ?";
                try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                    stmt.setString(1, username);
                    stmt.setString(2, password);

                    ResultSet rs = stmt.executeQuery();
                    if (rs.next()) {
                        // ✅ Đăng nhập thành công
                        Constants.staffName = username;
                    } else {
                        loiDangNhap = "Sai tên đăng nhập hoặc mật khẩu!";
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                loiDangNhap = "Lỗi kết nối đến cơ sở dữ liệu!";
            }
        }

        // 3️⃣ Nếu có lỗi thì báo ra và reset input
        if (!loiDangNhap.isEmpty()) {
            txtLoiDangNhap.setText(loiDangNhap);
            txtUserName.setText("");
            txtPassword.setText("");
            txtUserName.requestFocus();
            return;
        }

        // 4️⃣ Nếu đúng thì chuyển sang màn hình chính
        Stage stage = (Stage) btnDangNhap.getScene().getWindow();
        stage.close();

        Stage manHinhChinh = new Stage();
        try {
            Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/View/MainScreen.fxml")));
            Scene scene = new Scene(root, 1300, 690);
            manHinhChinh.setScene(scene);
            manHinhChinh.initModality(Modality.APPLICATION_MODAL);
            manHinhChinh.show();
            manHinhChinh.setTitle("MainMenu");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private void exit(ActionEvent e) {
        Platform.exit();
    }

}