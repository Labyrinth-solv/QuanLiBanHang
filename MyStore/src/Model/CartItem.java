package Model;

import javafx.beans.property.*;

public class CartItem {
    private final StringProperty name = new SimpleStringProperty();
    private final IntegerProperty soLuong = new SimpleIntegerProperty();
    private final DoubleProperty donGia = new SimpleDoubleProperty();
    private final DoubleProperty thanhTien = new SimpleDoubleProperty();

    public CartItem(String name, int soLuong, double donGia) {
        this.name.set(name);
        this.soLuong.set(soLuong);
        this.donGia.set(donGia);
        this.thanhTien.set(soLuong * donGia);
    }

    public String getName() { return name.get(); }
    public StringProperty nameProperty() { return name; }

    public int getSoLuong() { return soLuong.get(); }
    public IntegerProperty soLuongProperty() { return soLuong; }

    public double getDonGia() { return donGia.get(); }
    public DoubleProperty donGiaProperty() { return donGia; }

    public double getThanhTien() { return thanhTien.get(); }
    public DoubleProperty thanhTienProperty() { return thanhTien; }
}
