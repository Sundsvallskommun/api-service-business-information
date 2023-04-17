package se.sundsvall.businessinformation.integration.ecos;

import generated.se.sundsvall.forratt.*;
import minutmiljo.*;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class EcosMapper {

    private final static String EMAIL = "D34442AC-D8F7-419A-BE2B-2794674DE58E";
    private final static String PHONE = "2BB38776-54E4-405E-9E84-BD841C6BB2C3";
    private static final String INVOICE_ADRESS = "EEF91381-7025-4FE7-B5FA-92FB2B77976B";

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

    Anlaggning toAnlaggning(SearchFacilityResultSvcDto searchFacility, List<Kontaktperson> kontaktPersoner) {
        return new Anlaggning()
                .anlaggningsid(searchFacility.getFacilityId())
                .anlaggningsnamn(searchFacility.getFacilityName())
                .fastighetsbeteckning(searchFacility.getEstateDesignation())
                .adresser(List.of(
                        new Adress(searchFacility.getVistingAddress(), searchFacility.getPostalArea())
                                .postnummer(searchFacility.getPostCode())
                                .land("Sverige")
                                .adresstyp(AdresstypEnum.BES_KSADRESS)
                ))
                .kontaktpersoner(kontaktPersoner);
    }

    Kontaktperson fromOrganizationDto(OrganizationSvcDto party) {
        return new Kontaktperson()
                .fornamn(party.getOrganizationName())
                .roll(List.of(RollEnum.KONTAKTPERSON_ANL_GGNINGEN))
                .kontaktpersonid(party.getId())
                .kontaktuppgifter(party.getContactInfo().getContactInfoSvcDto().stream()
                        .flatMap(contactInfoSvcDto -> toKontaktUppgifter(contactInfoSvcDto.getContactDetails()
                                .getContactInfoItemSvcDto()).stream())
                        .toList());
    }

    Kontaktperson fromPersonDto(PersonSvcDto party) {
        return new Kontaktperson()
                .fornamn(party.getFirstName())
                .roll(List.of(RollEnum.KONTAKTPERSON_ANL_GGNINGEN))
                .efternamn(party.getLastName())
                .kontaktpersonid(party.getId())
                .kontaktuppgifter(toKontaktUppgifter(party.getContactInfo().getContactDetails().getContactInfoItemSvcDto()));
    }

    private List<Kontaktuppgift> toKontaktUppgifter(List<ContactInfoItemSvcDto> svcDtoList) {
        var kontaktuppgifter = new ArrayList<Kontaktuppgift>();

        var emailIterator = svcDtoList.stream()
                .filter(dto -> dto.getContactPathId().equalsIgnoreCase(EMAIL))
                .iterator();

        var phoneIterator = svcDtoList.stream()
                .filter(dto -> dto.getContactPathId().equalsIgnoreCase(PHONE))
                .iterator();

        while (emailIterator.hasNext() || phoneIterator.hasNext()) {
            var kontaktUppgift = new Kontaktuppgift();
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

    Livsmedelsverksamhet toLivsmedelsverksamhet() {
        return new Livsmedelsverksamhet()
                .omfattning(null)
                .huvudsakliginriktning(null)
                .huvudaktivitet(List.of())
                .aktivitet(List.of())
                .produktgrupp(List.of())
                .tredjepartscertifiering(null);
    }

    Faktura toFaktura(List<PartySvcDto> partyResponses, String facilityName) {
        var addresses = partyResponses.stream()
                .flatMap(partyResponse -> partyResponse
                        .getAddresses()
                        .getPartyAddressSvcDto().stream()
                        .filter(a -> a.getAddressTypes().getAddressTypeSvcDto().stream()
                                .anyMatch(type -> type.getId().equalsIgnoreCase(INVOICE_ADRESS)))
                        .map(adress -> new Adress(adress.getStreetName() + " " + adress.getStreetNumber(), adress.getPostalArea())
                                .coadress(adress.getCareOfName())
                                .postnummer(adress.getPostCode())
                                .adresstyp(AdresstypEnum.FAKTURAADRESS)
                                .land(adress.getCountry())))
                .toList();
        return new Faktura().adresser(addresses).anlaggningsnamn(facilityName);
    }


}
