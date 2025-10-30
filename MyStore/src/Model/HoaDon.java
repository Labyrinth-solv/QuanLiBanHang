package Model;

import javafx.beans.property.*;

import java.time.LocalDateTime;

public class HoaDon {
    private final IntegerProperty maHD;
    private final ObjectProperty<LocalDateTime> ngayLap;
    private final DoubleProperty tongTien;
    private final StringProperty SDT;

    public HoaDon(int maHD, LocalDateTime ngayLap, double tongTien, String sdt) {
        this.maHD = new SimpleIntegerProperty(maHD);
        this.ngayLap = new SimpleObjectProperty<>(ngayLap);
        this.tongTien = new SimpleDoubleProperty(tongTien);
        this.SDT = new SimpleStringProperty(sdt);
    }

    public int getMaHD() { return maHD.get(); }
    public IntegerProperty maHDProperty() { return maHD; }

    public LocalDateTime getNgayLap() { return ngayLap.get(); }
    public ObjectProperty<LocalDateTime> ngayLapProperty() { return ngayLap; }

    public double getTongTien() { return tongTien.get(); }
    public DoubleProperty tongTienProperty() { return tongTien; }

    public String getSDT() { return SDT.get(); }
    public StringProperty SDTProperty() { return SDT; }
}
