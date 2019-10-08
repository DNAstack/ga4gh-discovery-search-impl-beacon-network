/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.ga4gh.discovery.search.model;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

/**
 *
 * @author mfiume
 */
@RequiredArgsConstructor
@AllArgsConstructor
public class SchemaProperty {
    
    @NonNull
    private String type;

    @JsonProperty("x-ga4gh-position")
    private int ga4gh_position;

    public String getType ()
    {
        return type;
    }

    public void setType (String type)
    {
        this.type = type;
    }

    @JsonGetter("x-ga4gh-position")
    public int getGa4ghPosition ()
    {
        return ga4gh_position;
    }

    public void setGa4ghPosition(int ga4gh_position)
    {
        this.ga4gh_position = ga4gh_position;
    }

    @Override
    public String toString()
    {
        return "SchemaProperty [type = "+type+", x-ga4gh-position = "+ga4gh_position+"]";
    }
}