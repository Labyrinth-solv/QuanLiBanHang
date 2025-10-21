package Model;

import javafx.beans.property.*;
import java.time.LocalDateTime;

public class HoaDon {
    private final IntegerProperty maHD = new SimpleIntegerProperty();
    private final ObjectProperty<LocalDateTime> ngayLap = new SimpleObjectProperty<>();
    private final DoubleProperty tongTien = new SimpleDoubleProperty();

    public HoaDon(int maHD, LocalDateTime ngayLap, double tongTien) {
        this.maHD.set(maHD);
        this.ngayLap.set(ngayLap);
        this.tongTien.set(tongTien);
    }

    public int getMaHD() { return maHD.get(); }
    public IntegerProperty maHDProperty() { return maHD; }

    public LocalDateTime getNgayLap() { return ngayLap.get(); }
    public ObjectProperty<LocalDateTime> ngayLapProperty() { return ngayLap; }

    public double getTongTien() { return tongTien.get(); }
    public DoubleProperty tongTienProperty() { return tongTien; }
}
