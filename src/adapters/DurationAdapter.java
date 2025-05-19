package adapters;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.time.Duration;

//Регистрируем пользовательский адаптер для обработки сериализации и десериализации данных типа Duration
public class DurationAdapter extends TypeAdapter<Duration> {

    @Override
    public void write(JsonWriter out, Duration value) throws IOException {
        if (value == null) {
            out.nullValue();
        } else {
            out.value(value.toString()); // Используем строковое представление длительности
        }
    }

    @Override
    public Duration read(JsonReader in) throws IOException {
        String durationStr = in.nextString();
        return Duration.parse(durationStr); // Восстанавливаем длительность из строки
    }
}