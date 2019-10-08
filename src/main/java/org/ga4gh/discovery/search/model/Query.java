/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.ga4gh.discovery.search.model;

import lombok.Data;

@Data
public class Query {

  private String query;

  public Query() {}

  public Query(String query) {
    this.query = query;
  }
  
  public String getQuery() {
      return query;
  }
}
