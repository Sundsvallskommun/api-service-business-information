package se.sundsvall.businessinformation.integration.ecos;

import generated.se.sundsvall.forratt.Anlaggning;
import generated.se.sundsvall.forratt.Anlaggningar;
import generated.se.sundsvall.forratt.Faktura;
import generated.se.sundsvall.forratt.Kontaktperson;
import generated.se.sundsvall.forratt.Livsmedelsverksamhet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.Predicate;
import minutmiljo.ArrayOfFacilityFilterSvcDto;
import minutmiljo.ArrayOfguid;
import minutmiljo.ConnectedRoleSvcDto;
import minutmiljo.FacilityFacilityStatusIdsFilterSvcDto;
import minutmiljo.FacilityFacilityTypeIdsFilterSvcDto;
import minutmiljo.FacilityPartyOrganizationNumberFilterSvcDto;
import minutmiljo.FoodFacilityInfoSvcDto;
import minutmiljo.GetFacilityPartyRoles;
import minutmiljo.GetFoodFacilities;
import minutmiljo.GetFoodFacilitiesSvcDto;
import minutmiljo.GetParty;
import minutmiljo.OrganizationSvcDto;
import minutmiljo.PartySvcDto;
import minutmiljo.PersonSvcDto;
import minutmiljo.SearchFacility;
import minutmiljo.SearchFacilityResultSvcDto;
import minutmiljo.SearchFacilitySvcDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Component;
import org.zalando.problem.Problem;
import se.sundsvall.businessinformation.integration.ecos.configuration.EcosProperties;
import se.sundsvall.businessinformation.integration.ecos.model.FacilityStatus;
import se.sundsvall.businessinformation.integration.ecos.model.FacilityType;

import static org.zalando.problem.Status.BAD_REQUEST;
import static se.sundsvall.dept44.util.LogUtils.sanitizeForLogging;

@Component
@EnableConfigurationProperties(EcosProperties.class)
public class EcosIntegration {

	private static final String INVOICE_ROLE = "480E2731-1F2F-4F35-8A37-FDDE957E9CD0";
	private static final String ERROR_MSG = "Could not find a facility in Ecos with anlaggningsid: %s";
	private static final Logger LOG = LoggerFactory.getLogger(EcosIntegration.class);
	private final EcosClient client;

	public EcosIntegration(final EcosClient client) {
		this.client = client;
	}

	private static <T> Predicate<T> distinctByKey(final Function<? super T, ?> keyExtractor) {
		final Set<Object> seen = ConcurrentHashMap.newKeySet();
		return t -> seen.add(keyExtractor.apply(t));
	}

	public List<Anlaggningar> getFacilities(final String orgNr) {

		final var facilityTypeFilter = new FacilityFacilityTypeIdsFilterSvcDto()
			.withFacilityTypeIds(FacilityType.LIVSMEDELSANLAGGNING.getValue());

		final var facilityStatusFilter = new FacilityFacilityStatusIdsFilterSvcDto()
			.withFacilityStatusIds(new ArrayOfguid().withGuid(
				FacilityStatus.ANMALD.getValue(),
				FacilityStatus.INAKTIV.getValue(),
				FacilityStatus.AKTIV.getValue(),
				FacilityStatus.BEVILJAD.getValue()));

		final var orgFilter = new FacilityPartyOrganizationNumberFilterSvcDto()
			.withOrganizationNumber(formatOrganizationNumber(orgNr));

		return EcosMapper.toDto(client
			.searchFacility(new SearchFacility()
				.withSearchFacilitySvcDto(new SearchFacilitySvcDto()
					.withFacilityFilters(new ArrayOfFacilityFilterSvcDto()
						.withFacilityFilterSvcDto(facilityStatusFilter, facilityTypeFilter, orgFilter)))));
	}

	private String formatOrganizationNumber(final String organizationNumber) {

		// Remove all non digits
		final String cleanNumber = organizationNumber.replaceAll("\\D", "");

		return switch (cleanNumber.length()) {
			case 12 -> "%s-%s".formatted(cleanNumber.substring(0, 8), cleanNumber.substring(8));
			case 10 -> "16%s-%s".formatted(cleanNumber.substring(0, 6), cleanNumber.substring(6));
			default -> throw Problem.valueOf(BAD_REQUEST, "organizationNumber must consist of 10 or 12 digits with the last four seperated with a hyphen");
		};
	}

