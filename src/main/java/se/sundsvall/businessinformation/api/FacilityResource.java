package se.sundsvall.businessinformation.api;

import static org.springframework.http.MediaType.APPLICATION_PROBLEM_JSON_VALUE;
import static org.springframework.http.ResponseEntity.ok;

import generated.se.sundsvall.forratt.Anlaggning;
import generated.se.sundsvall.forratt.Faktura;
import generated.se.sundsvall.forratt.Livsmedelsverksamhet;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.zalando.problem.Problem;
import org.zalando.problem.violations.ConstraintViolationProblem;
import se.sundsvall.businessinformation.service.ecos.FacilityService;
import se.sundsvall.dept44.common.validators.annotation.ValidMunicipalityId;

@RestController
@Validated
@RequestMapping("/{municipalityId}/anlaggning")
@Tag(name = "anlaggningsuppgifter", description = "Information om en anläggning")
public class FacilityResource {

	private final FacilityService service;

	public FacilityResource(final FacilityService service) {
		this.service = service;
	}

	@GetMapping("/{anlaggningsid}")
	@Operation(summary = "Hämta uppgifter om anläggningen med angivet anläggningsid", description = "Returnerar uppgifter om en anläggning")
	@ApiResponse(responseCode = "200", description = "Successful operation", useReturnTypeSchema = true)
	@ApiResponse(responseCode = "400", description = "Bad request", content = @Content(mediaType = APPLICATION_PROBLEM_JSON_VALUE, schema = @Schema(oneOf = {
		Problem.class, ConstraintViolationProblem.class
	})))
	@ApiResponse(responseCode = "500", description = "Internal Server error", content = @Content(mediaType = APPLICATION_PROBLEM_JSON_VALUE, schema = @Schema(implementation = Problem.class)))
	public ResponseEntity<Anlaggning> getAnlaggning(
		@Parameter(name = "municipalityId", description = "Municipality id", example = "2281") @ValidMunicipalityId @PathVariable final String municipalityId,
		@Schema(description = "Anläggningsid för den anläggning som ska hämtas") @PathVariable final String anlaggningsid) {

		return ok(service.getFacility(anlaggningsid));
	}

	@GetMapping("/{anlaggningsid}/livsmedelsverksamhet")
	@Operation(
		summary = "Hämta uppgifter om livsmedelsverksamheten vid angiven anläggning*",
		description = """
			*Den här endpointen kommer inte returnera någon data då underliggande system inte tillhandahåller den information som ska returneras.
			"Returnerar uppgifter om en anläggnings livsmedelsverksamhet. Med livsmedelsverksamhet avses huvudsaklig inriktning, aktiviteter, produktgrupper och eventuella tredjepartscertifieringar.""")
	@ApiResponse(responseCode = "200", description = "Successful operation", useReturnTypeSchema = true)
	@ApiResponse(responseCode = "400", description = "Bad request", content = @Content(mediaType = APPLICATION_PROBLEM_JSON_VALUE, schema = @Schema(oneOf = {
		Problem.class, ConstraintViolationProblem.class
	})))
	@ApiResponse(responseCode = "500", description = "Internal Server error", content = @Content(mediaType = APPLICATION_PROBLEM_JSON_VALUE, schema = @Schema(implementation = Problem.class)))
	public ResponseEntity<Livsmedelsverksamhet> getLivsmedelsverksamhet(
		@Parameter(name = "municipalityId", description = "Municipality id", example = "2281") @ValidMunicipalityId @PathVariable final String municipalityId,
		@Schema(description = "Anläggningsid för den anläggning som ska hämtas") @PathVariable final String anlaggningsid) {

		return ok(service.getLivsmedelsverksamhet(anlaggningsid));
	}

	@GetMapping("/{anlaggningsid}/fakturering")
	@Operation(summary = "Hämta faktureringsuppgifter för en anläggning", description = "Returnerar faktureringsuppgifter för en anläggning.")
	@ApiResponse(responseCode = "200", description = "Successful operation", useReturnTypeSchema = true)
	@ApiResponse(responseCode = "400", description = "Bad request", content = @Content(mediaType = APPLICATION_PROBLEM_JSON_VALUE, schema = @Schema(oneOf = {
		Problem.class, ConstraintViolationProblem.class
	})))
	@ApiResponse(responseCode = "500", description = "Internal Server error", content = @Content(mediaType = APPLICATION_PROBLEM_JSON_VALUE, schema = @Schema(implementation = Problem.class)))
	public ResponseEntity<Faktura> getFakturering(
		@Parameter(name = "municipalityId", description = "Municipality id", example = "2281") @ValidMunicipalityId @PathVariable final String municipalityId,
		@Schema(description = "Anläggningsid för den anläggning vars faktureringsuppgifter ska hämtas") @PathVariable final String anlaggningsid) {

		return ok(service.getFakturering(anlaggningsid));
	}
}
