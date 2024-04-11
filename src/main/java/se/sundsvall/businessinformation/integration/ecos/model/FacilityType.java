package se.sundsvall.businessinformation.integration.ecos.model;

public enum FacilityType {

	LIVSMEDELSANLAGGNING("4958BC00-76E8-4D5B-A862-AAF8E815202A");

	private final String label;

	FacilityType(final String label) {
		this.label = label;
	}

	public String getValue() {
		return label;
	}
}
