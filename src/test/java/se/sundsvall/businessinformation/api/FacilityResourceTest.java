package se.sundsvall.businessinformation.api;

import generated.se.sundsvall.forratt.Anlaggning;
import generated.se.sundsvall.forratt.Faktura;
import generated.se.sundsvall.forratt.Livsmedelsverksamhet;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import se.sundsvall.businessinformation.service.ecos.FacilityService;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FacilityResourceTest {

    @Mock
    FacilityService service;

    @InjectMocks
    FacilityResource resource;

    @Test
    void getAnlaggning() {

        when(service.getFacility(any(String.class))).thenReturn(new Anlaggning());

        var result = resource.getAnlaggning("someAnlaggningsId");

        assertThat(result).isNotNull();

        verify(service, times(1)).getFacility(any(String.class));
        verifyNoMoreInteractions(service);
    }

    @Test
    void getLivsmedelsverksamhet() {

        when(service.getLivsmedelsverksamhet(any(String.class))).thenReturn(new Livsmedelsverksamhet());

        var result = resource.getLivsmedelsverksamhet("someAnlaggningsId");

        assertThat(result).isNotNull();

        verify(service, times(1)).getLivsmedelsverksamhet(any(String.class));
        verifyNoMoreInteractions(service);
    }


    @Test
    void getFakturering() {

        when(service.getFakturering(any(String.class))).thenReturn(new Faktura());

        var result = resource.getFakturering("someAnlaggningsId");

        assertThat(result).isNotNull();

        verify(service, times(1)).getFakturering(any(String.class));
        verifyNoMoreInteractions(service);
    }
}