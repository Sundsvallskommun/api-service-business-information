package se.sundsvall.businessinformation.integration.ecos;

import java.util.List;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Component;
import org.zalando.problem.Problem;
import org.zalando.problem.Status;

import se.sundsvall.businessinformation.integration.ecos.model.FacilityStatus;
import se.sundsvall.businessinformation.integration.ecos.model.FacilityType;

import generated.se.sundsvall.forratt.Anlaggningar;
import generated.se.sundsvall.minutmiljo.ArrayOfFacilityFilterSvcDto;
import generated.se.sundsvall.minutmiljo.ArrayOfguid;
import generated.se.sundsvall.minutmiljo.FacilityFacilityStatusIdsFilterSvcDto;
import generated.se.sundsvall.minutmiljo.FacilityFacilityTypeIdsFilterSvcDto;
import generated.se.sundsvall.minutmiljo.FacilityPartyOrganizationNumberFilterSvcDto;
import generated.se.sundsvall.minutmiljo.SearchFacility;
import generated.se.sundsvall.minutmiljo.SearchFacilitySvcDto;


@Component
@EnableConfigurationProperties(EcosIntegrationProperties.class)
public class EcosIntegration {
    
    private final EcosClient client;
    private final EcosMapper mapper;
    
    public EcosIntegration(EcosClient client, EcosMapper mapper) {
        this.client = client;
        this.mapper = mapper;
    }
    
    
    public List<Anlaggningar> getFacilities(String orgNr) {
        
        var facilityTypeFilter = new FacilityFacilityTypeIdsFilterSvcDto()
            .withFacilityTypeIds(FacilityType.LIVSMEDELSANLAGGNING.getValue());
        
        var facilityStatusFilter =
            new FacilityFacilityStatusIdsFilterSvcDto()
                .withFacilityStatusIds(new ArrayOfguid().withGuid(
                    FacilityStatus.ANMALD.getValue(),
                    FacilityStatus.INAKTIV.getValue(),
                    FacilityStatus.AKTIV.getValue(),
                    FacilityStatus.BEVILJAD.getValue()
                ));
        
        var orgFilter = new FacilityPartyOrganizationNumberFilterSvcDto()
            .withOrganizationNumber(formatOrganizationNumber(orgNr));
        
        return mapper.toDto(client
            .searchFacility(new SearchFacility()
                .withSearchFacilitySvcDto(new SearchFacilitySvcDto()
                    .withFacilityFilters(new ArrayOfFacilityFilterSvcDto()
                        .withFacilityFilterSvcDto(facilityStatusFilter, facilityTypeFilter, orgFilter)))));
    }
    
    private String formatOrganizationNumber(String organizationNumber) {
        // The length is 1 more than the number of numbers because the text contains a hyphen
        return switch (organizationNumber.length()) {
            case 13 -> organizationNumber;
            case 11 -> "16" + organizationNumber;
            default -> throw Problem.valueOf(Status.BAD_REQUEST,
                "organizationNumber must consist of 10 or 12 digits with the last four seperated with a hyphen");
        };
        
    }
}
