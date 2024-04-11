package se.sundsvall.businessinformation.util;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;

import org.junit.jupiter.api.Test;

class LocalDateAdapterTest {


	private final LocalDateAdapter adapter = new LocalDateAdapter();

	@Test
	void testUnmarshal() {
		// Arrange
		final var dateString = "2022-01-01";

		// Act
		final var result = adapter.unmarshal(dateString);

		// Assert
		assertEquals(LocalDate.of(2022, 1, 1), result);
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
		final var faultyDateString = "2022-13-01";

		// Act and Assert
		assertThatThrownBy(() -> adapter.unmarshal(faultyDateString)).isInstanceOf(DateTimeParseException.class);

	}

	@Test
	void testMarshal() {
		// Arrange
		final var date = LocalDate.of(2022, 1, 1);

		// Act
		final var result = adapter.marshal(date);

		// Assert
		assertEquals("2022-01-01", result);
	}

	@Test
	void testMarshalNull() {

		//  Act
		final var result = adapter.marshal(null);

		// Assert
		assertNull(result);
	}

}
