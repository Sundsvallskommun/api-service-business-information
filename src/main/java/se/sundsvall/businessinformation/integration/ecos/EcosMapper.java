package se.sundsvall.businessinformation.integration.ecos;

import java.util.ArrayList;
import java.util.List;

import generated.se.sundsvall.forratt.Adress;
import generated.se.sundsvall.forratt.AdresstypEnum;
import generated.se.sundsvall.forratt.Anlaggning;
import generated.se.sundsvall.forratt.Anlaggningar;
import generated.se.sundsvall.forratt.Faktura;
import generated.se.sundsvall.forratt.Kontaktperson;
import generated.se.sundsvall.forratt.Kontaktuppgift;
import generated.se.sundsvall.forratt.Livsmedelsverksamhet;
import generated.se.sundsvall.forratt.RollEnum;
import minutmiljo.ArrayOfFacilityFilterSvcDto;
import minutmiljo.ContactInfoItemSvcDto;
import minutmiljo.FacilityFacilityCollectionNameFilterSvcDto;
import minutmiljo.OrganizationSvcDto;
import minutmiljo.PartySvcDto;
import minutmiljo.PersonSvcDto;
import minutmiljo.SearchFacility;
import minutmiljo.SearchFacilityResponse;
import minutmiljo.SearchFacilityResultSvcDto;
import minutmiljo.SearchFacilitySvcDto;

public final class EcosMapper {

	private static final String EMAIL = "D34442AC-D8F7-419A-BE2B-2794674DE58E";
	private static final String PHONE = "2BB38776-54E4-405E-9E84-BD841C6BB2C3";
	private static final String INVOICE_ADRESS = "EEF91381-7025-4FE7-B5FA-92FB2B77976B";

	private EcosMapper() {
		// Intentionally empty
	}

	static SearchFacility toSearchFacilityRequest(final String facilityName) {
		return new SearchFacility()
			.withSearchFacilitySvcDto(new SearchFacilitySvcDto()
				.withFacilityFilters(new ArrayOfFacilityFilterSvcDto()
					.withFacilityFilterSvcDto(new FacilityFacilityCollectionNameFilterSvcDto()
						.withFacilityCollectionName(facilityName))));
	}

	static List<Anlaggningar> toDto(final SearchFacilityResponse response) {
		final var result = response.getSearchFacilityResult().getSearchFacilityResultSvcDto();
		return result.stream().map(dto -> new Anlaggningar()
			.anlaggningsid(dto.getFacilityId())
			.anlaggningsnamn(dto.getFacilityName())
			.gatuadress(dto.getVistingAddress())
			.ort(dto.getPostalArea()))
			.toList();
	}

	static Anlaggning toAnlaggning(final SearchFacilityResultSvcDto searchFacility, final List<Kontaktperson> kontaktPersoner) {
		return new Anlaggning()
			.anlaggningsid(searchFacility.getFacilityId())
			.anlaggningsnamn(searchFacility.getFacilityName())
			.fastighetsbeteckning(searchFacility.getEstateDesignation())
			.adresser(List.of(
				new Adress(searchFacility.getVistingAddress(), searchFacility.getPostalArea())
					.postnummer(searchFacility.getPostCode())
					.land("Sverige")
					.adresstyp(AdresstypEnum.BES_KSADRESS)))
			.kontaktpersoner(kontaktPersoner);
	}

	static Kontaktperson fromOrganizationDto(final OrganizationSvcDto party) {
		return new Kontaktperson()
			.fornamn(party.getOrganizationName())
			.roll(List.of(RollEnum.KONTAKTPERSON_ANL_GGNINGEN))
			.kontaktpersonid(party.getId())
			.kontaktuppgifter(party.getContactInfo().getContactInfoSvcDto().stream()
				.flatMap(contactInfoSvcDto -> toKontaktUppgifter(contactInfoSvcDto.getContactDetails()
					.getContactInfoItemSvcDto()).stream())
				.toList());
	}

	static Kontaktperson fromPersonDto(final PersonSvcDto party) {
		return new Kontaktperson()
			.fornamn(party.getFirstName())
			.roll(List.of(RollEnum.KONTAKTPERSON_ANL_GGNINGEN))
			.efternamn(party.getLastName())
			.kontaktpersonid(party.getId())
			.kontaktuppgifter(toKontaktUppgifter(party.getContactInfo().getContactDetails().getContactInfoItemSvcDto()));
	}

	private static List<Kontaktuppgift> toKontaktUppgifter(final List<ContactInfoItemSvcDto> svcDtoList) {
		final var kontaktuppgifter = new ArrayList<Kontaktuppgift>();

		final var emailIterator = svcDtoList.stream()
			.filter(dto -> EMAIL.equalsIgnoreCase(dto.getContactPathId()))
			.iterator();

		final var phoneIterator = svcDtoList.stream()
			.filter(dto -> PHONE.equalsIgnoreCase(dto.getContactPathId()))
			.iterator();

		while (emailIterator.hasNext() || phoneIterator.hasNext()) {
			final var kontaktUppgift = new Kontaktuppgift();
			if (emailIterator.hasNext()) {
				kontaktUppgift.setEpostadress(emailIterator.next().getValue());
			}
			if (phoneIterator.hasNext()) {
				kontaktUppgift.setTelefonnummer(phoneIterator.next().getValue());
			}
			kontaktuppgifter.add(kontaktUppgift);
		}
		return kontaktuppgifter;
	}

	static Livsmedelsverksamhet toLivsmedelsverksamhet() {
		return new Livsmedelsverksamhet()
			.omfattning(null)
			.huvudsakliginriktning(null)
			.huvudaktivitet(List.of())
			.aktivitet(List.of())
			.produktgrupp(List.of())
			.tredjepartscertifiering(null);
	}

	static Faktura toFaktura(final List<PartySvcDto> partyResponses, final String facilityName) {
		final var addresses = partyResponses.stream()
			.flatMap(partyResponse -> partyResponse
				.getAddresses()
				.getPartyAddressSvcDto().stream()
				.filter(a -> a.getAddressTypes().getAddressTypeSvcDto().stream()
					.anyMatch(type -> INVOICE_ADRESS.equalsIgnoreCase(type.getId())))
				.map(adress -> new Adress(adress.getStreetName() + " " + adress.getStreetNumber(), adress.getPostalArea())
					.coadress(adress.getCareOfName())
					.postnummer(adress.getPostCode())
					.adresstyp(AdresstypEnum.FAKTURAADRESS)
					.land(adress.getCountry())))
			.toList();
		return new Faktura().adresser(addresses).anlaggningsnamn(facilityName);
	}
}
