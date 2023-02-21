package se.sundsvall.businessinformation.integration.ecos.model;

public enum FacilityStatus {
   ANMALD("88E11CAA-DF35-4C5E-94A8-3C7B0369D8F2"),
   INAKTIV("64B2DB7A-9A11-4F20-A57C-8122B1A469E6"),
   AKTIV("D203BB33-EB9A-4679-8E1C-BBD8AF86E554"),
   BEVILJAD("C5A98B2B-C2B8-428E-B597-A3F97A77B818"),
   UPPHORD("9A748E4E-BD7E-481A-B449-73CBD0992213"),
   MAKULERAD("80FFA45C-B3DF-4A10-8DB3-A042F36C64B7");
   
   private final String label;
   
   FacilityStatus(final String label) {
      this.label = label;
   }
   
 public String getValue(){
      return label;
  }
}