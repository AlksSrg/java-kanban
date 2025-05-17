package adapters;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import tasks.Task;

import java.io.IOException;
import java.time.LocalDateTime;

//Регистрируем пользовательский адаптер для обработки сериализации и десериализации данных типа LocalDateTimeAdapter
public class LocalDateTimeAdapter extends TypeAdapter<LocalDateTime> {

    @Override
    public void write(JsonWriter writer, LocalDateTime value) throws IOException {
        if (value == null) {
            writer.nullValue();
        } else {
            writer.value(value.format(Task.dateTimeFormatter)); // Используем формат даты и времени из класса Task
        }
    }

    @Override
    public LocalDateTime read(JsonReader reader) throws IOException {
        return LocalDateTime.parse(reader.nextString(), Task.dateTimeFormatter); // Парсим строку обратно в LocalDateTime
    }
}