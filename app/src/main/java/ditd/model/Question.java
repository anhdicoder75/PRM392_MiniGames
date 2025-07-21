package ditd.model;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;

import java.util.List;

@Entity(
        tableName = "ImageGameQuestions",
        foreignKeys = @ForeignKey(
                entity = Category.class,
                parentColumns = "id",
                childColumns = "category_id",
                onDelete = ForeignKey.CASCADE
        ),
        indices = {@Index("category_id")}
)
public class Question {
    @PrimaryKey(autoGenerate = true)
    public int id;
    @ColumnInfo(name="images")


    public List<String> imageUrls;
    @ColumnInfo(name="options")

    public List<String> options;
    @ColumnInfo(name="correct_answer")

    public String correctAnswer;
    @ColumnInfo(name = "category_id")
    public int categoryId;
    @ColumnInfo(name="difficulty")

    public int difficulty;
    @ColumnInfo(name="hint")

    public String hint;
    @ColumnInfo(name="explanation")

    public String explanation;

    @ColumnInfo(name="time_limit")
    public int timeLimitSeconds;

}
