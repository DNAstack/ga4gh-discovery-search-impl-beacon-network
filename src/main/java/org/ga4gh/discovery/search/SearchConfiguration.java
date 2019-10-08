/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.ga4gh.discovery.search;

import org.ga4gh.discovery.search.beaconnetwork.BeaconNetworkSearchAdapter;
import org.ga4gh.discovery.search.model.SearchAdapter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 *
 * @author mfiume
 */
@Configuration
public class SearchConfiguration {
  
  @Bean
  public SearchAdapter searchAdapter(){
    return new BeaconNetworkSearchAdapter();
  }

}