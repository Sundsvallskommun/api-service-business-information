package se.sundsvall.businessinformation.integration.ecos;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import generated.se.sundsvall.forratt.Anlaggningar;
import minutmiljo.SearchFacilityResponse;

@Component
public class EcosMapper {
    
    List<Anlaggningar> toDto(SearchFacilityResponse response) {
        var result = response.getSearchFacilityResult().getSearchFacilityResultSvcDto();
        return result.stream().map(dto ->
                new Anlaggningar()
                    .anlaggningsid(dto.getFacilityId())
                    .anlaggningsnamn(dto.getFacilityName())
                    .gatuadress(dto.getVistingAddress())
                    .ort(dto.getPostalArea()))
            .collect(Collectors.toList());
    }
    
    
}
