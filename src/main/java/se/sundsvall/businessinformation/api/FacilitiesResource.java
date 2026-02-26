package se.sundsvall.businessinformation.api;

import generated.se.sundsvall.forratt.Anlaggningar;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import se.sundsvall.businessinformation.service.ecos.FacilityService;
import se.sundsvall.dept44.common.validators.annotation.ValidMunicipalityId;
import se.sundsvall.dept44.problem.Problem;
import se.sundsvall.dept44.problem.violations.ConstraintViolationProblem;

import static org.springframework.http.MediaType.APPLICATION_PROBLEM_JSON_VALUE;
import static org.springframework.http.ResponseEntity.ok;

@RestController
@Validated
@RequestMapping("/{municipalityId}/anlaggningar")
@Tag(name = "Anlaggningar", description = "Anläggningar som står som registrerade och som inte har ett upphört datum ska kunna klassas om.")
class FacilitiesResource {

	private final FacilityService service;

	FacilitiesResource(final FacilityService service) {
		this.service = service;
	}

	@GetMapping("{organisationsnummer}")
	@Operation(summary = "Hämta aktiva företag/anläggningar med angivet organisationsnummer", description = "Returnerar aktiva anläggningar med angivet organisationsnummer", responses = {
		@ApiResponse(responseCode = "200", description = "Successful operation", useReturnTypeSchema = true),
		@ApiResponse(responseCode = "400", description = "Bad request", content = @Content(mediaType = APPLICATION_PROBLEM_JSON_VALUE, schema = @Schema(oneOf = {
			Problem.class, ConstraintViolationProblem.class
		}))),
		@ApiResponse(responseCode = "500", description = "Internal Server error", content = @Content(mediaType = APPLICATION_PROBLEM_JSON_VALUE, schema = @Schema(implementation = Problem.class)))
	})
	ResponseEntity<List<Anlaggningar>> getAnlaggningar(
		@Parameter(name = "municipalityId", description = "Municipality id", example = "2281") @ValidMunicipalityId @PathVariable final String municipalityId,
		@Parameter(description = "organisationsnummer för den eller de anläggningar som ska hämtas") @PathVariable final String organisationsnummer) {

		return ok(service.getFacilities(organisationsnummer));
	}
}
