package se.sundsvall.businessinformation.integration.ecos;

import java.util.List;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Component;

import se.sundsvall.businessinformation.integration.ecos.model.FacilityStatus;
import se.sundsvall.businessinformation.integration.ecos.model.FacilityType;

import generated.se.sundsvall.forratt.Anlaggningar;
import minutmiljo.ArrayOfFacilityFilterSvcDto;
import minutmiljo.ArrayOfguid;
import minutmiljo.FacilityFacilityStatusIdsFilterSvcDto;
import minutmiljo.FacilityFacilityTypeIdsFilterSvcDto;
import minutmiljo.FacilityPartyOrganizationNumberFilterSvcDto;
import minutmiljo.SearchFacility;
import minutmiljo.SearchFacilitySvcDto;

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
            .withOrganizationNumber(orgNr);
        
        return mapper.toDto(client
            .searchFacility(new SearchFacility()
                .withSearchFacilitySvcDto(new SearchFacilitySvcDto()
                    .withFacilityFilters(new ArrayOfFacilityFilterSvcDto()
                        .withFacilityFilterSvcDto(facilityStatusFilter, facilityTypeFilter, orgFilter)))));
    }
}
