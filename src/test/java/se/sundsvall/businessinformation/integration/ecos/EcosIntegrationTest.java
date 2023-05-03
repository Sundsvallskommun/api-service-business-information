package se.sundsvall.businessinformation.integration.ecos;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.anyList;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Answers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.ws.client.core.WebServiceTemplate;
import org.zalando.problem.ThrowableProblem;

import se.sundsvall.businessinformation.TestUtil;

import minutmiljo.ArrayOfSearchFacilityResultSvcDto;
import minutmiljo.GetFacilityPartyRoles;
import minutmiljo.GetFoodFacilities;
import minutmiljo.GetParty;
import minutmiljo.OrganizationSvcDto;
import minutmiljo.PersonSvcDto;
import minutmiljo.SearchFacility;
import minutmiljo.SearchFacilityResponse;
import minutmiljo.SearchFacilityResultSvcDto;


@ExtendWith(MockitoExtension.class)
class EcosIntegrationTest {
    
    @Mock
    WebServiceTemplate client;
    @Mock(answer = Answers.CALLS_REAL_METHODS)
    EcosMapper mapper;
    @InjectMocks
    EcosIntegration integration;
    @ParameterizedTest()
    @ValueSource(strings = {"123456-7890", "12123456-7890"})
    void getFacilities(String orgNr) {
        
        when(client.marshalSendAndReceive(any(SearchFacility.class))).thenReturn(buildSearchFacilityResult());
        
        
        var result = integration.getFacilities(orgNr);
        
        assertThat(result).isNotNull();
        assertThat(result.size()).isEqualTo(1);
        // We ignore huvudsakliginriktning since ECOS cannot deliver huvudsakliginriktning/main
        // orientation at this given time.
        assertThat(result.get(0)).hasNoNullFieldsOrPropertiesExcept("huvudsakliginriktning");
        
        verify(client, times(1)).marshalSendAndReceive(any(SearchFacility.class));
        verify(mapper, times(1)).toDto(any(SearchFacilityResponse.class));
        verifyNoMoreInteractions(mapper);
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
                                        .withVistingAddress("someVisitingAdress")))
                );
    }

    @Test
    void getFacilities_withFaultyOrgNumber() {

        assertThatExceptionOfType(ThrowableProblem.class)
                .isThrownBy(() -> integration.getFacilities("someOrgNr"))
                .withMessage("Bad Request: organizationNumber must consist of 10 or 12 digits with " +
                        "the last four seperated with a hyphen");


        verifyNoInteractions(client);
        verifyNoInteractions(mapper);
    }


    @Test
    void getFacility() {
        
        when(client.marshalSendAndReceive(any())).thenReturn(TestUtil.getFoodFacilitiesResponse());
        when(client.marshalSendAndReceive(any(SearchFacility.class))).thenReturn(TestUtil.searchFacilityResponse());
        
        when(client.marshalSendAndReceive(any(GetFacilityPartyRoles.class))).thenReturn(TestUtil.getFacilityPartyRolesResponse());
        when(client.marshalSendAndReceive(any(GetParty.class))).thenReturn(TestUtil.getPartyResponse(), TestUtil.getOrganisationParty());
        
        
        var result = integration.getFacility("someAnlaggningsid");
        
        assertThat(result).isNotNull();
        assertThat(result).hasNoNullFieldsOrPropertiesExcept("mobilAnlaggning");
        
        verify(mapper, times(1)).fromPersonDto(any(PersonSvcDto.class));
        verify(mapper, times(1)).fromOrganizationDto(any(OrganizationSvcDto.class));
        verify(mapper, times(1)).toAnlaggning(any(SearchFacilityResultSvcDto.class), anyList());
        verify(client, times(2)).marshalSendAndReceive(any(GetParty.class));
        verify(client, times(1)).marshalSendAndReceive(any(GetFacilityPartyRoles.class));
        verify(client, times(1)).marshalSendAndReceive(any(GetFoodFacilities.class));
        verify(client, times(1)).marshalSendAndReceive(any(SearchFacility.class));
        verifyNoMoreInteractions(mapper);
        verifyNoMoreInteractions(client);
    }

    @Test
    void getFacilityWithoutFacilityName() {
        
        when(client.marshalSendAndReceive(any())).thenReturn(TestUtil.getFoodFacilitiesResponseWithOnlyFacilityCollectionName());
        when(client.marshalSendAndReceive(any(SearchFacility.class))).thenReturn(TestUtil.searchFacilityResponse());
        
        when(client.marshalSendAndReceive(any(GetFacilityPartyRoles.class))).thenReturn(TestUtil.getFacilityPartyRolesResponse());
        when(client.marshalSendAndReceive(any(GetParty.class))).thenReturn(TestUtil.getPartyResponse(), TestUtil.getOrganisationParty());
        
        
        var result = integration.getFacility("someAnlaggningsid");
        
        assertThat(result).isNotNull();
        assertThat(result).hasNoNullFieldsOrPropertiesExcept("mobilAnlaggning");
        
        verify(mapper, times(1)).fromPersonDto(any(PersonSvcDto.class));
        verify(mapper, times(1)).fromOrganizationDto(any(OrganizationSvcDto.class));
        verify(mapper, times(1)).toAnlaggning(any(SearchFacilityResultSvcDto.class), anyList());
        verify(client, times(2)).marshalSendAndReceive(any(GetParty.class));
        verify(client, times(1)).marshalSendAndReceive(any(GetFacilityPartyRoles.class));
        verify(client, times(1)).marshalSendAndReceive(any(GetFoodFacilities.class));
        verify(client, times(1)).marshalSendAndReceive(any(SearchFacility.class));
        verifyNoMoreInteractions(mapper);
        verifyNoMoreInteractions(client);
    }

    @Test
    void getLivsmedelsverksamhet() {
        var result = integration.getLivsmedelsverksamhet("someAnlaggningsid");

        assertThat(result).isNotNull();

        verify(mapper, times(1)).toLivsmedelsverksamhet();
        verifyNoInteractions(client);
        verifyNoMoreInteractions(mapper);
    }

    @Test
    void getFakturering() {
        
        when(client.marshalSendAndReceive(any())).thenReturn(TestUtil.getFoodFacilitiesResponse());
        when(client.marshalSendAndReceive(any(GetFacilityPartyRoles.class))).thenReturn(TestUtil.getFacilityPartyRolesResponse());
        when(client.marshalSendAndReceive(any(GetParty.class))).thenReturn(TestUtil.getPartyResponse());
        
        var result = integration.getFakturering("someAnlaggningsid");
        
        assertThat(result).isNotNull();
        assertThat(result).hasNoNullFieldsOrPropertiesExcept("fakturareferens");
        
        verify(mapper, times(1)).toFaktura(anyList(), any(String.class));
        verifyNoMoreInteractions(mapper);
        verifyNoMoreInteractions(client);
    }
}