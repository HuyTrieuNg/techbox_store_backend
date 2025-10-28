package vn.techbox.techbox_store.config.serializer;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class UTCLocalDateTimeDeserializer extends JsonDeserializer<LocalDateTime> {

    private static final DateTimeFormatter[] FORMATTERS = {
        DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"),
        DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss'Z'"),
        DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSXXX"),
        DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ssXXX"),
        DateTimeFormatter.ISO_OFFSET_DATE_TIME,
        DateTimeFormatter.ISO_LOCAL_DATE_TIME
    };

    @Override
    public LocalDateTime deserialize(JsonParser parser, DeserializationContext context) throws IOException {
        String dateString = parser.getText();

        if (dateString == null || dateString.trim().isEmpty()) {
            return null;
        }

        // Thử parse với các format khác nhau
        for (DateTimeFormatter formatter : FORMATTERS) {
            try {
                if (dateString.contains("Z") || dateString.contains("+") || dateString.contains("-")) {
                    OffsetDateTime offsetDateTime = OffsetDateTime.parse(dateString, formatter);
                    return offsetDateTime.atZoneSameInstant(java.time.ZoneOffset.UTC).toLocalDateTime();
                } else {
                    return LocalDateTime.parse(dateString, formatter);
                }
            } catch (DateTimeParseException e) {
                continue;
            }
        }

        throw new IOException("Unable to parse date: " + dateString);
    }
}
