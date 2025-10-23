package Model;

import javafx.beans.property.*;

public class DoanhThu {
    private final StringProperty ngay;
    private final IntegerProperty soHoaDon;
    private final DoubleProperty tongTien;

    public DoanhThu(String ngay, int soHoaDon, double tongTien) {
        this.ngay = new SimpleStringProperty(ngay);
        this.soHoaDon = new SimpleIntegerProperty(soHoaDon);
        this.tongTien = new SimpleDoubleProperty(tongTien);
    }

    public String getNgay() { return ngay.get(); }
    public int getSoHoaDon() { return soHoaDon.get(); }
    public double getTongTien() { return tongTien.get(); }

    public StringProperty ngayProperty() { return ngay; }
    public IntegerProperty soHoaDonProperty() { return soHoaDon; }
    public DoubleProperty tongTienProperty() { return tongTien; }
}
