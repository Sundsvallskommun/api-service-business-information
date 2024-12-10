package se.sundsvall.businessinformation;

import java.util.List;
import minutmiljo.AddressTypeSvcDto;
import minutmiljo.ArrayOfAddressTypeSvcDto;
import minutmiljo.ArrayOfConnectedRoleSvcDto;
import minutmiljo.ArrayOfContactInfoItemSvcDto;
import minutmiljo.ArrayOfContactInfoSvcDto;
import minutmiljo.ArrayOfFoodFacilityInfoSvcDto;
import minutmiljo.ArrayOfPartyAddressSvcDto;
import minutmiljo.ArrayOfSearchFacilityResultSvcDto;
import minutmiljo.ConnectedRoleSvcDto;
import minutmiljo.ContactInfoItemSvcDto;
import minutmiljo.ContactInfoSvcDto;
import minutmiljo.FoodFacilityInfoSvcDto;
import minutmiljo.GetFacilityPartyRolesResponse;
import minutmiljo.GetFoodFacilitiesResponse;
import minutmiljo.GetPartyResponse;
import minutmiljo.OrganizationSvcDto;
import minutmiljo.PartyAddressSvcDto;
import minutmiljo.PersonSvcDto;
import minutmiljo.SearchFacilityResponse;
import minutmiljo.SearchFacilityResultSvcDto;

public final class TestUtil {

	public static GetPartyResponse getPartyResponse() {
		return new GetPartyResponse()
			.withGetPartyResult(new PersonSvcDto()
				.withContactInfo(new ContactInfoSvcDto().withContactDetails(new ArrayOfContactInfoItemSvcDto()
					.withContactInfoItemSvcDto(new ContactInfoItemSvcDto()
						.withContactDetailId("someContactDetailId")
						.withContactDetailTypeId("someContactDetailTypeId")
						.withContactPathId("2BB38776-54E4-405E-9E84-BD841C6BB2C3")
						.withValue("someValue"))))
				.withFirstName("someFirstname")
				.withId("someId")
				.withLastName("someLastName")
				.withAddresses(new ArrayOfPartyAddressSvcDto()
					.withPartyAddressSvcDto(List.of(new PartyAddressSvcDto()
						.withCareOfName("someCareOfName")
						.withPostalArea("somePostalArea")
						.withStreetName("someStreetName")
						.withStreetNumber("someStreetNumber")
						.withAddressTypes(new ArrayOfAddressTypeSvcDto()
							.withAddressTypeSvcDto(new AddressTypeSvcDto()
								.withId("EEF91381-7025-4FE7-B5FA-92FB2B77976B")))
						.withPostCode("somePostCode")
						.withCountry("someCountry")))));
	}

	public static GetPartyResponse getOrganisationParty() {
		return new GetPartyResponse()
			.withGetPartyResult(new OrganizationSvcDto()
				.withContactInfo(new ArrayOfContactInfoSvcDto().withContactInfoSvcDto((new ContactInfoSvcDto().withContactDetails(new ArrayOfContactInfoItemSvcDto()
					.withContactInfoItemSvcDto(new ContactInfoItemSvcDto()
						.withContactDetailId("someContactDetailId")
						.withContactDetailTypeId("someContactDetailTypeId")
						.withContactPathId("2BB38776-54E4-405E-9E84-BD841C6BB2C3")
						.withValue("someValue"))))))
				.withId("someId")
				.withOrganizationName("someName")
				.withAddresses(new ArrayOfPartyAddressSvcDto()
					.withPartyAddressSvcDto(List.of(new PartyAddressSvcDto()
						.withCareOfName("someCareOfName")
						.withPostalArea("somePostalArea")
						.withStreetName("someStreetName")
						.withStreetNumber("someStreetNumber")
						.withAddressTypes(new ArrayOfAddressTypeSvcDto()
							.withAddressTypeSvcDto(new AddressTypeSvcDto()
								.withId("EEF91381-7025-4FE7-B5FA-92FB2B77976B")))
						.withPostCode("somePostCode")
						.withCountry("someCountry")))));
	}

	public static GetFacilityPartyRolesResponse getFacilityPartyRolesResponse() {
		return new GetFacilityPartyRolesResponse()
			.withGetFacilityPartyRolesResult(new ArrayOfConnectedRoleSvcDto()
				.withConnectedRoleSvcDto(List.of(new ConnectedRoleSvcDto()
					.withConnectionId("someConnectionId")
					.withPartyId("somePartyId")
					.withRoleId("480E2731-1F2F-4F35-8A37-FDDE957E9CD0"),
					new ConnectedRoleSvcDto()
						.withConnectionId("someOtherConnectionId")
						.withPartyId("someOtherPartyId")
						.withRoleId("45A48C9F-9BAC-45DB-8D47-CDA790E17383"))));
	}

	public static GetFoodFacilitiesResponse getFoodFacilitiesResponse() {
		return new GetFoodFacilitiesResponse()
			.withGetFoodFacilitiesResult(new ArrayOfFoodFacilityInfoSvcDto()
				.withFoodFacilityInfoSvcDto(new FoodFacilityInfoSvcDto()

					.withFacilityName("somename")));
	}

	public static GetFoodFacilitiesResponse getFoodFacilitiesResponseWithOnlyFacilityCollectionName() {
		return new GetFoodFacilitiesResponse()
			.withGetFoodFacilitiesResult(new ArrayOfFoodFacilityInfoSvcDto()
				.withFoodFacilityInfoSvcDto(new FoodFacilityInfoSvcDto()
					.withFacilityCollectionName("someFacilityCollectionName")));
	}

	public static SearchFacilityResponse searchFacilityResponse() {
		return new SearchFacilityResponse()
			.withSearchFacilityResult(new ArrayOfSearchFacilityResultSvcDto()
				.withSearchFacilityResultSvcDto(List.of(new SearchFacilityResultSvcDto()
					.withFacilityName("someFacilityName")
					.withPlace("somePlace")
					.withPostCode("somePostCode")
					.withFacilityId("someAnlaggningsid")
					.withPostalArea("somePostalArea")
					.withVistingAddress("someVisitingAdress")
					.withEstateDesignation("someEstateDesignation"))));
	}
}
