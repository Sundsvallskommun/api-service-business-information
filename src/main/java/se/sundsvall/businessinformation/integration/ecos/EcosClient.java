package se.sundsvall.businessinformation.integration.ecos;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;

import minutmiljo.SearchFacility;
import minutmiljo.SearchFacilityResponse;

@FeignClient(name = "minutmiljo", url = "${integration.ecos.url}", configuration =
    EcosIntegrationConfiguration.class)
interface EcosClient {
    
    String TEXT_XML_UTF8 = "text/xml;charset=UTF-8";
    
    @PostMapping(consumes = TEXT_XML_UTF8, headers = {"SOAPAction=urn:Ecos.API.MinutMiljo.Service.V1/IMinutMiljoService/SearchFacility"})
    SearchFacilityResponse searchFacility(SearchFacility searchFacility);
}
