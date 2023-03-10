package se.sundsvall.businessinformation.integration.ecos;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.ArgumentMatchers.any;
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
import org.zalando.problem.ThrowableProblem;

import generated.se.sundsvall.minutmiljo.ArrayOfSearchFacilityResultSvcDto;
import generated.se.sundsvall.minutmiljo.SearchFacility;
import generated.se.sundsvall.minutmiljo.SearchFacilityResponse;
import generated.se.sundsvall.minutmiljo.SearchFacilityResultSvcDto;


@ExtendWith(MockitoExtension.class)
class EcosIntegrationTest {
    
    @Mock
    EcosClient client;
    @Mock(answer = Answers.CALLS_REAL_METHODS)
    EcosMapper mapper;
    @InjectMocks
    EcosIntegration integration;
    
    @ParameterizedTest()
    @ValueSource(strings = {"123456-7890", "12123456-7890"})
    void getFacilities(String orgNr) {
        
        when(client.searchFacility(any(SearchFacility.class))).thenReturn(buildSearchFacilityResult());
        
        
        var result = integration.getFacilities(orgNr);
        
        assertThat(result).isNotNull();
        assertThat(result.size()).isEqualTo(1);
        // We ignore huvudsakliginriktning since ECOS cannot deliver huvudsakliginriktning/main
        // orientation at this given time.
        assertThat(result.get(0)).hasNoNullFieldsOrPropertiesExcept("huvudsakliginriktning");
        
        verify(client, times(1)).searchFacility(any(SearchFacility.class));
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
    void getFacilities_withFaultyOrgNumber(){
    
        assertThatExceptionOfType(ThrowableProblem.class)
            .isThrownBy(() -> integration.getFacilities("someOrgNr"))
            .withMessage("Bad Request: organizationNumber must consist of 10 or 12 digits with " +
                         "the last four seperated with a hyphen");
        
        
        verifyNoInteractions(client);
        verifyNoInteractions(mapper);
    }
    
    
    
    
}