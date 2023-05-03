package se.sundsvall.businessinformation.integration.ecos;

import generated.se.sundsvall.forratt.*;
import minutmiljo.*;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.ws.client.core.WebServiceTemplate;
import org.zalando.problem.Problem;
import org.zalando.problem.Status;
import se.sundsvall.businessinformation.integration.ecos.model.FacilityStatus;
import se.sundsvall.businessinformation.integration.ecos.model.FacilityType;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.Predicate;


@Component
@EnableConfigurationProperties(EcosIntegrationProperties.class)
public class EcosIntegration {

    private final static String INVOICE_ROLE = "480E2731-1F2F-4F35-8A37-FDDE957E9CD0";

    private final WebServiceTemplate webServiceTemplate;
    private final EcosMapper mapper;

    public EcosIntegration(@Qualifier("minutmiljo-webservice-template") final WebServiceTemplate webServiceTemplate, EcosMapper mapper) {
        this.webServiceTemplate = webServiceTemplate;
        this.mapper = mapper;
    }

    private static <T> Predicate<T> distinctByKey(Function<? super T, ?> keyExtractor) {
        Set<Object> seen = ConcurrentHashMap.newKeySet();
        return t -> seen.add(keyExtractor.apply(t));
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

        return mapper.toDto((SearchFacilityResponse) webServiceTemplate.marshalSendAndReceive(new SearchFacility()
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
    
    public Anlaggning getFacility(String anlaggningsid) {
        
        var foodFacility = Optional.ofNullable(getFoodFacility(anlaggningsid));
        var searchFacility = foodFacility
            .map(FoodFacilityInfoSvcDto::getFacilityName)
            .map(name -> searchFacilityWithFacilityName(name, anlaggningsid))
            .orElseGet(() -> searchFacilityWithFacilityCollectionName(foodFacility
                .orElseThrow(() -> Problem.valueOf(Status.BAD_REQUEST, "Could not find a facility in Ecos with anlaggningsid: " + anlaggningsid))
                .getFacilityCollectionName(), anlaggningsid));
        
        var kontaktPersoner = getKontaktpersoner(getFacilityPartyRoles(anlaggningsid));
        
        return mapper.toAnlaggning(searchFacility, kontaktPersoner);
    }
    
    private List<Kontaktperson> getKontaktpersoner(List<ConnectedRoleSvcDto> partyRoles) {
        return partyRoles.stream()
            .distinct()
            .filter(distinctByKey(ConnectedRoleSvcDto::getPartyId))
            .map(connectedRoleSvcDto -> {
                var party = getParty(connectedRoleSvcDto.getPartyId());
                if (party instanceof PersonSvcDto) {
                    return mapper.fromPersonDto((PersonSvcDto) party);
                } else {
                    return mapper.fromOrganizationDto((OrganizationSvcDto) party);
                }
            }).toList();
    }
    
    private SearchFacilityResultSvcDto searchFacilityWithFacilityName(String facilityName, String anlaggningsid) {
        var result = (SearchFacilityResponse) webServiceTemplate.marshalSendAndReceive(new SearchFacility()
                .withSearchFacilitySvcDto(new SearchFacilitySvcDto()
                        .withFacilityFilters(new ArrayOfFacilityFilterSvcDto()
                                .withFacilityFilterSvcDto(new FacilityFacilityNameFilterSvcDto()
                                        .withFacilityName(facilityName)))));

        return result.getSearchFacilityResult()
                .getSearchFacilityResultSvcDto()
                .stream()
                .filter(a -> a.getFacilityId().equalsIgnoreCase(anlaggningsid))
                .findFirst()
                .orElseThrow(() -> Problem.valueOf(Status.BAD_REQUEST, "Could not find a facility in Ecos with anlaggningsid: " + anlaggningsid));
    }
    
    private SearchFacilityResultSvcDto searchFacilityWithFacilityCollectionName(String facilityCollectionName, String anlaggningsid) {
        var result = (SearchFacilityResponse) webServiceTemplate.marshalSendAndReceive(new SearchFacility()
                .withSearchFacilitySvcDto(new SearchFacilitySvcDto()
                        .withFacilityFilters(new ArrayOfFacilityFilterSvcDto()
                                .withFacilityFilterSvcDto(new FacilityFacilityCollectionNameFilterSvcDto()
                                        .withFacilityCollectionName(facilityCollectionName)))));

        return result.getSearchFacilityResult()
                .getSearchFacilityResultSvcDto()
                .stream()
                .filter(a -> a.getFacilityId().equalsIgnoreCase(anlaggningsid))
                .findFirst()
                .orElseThrow(() -> Problem.valueOf(Status.BAD_REQUEST, "Could not find a facility in Ecos with anlaggningsid: " + anlaggningsid));
    }
    
    private FoodFacilityInfoSvcDto getFoodFacility(String anlaggningsid) {
        try {
            var result = (GetFoodFacilitiesResponse) webServiceTemplate.marshalSendAndReceive(new GetFoodFacilities()
                    .withRequest(new GetFoodFacilitiesSvcDto()
                            .withFacilityIds(new ArrayOfguid()
                                    .withGuid(anlaggningsid))));

            return result.getGetFoodFacilitiesResult().getFoodFacilityInfoSvcDto()
                    .get(0);
        } catch (Exception e) {
            throw Problem.valueOf(Status.BAD_REQUEST, "Could not find a facility in Ecos with anlaggningsid: " + anlaggningsid);
        }
    }
    
    private List<ConnectedRoleSvcDto> getFacilityPartyRoles(String anlaggningsid) {
        var result =
                (GetFacilityPartyRolesResponse) webServiceTemplate.marshalSendAndReceive(new GetFacilityPartyRoles().withFacilityId(anlaggningsid));

        return result
                .getGetFacilityPartyRolesResult()
                .getConnectedRoleSvcDto();
    }
    
    private PartySvcDto getParty(String partyId) {
        var result = (GetPartyResponse) webServiceTemplate.marshalSendAndReceive(new GetParty().withPartyId(partyId));
        return result.getGetPartyResult();
    }
    
    
    public Livsmedelsverksamhet getLivsmedelsverksamhet(String anlaggningsid) {
        return mapper.toLivsmedelsverksamhet();
    }
    
    public Faktura getFakturering(String anlaggningsid) {
        var facilityName = getFoodFacility(anlaggningsid).getFacilityName();
        
        var partyResponses = getFacilityPartyRoles(anlaggningsid)
            .stream()
            .filter(dto -> dto.getRoleId().equalsIgnoreCase(INVOICE_ROLE))
            .map(dto -> getParty(dto.getPartyId()))
            .toList();
        
        return mapper.toFaktura(partyResponses, facilityName);
    }
}
