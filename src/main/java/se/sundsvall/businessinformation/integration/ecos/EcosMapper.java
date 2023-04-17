package se.sundsvall.businessinformation.integration.ecos;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import generated.se.sundsvall.forratt.Adress;
import generated.se.sundsvall.forratt.AdresstypEnum;
import generated.se.sundsvall.forratt.AktivitetEnum;
import generated.se.sundsvall.forratt.Anlaggning;
import generated.se.sundsvall.forratt.Anlaggningar;
import generated.se.sundsvall.forratt.Faktura;
import generated.se.sundsvall.forratt.HuvudaktivitetEnum;
import generated.se.sundsvall.forratt.InriktningEnum;
import generated.se.sundsvall.forratt.Kontaktperson;
import generated.se.sundsvall.forratt.Kontaktuppgift;
import generated.se.sundsvall.forratt.Livsmedelsverksamhet;
import generated.se.sundsvall.forratt.OmfattningEnum;
import generated.se.sundsvall.forratt.ProduktgruppEnum;
import generated.se.sundsvall.forratt.RollEnum;
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
    
    Anlaggning toAnlaggning() {
        return new Anlaggning()
            .anlaggningsid("")
            .anlaggningsnamn("")
            .fastighetsbeteckning("")
            .mobilAnlaggning(Anlaggning.MobilAnlaggningEnum.fromValue(""))
            .adresser(List.of(
                new Adress("", "")
                    .coadress("")
                    .postnummer("")
                    .land("")
                    .adresstyp(AdresstypEnum.POSTADRESS)
            ))
            .kontaktpersoner(List.of(new Kontaktperson().kontaktpersonid("")
                .fornamn("")
                .efternamn("")
                .kontaktuppgifter(List.of(new Kontaktuppgift().telefonnummer("").epostadress("")))
                .roll(List.of(RollEnum.fromValue("")))
            ));
        
    }
    
    Livsmedelsverksamhet toLivsmedelsverksamhet() {
        return new Livsmedelsverksamhet()
            .omfattning(OmfattningEnum.fromValue(""))
            .huvudsakliginriktning(InriktningEnum.fromValue(""))
            .huvudaktivitet(List.of(HuvudaktivitetEnum.fromValue("")))
            .aktivitet(List.of(AktivitetEnum.fromValue("")))
            .produktgrupp(List.of(ProduktgruppEnum.fromValue("")))
            .tredjepartscertifiering(Livsmedelsverksamhet.TredjepartscertifieringEnum.fromValue(""));
    }
    
    Faktura toFaktura() {
        return new Faktura()
            .anlaggningsnamn("")
            .fakturareferens("")
            .adresser(List.of(
                new Adress("", "")
                    .coadress("")
                    .postnummer("")
                    .land("")
                    .adresstyp(AdresstypEnum.POSTADRESS)
            ));
    }
    
    
}
