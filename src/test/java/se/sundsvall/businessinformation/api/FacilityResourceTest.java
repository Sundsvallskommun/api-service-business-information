package se.sundsvall.businessinformation.api;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import generated.se.sundsvall.forratt.Anlaggning;
import generated.se.sundsvall.forratt.Faktura;
import generated.se.sundsvall.forratt.Livsmedelsverksamhet;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import se.sundsvall.businessinformation.service.ecos.FacilityService;

@ExtendWith(MockitoExtension.class)
class FacilityResourceTest {

	@Mock
	private FacilityService service;

	@InjectMocks
	private FacilityResource resource;

	@Test
	void getAnlaggning() {

		final var municipalityId = "2281";

		when(service.getFacility(any(String.class))).thenReturn(new Anlaggning());

		final var result = resource.getAnlaggning(municipalityId, "someAnlaggningsId");

		assertThat(result).isNotNull();

		verify(service).getFacility(any(String.class));
		verifyNoMoreInteractions(service);
	}

	@Test
	void getLivsmedelsverksamhet() {

		final var municipalityId = "2281";

		when(service.getLivsmedelsverksamhet(any(String.class))).thenReturn(new Livsmedelsverksamhet());

		final var result = resource.getLivsmedelsverksamhet(municipalityId, "someAnlaggningsId");

		assertThat(result).isNotNull();

		verify(service).getLivsmedelsverksamhet(any(String.class));
		verifyNoMoreInteractions(service);
	}

	@Test
	void getFakturering() {

		final var municipalityId = "2281";

		when(service.getFakturering(any(String.class))).thenReturn(new Faktura());

		final var result = resource.getFakturering(municipalityId, "someAnlaggningsId");

		assertThat(result).isNotNull();

		verify(service).getFakturering(any(String.class));
		verifyNoMoreInteractions(service);
	}
}
