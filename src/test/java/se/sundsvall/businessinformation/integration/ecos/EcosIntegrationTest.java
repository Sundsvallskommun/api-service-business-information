package se.sundsvall.businessinformation.integration.ecos;

import java.util.List;
import minutmiljo.ArrayOfSearchFacilityResultSvcDto;
import minutmiljo.GetFacilityPartyRoles;
import minutmiljo.GetFoodFacilities;
import minutmiljo.GetParty;
import minutmiljo.SearchFacility;
import minutmiljo.SearchFacilityResponse;
import minutmiljo.SearchFacilityResultSvcDto;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.zalando.problem.ThrowableProblem;
import se.sundsvall.businessinformation.TestUtil;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EcosIntegrationTest {

	@Mock
	private EcosClient client;

	@InjectMocks
	private EcosIntegration integration;

	@ParameterizedTest()
	@ValueSource(strings = {
		"123456-7890", "12123456-7890"
	})
	void getFacilities(final String orgNr) {

		when(client.searchFacility(any(SearchFacility.class))).thenReturn(buildSearchFacilityResult());

		final var result = integration.getFacilities(orgNr);

		assertThat(result).isNotNull().hasSize(1);
		// We ignore huvudsakligInriktning since ECOS cannot deliver huvudsakligInriktning/main
		// orientation at this given time.
		assertThat(result.getFirst()).hasNoNullFieldsOrPropertiesExcept("huvudsakliginriktning");

		verify(client).searchFacility(any(SearchFacility.class));
		verifyNoMoreInteractions(client);
	}

	private SearchFacilityResponse buildSearchFacilityResult() {
		return new SearchFacilityResponse()
			.withSearchFacilityResult(new ArrayOfSearchFacilityResultSvcDto()
				.withSearchFacilityResultSvcDto(List
					.of(new SearchFacilityResultSvcDto()
						.withPlace("somePlace")
						.withFacilityName("someFacilityName")
						.withPostCode("somePostCode")
						.withFacilityId("someFacilityId")
						.withFacilityName("someFacilityName")
						.withFacilityStatusName("someFacilityStatusName")
						.withFacilityCollectionName("someFacilityCollectionName")
						.withFacilityTypeName("someFacilityTypeName")
						.withEstateDesignation("someEstateDesignation")
						.withPostalArea("somePostalArea")
						.withVistingAddress("someVisitingAdress"))));
	}

	@Test
	void getFacilities_withFaultyOrgNumber() {

		assertThatExceptionOfType(ThrowableProblem.class)
			.isThrownBy(() -> integration.getFacilities("someOrgNr"))
			.withMessage("Bad Request: organizationNumber must consist of 10 or 12 digits with the last four seperated with a hyphen");

		verifyNoInteractions(client);
	}

	@Test
	void getFacility() {

		when(client.getFoodFacilities(any())).thenReturn(TestUtil.getFoodFacilitiesResponse());
		when(client.searchFacility(any(SearchFacility.class))).thenReturn(TestUtil.searchFacilityResponse());
		when(client.getFacilityPartyRoles(any(GetFacilityPartyRoles.class))).thenReturn(TestUtil.getFacilityPartyRolesResponse());
		when(client.getParty(any(GetParty.class))).thenReturn(TestUtil.getPartyResponse(), TestUtil.getOrganisationParty());

		final var result = integration.getFacility("someAnlaggningsid");

		assertThat(result).isNotNull().hasNoNullFieldsOrPropertiesExcept("mobilAnlaggning");

		verify(client, times(2)).getParty(any(GetParty.class));
		verify(client).getFacilityPartyRoles(any(GetFacilityPartyRoles.class));
		verify(client).getFoodFacilities(any(GetFoodFacilities.class));
		verify(client).searchFacility(any(SearchFacility.class));
		verifyNoMoreInteractions(client);
	}

	@Test
	void getFacilityWithoutFacilityName() {

		when(client.getFoodFacilities(any())).thenReturn(TestUtil.getFoodFacilitiesResponseWithOnlyFacilityCollectionName());
		when(client.searchFacility(any(SearchFacility.class))).thenReturn(TestUtil.searchFacilityResponse());
		when(client.getFacilityPartyRoles(any(GetFacilityPartyRoles.class))).thenReturn(TestUtil.getFacilityPartyRolesResponse());
		when(client.getParty(any(GetParty.class))).thenReturn(TestUtil.getPartyResponse(), TestUtil.getOrganisationParty());

		final var result = integration.getFacility("someAnlaggningsid");

		assertThat(result).isNotNull().hasNoNullFieldsOrPropertiesExcept("mobilAnlaggning");

		verify(client, times(2)).getParty(any(GetParty.class));
		verify(client).getFacilityPartyRoles(any(GetFacilityPartyRoles.class));
		verify(client).getFoodFacilities(any(GetFoodFacilities.class));
		verify(client).searchFacility(any(SearchFacility.class));

		verifyNoMoreInteractions(client);
	}

	@Test
	void getLivsmedelsverksamhet() {
		final var result = integration.getLivsmedelsverksamhet("someAnlaggningsid");

		assertThat(result).isNotNull();
		verifyNoInteractions(client);
	}

	@Test
	void getFakturering() {

		when(client.getFoodFacilities(any())).thenReturn(TestUtil.getFoodFacilitiesResponse());
		when(client.getFacilityPartyRoles(any(GetFacilityPartyRoles.class))).thenReturn(TestUtil.getFacilityPartyRolesResponse());
		when(client.getParty(any(GetParty.class))).thenReturn(TestUtil.getPartyResponse());

		final var result = integration.getFakturering("someAnlaggningsid");

		assertThat(result).isNotNull().hasNoNullFieldsOrPropertiesExcept("fakturareferens");

		verifyNoMoreInteractions(client);
	}
}