	public Anlaggning getFacility(final String anlaggningsid) {

		final var foodFacility = Optional.ofNullable(getFoodFacility(anlaggningsid));
		final var searchFacility = foodFacility
			.map(FoodFacilityInfoSvcDto::getFacilityName)
			.map(name -> searchFacilityWithFacilityName(name, anlaggningsid))
			.orElseGet(() -> searchFacilityWithFacilityCollectionName(foodFacility
				.orElseThrow(() -> Problem.valueOf(BAD_REQUEST, ERROR_MSG.formatted(anlaggningsid)))
				.getFacilityCollectionName(), anlaggningsid));

		final var kontaktPersoner = getKontaktpersoner(getFacilityPartyRoles(anlaggningsid));

		return EcosMapper.toAnlaggning(searchFacility, kontaktPersoner);
	}

	private List<Kontaktperson> getKontaktpersoner(final List<ConnectedRoleSvcDto> partyRoles) {
		return partyRoles.stream()
			.distinct()
			.filter(distinctByKey(ConnectedRoleSvcDto::getPartyId))
			.map(this::getKontaktperson).toList();
	}

	private Kontaktperson getKontaktperson(final ConnectedRoleSvcDto connectedRoleSvcDto) {
		final var party = getParty(connectedRoleSvcDto.getPartyId());
		return switch (party) {
			case final PersonSvcDto personSvcDto -> EcosMapper.fromPersonDto(personSvcDto);
			case final OrganizationSvcDto organizationSvcDto -> EcosMapper.fromOrganizationDto(organizationSvcDto);
			default -> throw new IllegalStateException("Unexpected value: " + party);
		};
	}

	private SearchFacilityResultSvcDto searchFacilityWithFacilityName(final String facilityName, final String anlaggningsid) {
		return client
			.searchFacility(EcosMapper.toSearchFacilityRequest(facilityName))
			.getSearchFacilityResult()
			.getSearchFacilityResultSvcDto()
			.stream()
			.filter(a -> a.getFacilityId().equalsIgnoreCase(anlaggningsid))
			.findFirst()
			.orElseThrow(() -> Problem.valueOf(BAD_REQUEST, ERROR_MSG.formatted(anlaggningsid)));
	}

	private SearchFacilityResultSvcDto searchFacilityWithFacilityCollectionName(final String facilityCollectionName, final String anlaggningsid) {
		return client
			.searchFacility(EcosMapper.toSearchFacilityRequest(facilityCollectionName))
			.getSearchFacilityResult()
			.getSearchFacilityResultSvcDto()
			.stream()
			.filter(a -> a.getFacilityId().equalsIgnoreCase(anlaggningsid))
			.findFirst()
			.orElseThrow(() -> Problem.valueOf(BAD_REQUEST, ERROR_MSG.formatted(anlaggningsid)));
	}

	private FoodFacilityInfoSvcDto getFoodFacility(final String anlaggningsid) {
		try {
			return client.getFoodFacilities(new GetFoodFacilities()
				.withRequest(new GetFoodFacilitiesSvcDto()
					.withFacilityIds(new ArrayOfguid()
						.withGuid(anlaggningsid))))
				.getGetFoodFacilitiesResult()
				.getFoodFacilityInfoSvcDto()
				.getFirst();
		} catch (final Exception e) {
			throw Problem.valueOf(BAD_REQUEST, ERROR_MSG.formatted(anlaggningsid));
		}
	}

	private List<ConnectedRoleSvcDto> getFacilityPartyRoles(final String anlaggningsid) {
		return client.getFacilityPartyRoles(new GetFacilityPartyRoles().withFacilityId(anlaggningsid))
			.getGetFacilityPartyRolesResult()
			.getConnectedRoleSvcDto();
	}

	private PartySvcDto getParty(final String partyId) {
		return client.getParty(new GetParty().withPartyId(partyId)).getGetPartyResult();
	}

	public Livsmedelsverksamhet getLivsmedelsverksamhet(final String anlaggningsid) {
		LOG.info("Fetching Livsmedelsverksamhet for anlaggningsid: {}", sanitizeForLogging(anlaggningsid));
		return EcosMapper.toLivsmedelsverksamhet();
	}

	public Faktura getFakturering(final String anlaggningsid) {
		final var facilityName = getFoodFacility(anlaggningsid).getFacilityName();

		final var partyResponses = getFacilityPartyRoles(anlaggningsid)
			.stream()
			.filter(dto -> INVOICE_ROLE.equalsIgnoreCase(dto.getRoleId()))
			.map(dto -> getParty(dto.getPartyId()))
			.toList();

		return EcosMapper.toFaktura(partyResponses, facilityName);
	}
}
