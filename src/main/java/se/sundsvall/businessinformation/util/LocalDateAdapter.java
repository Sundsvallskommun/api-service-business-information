package se.sundsvall.businessinformation.util;

import static java.time.format.DateTimeFormatter.ISO_DATE;

import jakarta.xml.bind.annotation.adapters.XmlAdapter;
import java.time.LocalDate;

public class LocalDateAdapter extends XmlAdapter<String, LocalDate> {

	@Override
	public LocalDate unmarshal(final String s) {
		if (s == null) {
			return null;
		}

		return ISO_DATE.parse(s, LocalDate::from);
	}

	@Override
	public String marshal(final LocalDate localDate) {
		if (localDate == null) {
			return null;
		}

		return ISO_DATE.format(localDate);
	}
}
