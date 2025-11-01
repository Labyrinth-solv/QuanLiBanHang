package Model;

import javafx.beans.property.*;
import java.time.LocalDate;

public class Employee {

    private final IntegerProperty id = new SimpleIntegerProperty();
    private final StringProperty name = new SimpleStringProperty();
    private final ObjectProperty<LocalDate> birth = new SimpleObjectProperty<>();
    private final StringProperty gender = new SimpleStringProperty();
    private final StringProperty shift = new SimpleStringProperty();
    private final DoubleProperty salary = new SimpleDoubleProperty();

    //  Constructors
    public Employee() {}

    public Employee(int id, String name, String gender, LocalDate birth, String shift, double salary) {
        this.id.set(id);
        this.name.set(name);
        this.gender.set(gender);
        this.birth.set(birth);
        this.shift.set(shift);
        this.salary.set(salary);
    }

    //  ID
    public int getId() { return id.get(); }
    public void setId(int value) { id.set(value); }
    public IntegerProperty idProperty() { return id; }

    //  Name
    public String getName() { return name.get(); }
    public void setName(String value) { name.set(value); }
    public StringProperty nameProperty() { return name; }

    //  Gender
    public String getGender() { return gender.get(); }
    public void setGender(String value) { gender.set(value); }
    public StringProperty genderProperty() { return gender; }

    // Birth
    public LocalDate getBirth() { return birth.get(); }
    public void setBirth(LocalDate value) { birth.set(value); }
    public ObjectProperty<LocalDate> birthProperty() { return birth; }

    //  Shift
    public String getShift() { return shift.get(); }
    public void setShift(String value) { shift.set(value); }
    public StringProperty shiftProperty() { return shift; }

    // Salary
    public double getSalary() { return salary.get(); }
    public void setSalary(double value) { salary.set(value); }
    public DoubleProperty salaryProperty() { return salary; }

    @Override
    public String toString() {
        return String.format("[%d] %s - %s - %s - %s - %.0f",
                getId(), getName(), getGender(),
                getBirth(), getShift(), getSalary());
    }
}
