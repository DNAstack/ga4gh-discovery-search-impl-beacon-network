/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.ga4gh.discovery.search.beaconnetwork;

/**
 *
 * @author mfiume
 */
public class BeaconQuery {

    private String beaconId;
    private String chromosome;
    private Long position;
    private String referenceAllele;
    private String allele;
    private String reference;
    
    void setChromosome(String chromosome) {
        this.chromosome = chromosome;
    }
    
    public void setBeaconId(String beaconId) {
        this.beaconId = beaconId;
    }

    public void setPosition(Long position) {
        this.position = position;
    }

    public void setReferenceAllele(String referenceAllele) {
        this.referenceAllele = referenceAllele;
    }

    public void setAllele(String allele) {
        this.allele = allele;
    }

    public void setReference(String reference) {
        this.reference = reference;
    }

    public String getBeaconId() {
        return beaconId;
    }

    public String getChromosome() {
        return chromosome;
    }

    public Long getPosition() {
        return position;
    }

    public String getReferenceAllele() {
        return referenceAllele;
    }

    public String getAllele() {
        return allele;
    }

    public String getReference() {
        return reference;
    }
    
    

    @Override
    public String toString() {
        return "BeaconQuery{" + "beaconId=" + beaconId + ", chromosome=" + chromosome + ", position=" + position + ", referenceAllele=" + referenceAllele + ", allele=" + allele + ", reference=" + reference + '}';
    }
    
}
