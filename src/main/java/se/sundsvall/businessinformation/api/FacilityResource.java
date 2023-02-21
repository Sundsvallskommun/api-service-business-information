package se.sundsvall.businessinformation.api;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import se.sundsvall.businessinformation.service.ecos.FacilityService;

import generated.se.sundsvall.forratt.Anlaggningar;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@Validated
@RequestMapping("/anlaggningar")
@Tag(name = "Anlaggningar", description = "Anläggningar som står som registrerade och som inte har ett upphört datum ska kunna klassas om.")
public class FacilityResource {
    
    private final FacilityService service;
    
    public FacilityResource(FacilityService service) {
        this.service = service;
    }
    
    
    @GetMapping("{organisationsnummer}")
    @Operation(summary = "Hämta aktiva företag/anläggningar med angivet organisationsnummer",
        description = "Returnerar aktiva anläggningar med angivet organisationsnummer")
    public ResponseEntity<List<Anlaggningar>> getAnlaggningar(
        @Schema(description = "organisationsnummer för den eller de anläggningar som ska hämtas")
        @PathVariable String organisationsnummer) {
        return ResponseEntity.ok(service.getFacilities(organisationsnummer));
    }
}
