package namnq.db;

import androidx.room.TypeConverter;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.List;

public class SoundConverters {

    @TypeConverter
    public String fromStringList(List<String> list) {
        return new Gson().toJson(list);
    }

    @TypeConverter
    public List<String> toStringList(String json) {
        Type listType = new TypeToken<List<String>>() {
        }.getType();
        return new Gson().fromJson(json, listType);
    }
}
