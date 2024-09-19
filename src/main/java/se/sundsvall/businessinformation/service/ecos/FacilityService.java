package se.sundsvall.businessinformation.service.ecos;

import java.util.List;

import org.springframework.stereotype.Service;

import generated.se.sundsvall.forratt.Anlaggning;
import generated.se.sundsvall.forratt.Anlaggningar;
import generated.se.sundsvall.forratt.Faktura;
import generated.se.sundsvall.forratt.Livsmedelsverksamhet;
import se.sundsvall.businessinformation.integration.ecos.EcosIntegration;

@Service
public class FacilityService {

	private final EcosIntegration integration;

	public FacilityService(final EcosIntegration integration) {
		this.integration = integration;
	}

	public List<Anlaggningar> getFacilities(final String orgNr) {
		return integration.getFacilities(orgNr);
	}

	public Anlaggning getFacility(final String anlaggningsid) {
		return integration.getFacility(anlaggningsid);
	}

	public Livsmedelsverksamhet getLivsmedelsverksamhet(final String anlaggningsid) {
		return integration.getLivsmedelsverksamhet(anlaggningsid);
	}

	public Faktura getFakturering(final String anlaggningsid) {
		return integration.getFakturering(anlaggningsid);
	}
}
