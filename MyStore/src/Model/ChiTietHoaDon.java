package Model;

import javafx.beans.property.*;

public class ChiTietHoaDon {
    private final StringProperty tenSP;
    private final IntegerProperty soLuong;
    private final DoubleProperty donGia;

    public ChiTietHoaDon(String tenSP, int soLuong, double donGia) {
        this.tenSP = new SimpleStringProperty(tenSP);
        this.soLuong = new SimpleIntegerProperty(soLuong);
        this.donGia = new SimpleDoubleProperty(donGia);
    }

    // Property getters
    public StringProperty tenSPProperty() { return tenSP; }
    public IntegerProperty soLuongProperty() { return soLuong; }
    public DoubleProperty donGiaProperty() { return donGia; }

    // Normal getters
    public String getTenSP() { return tenSP.get(); }
    public int getSoLuong() { return soLuong.get(); }
    public double getDonGia() { return donGia.get(); }

    // Thành tiền tính động
    public double getThanhTien() {
        return getSoLuong() * getDonGia();
    }

    // Nếu muốn binding trực tiếp lên TableView
    public ReadOnlyDoubleWrapper thanhTienProperty() {
        return new ReadOnlyDoubleWrapper(getThanhTien());
    }
}
