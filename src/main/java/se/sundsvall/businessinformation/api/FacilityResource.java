package se.sundsvall.businessinformation.api;

import generated.se.sundsvall.forratt.Anlaggning;
import minutmiljo.GetFacilityPartyRoles;
import minutmiljo.GetFacilityPartyRolesResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import se.sundsvall.businessinformation.service.ecos.FacilityService;

import generated.se.sundsvall.forratt.Anmalan;
import generated.se.sundsvall.forratt.Faktura;
import generated.se.sundsvall.forratt.Livsmedelsverksamhet;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import minutmiljo.GetFoodFacilitiesResponse;


@RestController
@Validated
@RequestMapping("")
@Tag(name = "anlaggningsuppgifter", description = "Information om en anläggning")
public class FacilityResource {
    
    private final FacilityService service;
    
    public FacilityResource(FacilityService service) {
        this.service = service;
    }
    
    @GetMapping("/anlaggning/{anlaggningsid}")
    @Operation(summary = "Hämta uppgifter om anläggningen med angivet anläggningsid",
        description = "Returnerar uppgifter om en anläggning")
    public ResponseEntity<Anlaggning> getAnlaggning(
        @Schema(description = "Anläggningsid för den anläggning som ska hämtas")
        @PathVariable String anlaggningsid) {
        return ResponseEntity.ok(service.getFacility(anlaggningsid));
    }
    
    
    @GetMapping("/anlaggning/{anlaggningsid}/livsmedelsverksamhet")
    @Operation(summary = "Hämta uppgifter om livsmedelsverksamheten vid angiven anläggning*",
        description = "*Den här endpointen kommer inte returnera någon data då underliggande system inte tillhandahåller den information som ska returneras." +
                "Returnerar uppgifter om en anläggnings livsmedelsverksamhet. Med livsmedelsverksamhet avses huvudsaklig inriktning, aktiviteter, produktgrupper och eventuella tredjepartscertifieringar.")
    public ResponseEntity<Livsmedelsverksamhet> getLivsmedelsverksamhet(
        @Schema(description = "Anläggningsid för den anläggning som ska hämtas")
        @PathVariable String anlaggningsid) {
        return ResponseEntity.ok(service.getLivsmedelsverksamhet(anlaggningsid));
    }
    
    @GetMapping("/anlaggning/{anlaggningsid}/fakturering")
    @Operation(summary = "Hämta faktureringsuppgifter för en anläggning",
        description = "Returnerar faktureringsuppgifter för en anläggning.")
    public ResponseEntity<Faktura> getFakturering(
        @Schema(description = "Anläggningsid för den anläggning vars faktureringsuppgifter ska hämtas")
        @PathVariable String anlaggningsid) {
        return ResponseEntity.ok(service.getFakturering(anlaggningsid));
    }
    
}
