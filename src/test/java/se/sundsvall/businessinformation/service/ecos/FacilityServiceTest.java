package se.sundsvall.businessinformation.service.ecos;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import se.sundsvall.businessinformation.integration.ecos.EcosIntegration;

import generated.se.sundsvall.forratt.Anlaggningar;

@ExtendWith(MockitoExtension.class)
class FacilityServiceTest {
    
    @Mock
    EcosIntegration integration;
    
    @InjectMocks
    FacilityService service;
    
    
    @Test
    void getFacilities() {
        
        when(integration.getFacilities(any(String.class))).thenReturn(List.of(
            new Anlaggningar()
                .anlaggningsid("someAnlaggningsid")
                .anlaggningsnamn("someAnläggningsnamn")
                .gatuadress("someGatuadress")
                .ort("someOrt")));
        
        
     var result =   service.getFacilities("someOrgNr");
    
        assertThat(result).isNotNull();
        assertThat(result.size()).isEqualTo(1);
        // We ignore huvudsakliginriktning since ECOS cannot deliver huvudsakliginriktning/main
        // orientation at this given time.
        assertThat(result.get(0)).hasNoNullFieldsOrPropertiesExcept("huvudsakliginriktning");
    
        verify(integration, times(1)).getFacilities(any(String.class));
        verifyNoMoreInteractions(integration);
     
    }
}