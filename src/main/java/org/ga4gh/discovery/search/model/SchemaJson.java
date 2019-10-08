/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.ga4gh.discovery.search.model;

import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

/**
 *
 * @author mfiume
 */
@RequiredArgsConstructor
@AllArgsConstructor
public class SchemaJson
{
    
    @NonNull
    private Map<String, SchemaProperty> properties;
    
    private String description;

    public String getDescription ()
    {
        return description;
    }

    public void setDescription (String description)
    {
        this.description = description;
    }

    public Map<String, SchemaProperty> getProperties() {
        return properties;
    }

    public void setProperties(Map<String, SchemaProperty> properties) {
        this.properties = properties;
    }
    
    @Override
    public String toString()
    {
        return "ClassPojo [description = "+description+", properties = "+properties+"]";
    }
}