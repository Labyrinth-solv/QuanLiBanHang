package Model;

import javafx.beans.property.*;

public class KhachHang {
    private final IntegerProperty id = new SimpleIntegerProperty();
    private final StringProperty ten = new SimpleStringProperty();
    private final StringProperty sdt = new SimpleStringProperty();
    private final IntegerProperty diem = new SimpleIntegerProperty();

    public KhachHang(int id, String ten, String sdt, int diem) {
        this.id.set(id);
        this.ten.set(ten);
        this.sdt.set(sdt);
        this.diem.set(diem);
    }

    // getter/setter
    public int getId() { return id.get(); }
    public String getTen() { return ten.get(); }
    public String getSdt() { return sdt.get(); }
    public int getDiem() { return diem.get(); }

    public void setTen(String value) { ten.set(value); }
    public void setSdt(String value) { sdt.set(value); }
    public void setDiem(int value) { diem.set(value); }

    // property
    public IntegerProperty idProperty() { return id; }
    public StringProperty tenProperty() { return ten; }
    public StringProperty sdtProperty() { return sdt; }
    public IntegerProperty diemProperty() { return diem; }
}
