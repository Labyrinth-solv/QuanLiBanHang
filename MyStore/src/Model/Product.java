package Model;

import javafx.beans.property.*;

public class Product {
    private final SimpleStringProperty id;
    private final SimpleStringProperty categoryId;
    private final SimpleStringProperty name;
    private final SimpleDoubleProperty price; //  giá tiền (số thực)
    private final SimpleIntegerProperty stock; // số lượng tồn (số nguyên)

    //  Constructor
    public Product(String id, String categoryId, String name, double price, int stock) {
        this.id = new SimpleStringProperty(id);
        this.categoryId = new SimpleStringProperty(categoryId);
        this.name = new SimpleStringProperty(name);
        this.price = new SimpleDoubleProperty(price);
        this.stock = new SimpleIntegerProperty(stock);
    }

    public Product() {
        this(null, null, null, 0.0, 0);
    }

    //  Getter (trả về giá trị cơ bản)
    public String getId() { return id.get(); }
    public String getCategoryId() { return categoryId.get(); }
    public String getName() { return name.get(); }
    public double getPrice() { return price.get(); }
    public int getStock() { return stock.get(); }

    //  Property (dùng cho TableView)
    public StringProperty idProperty() { return id; }
    public StringProperty categoryIdProperty() { return categoryId; }
    public StringProperty nameProperty() { return name; }
    public DoubleProperty priceProperty() { return price; }
    public IntegerProperty stockProperty() { return stock; }

    //  Setter
    public void setId(String id) { this.id.set(id); }
    public void setCategoryId(String categoryId) { this.categoryId.set(categoryId); }
    public void setName(String name) { this.name.set(name); }
    public void setPrice(double price) { this.price.set(price); }
    public void setStock(int stock) { this.stock.set(stock); }
}
