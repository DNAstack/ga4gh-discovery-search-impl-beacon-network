/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.ga4gh.discovery.search.model;

import java.util.Map;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class Schema
{
    private SchemaId schemaId;
    
    private Map<String, SchemaProperty> schemaJson;

    
    public SchemaId getSchemaId ()
    {
        return schemaId;
    }

    public void setSchemaId (SchemaId schemaId)
    {
        this.schemaId = schemaId;
    }

    public Map<String, SchemaProperty> getSchemaJson ()
    {
        return schemaJson;
    }

    public void setSchemaJson (Map<String, SchemaProperty> schemaJson)
    {
        this.schemaJson = schemaJson;
    }

    @Override
    public String toString()
    {
        return "Schema [schemaId = "+schemaId+", schemaJson = "+schemaJson+"]";
    }
}