package se.sundsvall.businessinformation.integration.ecos;

import static se.sundsvall.businessinformation.integration.ecos.configuration.EcosConfiguration.CLIENT_ID;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import minutmiljo.GetFacilityPartyRoles;
import minutmiljo.GetFacilityPartyRolesResponse;
import minutmiljo.GetFoodFacilities;
import minutmiljo.GetFoodFacilitiesResponse;
import minutmiljo.GetParty;
import minutmiljo.GetPartyResponse;
import minutmiljo.SearchFacility;
import minutmiljo.SearchFacilityResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import se.sundsvall.businessinformation.integration.ecos.configuration.EcosConfiguration;

@FeignClient(name = CLIENT_ID, url = "${integration.ecos.url}", configuration = EcosConfiguration.class)
@CircuitBreaker(name = CLIENT_ID)
public interface EcosClient {

	String TEXT_XML_UTF8 = "text/xml;charset=UTF-8";

	@PostMapping(consumes = TEXT_XML_UTF8, headers = {
		"SOAPAction=urn:Ecos.API.MinutMiljo.Service.V1/IMinutMiljoService/SearchFacility"
	})
	SearchFacilityResponse searchFacility(SearchFacility searchFacility);

	@PostMapping(consumes = TEXT_XML_UTF8, headers = {
		"SOAPAction=urn:Ecos.API.MinutMiljo.Service.V1/IMinutMiljoService/GetFoodFacilities"
	})
	GetFoodFacilitiesResponse getFoodFacilities(GetFoodFacilities getFoodFacilities);

	@PostMapping(consumes = TEXT_XML_UTF8, headers = {
		"SOAPAction=urn:Ecos.API.MinutMiljo.Service.V1/IMinutMiljoService/GetFacilityPartyRoles"
	})
	GetFacilityPartyRolesResponse getFacilityPartyRoles(GetFacilityPartyRoles getFacilityPartyRoles);

	@PostMapping(consumes = TEXT_XML_UTF8, headers = {
		"SOAPAction=urn:Ecos.API.MinutMiljo.Service.V1/IMinutMiljoService/GetParty"
	})
	GetPartyResponse getParty(GetParty withPartyId);
}
