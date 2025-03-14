package se.sundsvall.businessinformation.api;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import generated.se.sundsvall.forratt.Anlaggningar;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import se.sundsvall.businessinformation.service.ecos.FacilityService;

@ExtendWith(MockitoExtension.class)
class FacilitiesResourceTest {

	@Mock
	private FacilityService service;

	@InjectMocks
	private FacilitiesResource resource;

	@Test
	void getAnlaggningar() {

		final var municipalityId = "2281";

		when(service.getFacilities(any(String.class))).thenReturn(List.of(
			new Anlaggningar()
				.anlaggningsid("someAnlaggningsid")
				.anlaggningsnamn("someAnläggningsnamn")
				.gatuadress("someGatuadress")
				.ort("someOrt")));

		final var result = resource.getAnlaggningar(municipalityId, "someOrgNr");

		assertThat(result.getStatusCode().is2xxSuccessful()).isTrue();
		assertThat(result.getBody()).isNotNull();
		// We ignore huvudsakliginriktning since ECOS cannot deliver huvudsakliginriktning/main
		// orientation at this given time.
		assertThat(result.getBody().getFirst()).hasNoNullFieldsOrPropertiesExcept("huvudsakliginriktning");

		verify(service).getFacilities(any(String.class));
		verifyNoMoreInteractions(service);
	}
}
