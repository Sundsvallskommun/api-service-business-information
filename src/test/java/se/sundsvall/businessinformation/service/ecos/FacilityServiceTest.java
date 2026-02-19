package se.sundsvall.businessinformation.service.ecos;

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
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import se.sundsvall.businessinformation.integration.ecos.EcosIntegration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FacilityServiceTest {

	@Mock
	private EcosIntegration integration;

	@InjectMocks
	private FacilityService service;

	@Test
	void getFacilities() {

		when(integration.getFacilities(any(String.class))).thenReturn(List.of(
			new Anlaggningar()
				.anlaggningsid("someAnlaggningsid")
				.anlaggningsnamn("someAnläggningsnamn")
				.gatuadress("someGatuadress")
				.ort("someOrt")));

		final var result = service.getFacilities("someOrgNr");

		assertThat(result).isNotNull().hasSize(1);
		// We ignore huvudsakliginriktning since ECOS cannot deliver huvudsakliginriktning/main
		// orientation at this given time.
		assertThat(result.getFirst()).hasNoNullFieldsOrPropertiesExcept("huvudsakliginriktning");

		verify(integration).getFacilities(any(String.class));
		verifyNoMoreInteractions(integration);
	}

	@Test
	void getFacility() {
		when(integration.getFacility(any(String.class))).thenReturn(new Anlaggning()
			.anlaggningsid("someAnlaggningID")
			.fastighetsbeteckning("someFastighetsbeteckning")
			.mobilAnlaggning(Anlaggning.MobilAnlaggningEnum.JA)
			.anlaggningsnamn("someAnlaggningsnamn")
			.kontaktpersoner(List.of(new Kontaktperson()
				.fornamn("someFornamn")
				.efternamn("someLastName")
				.kontaktuppgifter(List.of(new Kontaktuppgift()
					.telefonnummer("somePhoneNumber")
					.epostadress("someEmailAdress")))
				.roll(List.of(RollEnum.KONTAKTPERSON_ANL_GGNINGEN))))
			.adresser(List.of(new Adress("someStreet", "someCounty")
				.coadress("someCoAdress")
				.land("someCountry")
				.postnummer("someZipcode")
				.adresstyp(AdresstypEnum.BES_KSADRESS))));

		final var result = service.getFacility("someAnlaggningID");
		assertThat(result).isNotNull().hasNoNullFieldsOrProperties();

		verify(integration).getFacility(any(String.class));
		verifyNoMoreInteractions(integration);
	}

	@Test
	void getLivsmedelsverksamhet() {

		when(integration.getLivsmedelsverksamhet(any(String.class))).thenReturn(new Livsmedelsverksamhet()
			.huvudaktivitet(List.of(HuvudaktivitetEnum.HKHA001))
			.aktivitet(List.of(AktivitetEnum.HKUA001))
			.huvudsakliginriktning(InriktningEnum.DVI)
			.produktgrupp(List.of(ProduktgruppEnum.PG001))
			.tredjepartscertifiering(Livsmedelsverksamhet.TredjepartscertifieringEnum.JA)
			.omfattning(OmfattningEnum.DVO1));

		final var result = service.getLivsmedelsverksamhet("someAnlaggningID");
		assertThat(result).isNotNull().hasNoNullFieldsOrProperties();

		verify(integration).getLivsmedelsverksamhet(any(String.class));
		verifyNoMoreInteractions(integration);
	}

	@Test
	void getFakturering() {

		when(integration.getFakturering(any(String.class))).thenReturn(new Faktura()
			.anlaggningsnamn("someFacilityName")
			.fakturareferens("someInvoiceReference")
			.adresser(List.of(new Adress("someStreet", "someCounty")
				.coadress("someCoAdress")
				.land("someCountry")
				.postnummer("someZipcode")
				.adresstyp(AdresstypEnum.BES_KSADRESS))));

		final var result = service.getFakturering("someAnlaggningID");
		assertThat(result).isNotNull().hasNoNullFieldsOrProperties();

		verify(integration).getFakturering(any(String.class));
		verifyNoMoreInteractions(integration);
	}
}
