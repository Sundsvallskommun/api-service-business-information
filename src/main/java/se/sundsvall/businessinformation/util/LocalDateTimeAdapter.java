package se.sundsvall.businessinformation.util;

import static java.time.format.DateTimeFormatter.ISO_DATE_TIME;
import static java.time.temporal.ChronoUnit.MICROS;

import jakarta.xml.bind.annotation.adapters.XmlAdapter;
import java.time.LocalDateTime;

public class LocalDateTimeAdapter extends XmlAdapter<String, LocalDateTime> {

	@Override
	public LocalDateTime unmarshal(final String s) {
		if (s == null) {
			return null;
		}

		return ISO_DATE_TIME.parse(s, LocalDateTime::from);
	}

	@Override
	public String marshal(final LocalDateTime localDateTime) {
		if (localDateTime == null) {
			return null;
		}

		return ISO_DATE_TIME.format(localDateTime.truncatedTo(MICROS));
	}
}
