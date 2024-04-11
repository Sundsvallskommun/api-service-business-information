package se.sundsvall.businessinformation.api;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import se.sundsvall.businessinformation.service.ecos.FacilityService;

import generated.se.sundsvall.forratt.Anlaggning;
import generated.se.sundsvall.forratt.Faktura;
import generated.se.sundsvall.forratt.Livsmedelsverksamhet;

@ExtendWith(MockitoExtension.class)
class FacilityResourceTest {

	@Mock
	FacilityService service;

	@InjectMocks
	FacilityResource resource;

	@Test
	void getAnlaggning() {

		when(service.getFacility(any(String.class))).thenReturn(new Anlaggning());

		final var result = resource.getAnlaggning("someAnlaggningsId");

		assertThat(result).isNotNull();

		verify(service, times(1)).getFacility(any(String.class));
		verifyNoMoreInteractions(service);
	}

	@Test
	void getLivsmedelsverksamhet() {

		when(service.getLivsmedelsverksamhet(any(String.class))).thenReturn(new Livsmedelsverksamhet());

		final var result = resource.getLivsmedelsverksamhet("someAnlaggningsId");

		assertThat(result).isNotNull();

		verify(service, times(1)).getLivsmedelsverksamhet(any(String.class));
		verifyNoMoreInteractions(service);
	}


	@Test
	void getFakturering() {

		when(service.getFakturering(any(String.class))).thenReturn(new Faktura());

		final var result = resource.getFakturering("someAnlaggningsId");

		assertThat(result).isNotNull();

		verify(service, times(1)).getFakturering(any(String.class));
		verifyNoMoreInteractions(service);
	}

}
