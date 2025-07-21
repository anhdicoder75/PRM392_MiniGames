package namnq.model;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "SoundCategories")
public class SoundCategory {

    @PrimaryKey(autoGenerate = true)
    public int id;

    @ColumnInfo(name = "name")
    public String name;

    @ColumnInfo(name = "description")
    public String description;

    // 🔥 THÊM DEFAULT CONSTRUCTOR (Required by Room)
    public SoundCategory() {
    }

    // Constructor với parameters (giữ nguyên)
    public SoundCategory(String name, String description) {
        this.name = name;
        this.description = description;
    }

    // 🆕 Thêm getters/setters để dễ debug
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return "SoundCategory{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                '}';
    }
}