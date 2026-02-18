package se.sundsvall.businessinformation.util;

import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class LocalDateTimeAdapterTest {

	private final LocalDateTimeAdapter adapter = new LocalDateTimeAdapter();

	@Test
	void testUnmarshal() {
		// Arrange
		final var dateTimeString = "2022-01-01T00:00:00";

		// Act
		final var result = adapter.unmarshal(dateTimeString);

		// Assert
		assertEquals(LocalDateTime.of(2022, 1, 1, 0, 0, 0), result);
	}

	@Test
	void testUnmarshalNull() {
		// Act
		final var result = adapter.unmarshal(null);

		// Assert
		assertNull(result);
	}

	@Test
	void testUnmarshalFaultyValue() {
		// Arrange
		final var faultyDateTimeString = "2022-01-01T25:00:00";

		// Act and Assert
		assertThatThrownBy(() -> adapter.unmarshal(faultyDateTimeString)).isInstanceOf(DateTimeParseException.class);
	}

	@Test
	void testMarshal() {
		// Arrange
		final var dateTime = LocalDateTime.of(2022, 1, 1, 0, 0, 0);

		// Act
		final var result = adapter.marshal(dateTime);

		// Assert
		assertEquals("2022-01-01T00:00:00", result);
	}

	@Test
	void testMarshalNull() {
		// Act
		final String result = adapter.marshal(null);

		// Assert
		assertNull(result);
	}
}
