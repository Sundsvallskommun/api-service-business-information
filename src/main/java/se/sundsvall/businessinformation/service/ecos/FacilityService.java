package se.sundsvall.businessinformation.service.ecos;

import java.util.List;

import org.springframework.stereotype.Service;

import se.sundsvall.businessinformation.integration.ecos.EcosIntegration;

import generated.se.sundsvall.forratt.Anlaggning;
import generated.se.sundsvall.forratt.Anlaggningar;
import generated.se.sundsvall.forratt.Faktura;
import generated.se.sundsvall.forratt.Livsmedelsverksamhet;

@Service
public class FacilityService {
    
    private final EcosIntegration integration;
    
    
    public FacilityService(EcosIntegration integration) {
        this.integration = integration;
    }
    
    
    public List<Anlaggningar> getFacilities(String orgNr) {
        return integration.getFacilities(orgNr);
    }
    
    public Anlaggning getFacility(String anlaggningsid) {
        return integration.getFacility(anlaggningsid);
    }
    
    public Livsmedelsverksamhet getLivsmedelsverksamhet(String anlaggningsid) {
        return integration.getLivsmedelsverksamhet(anlaggningsid);
    }
    
    public Faktura getFakturering(String anlaggningsid) {
        return integration.getFakturering(anlaggningsid);
    }
}
