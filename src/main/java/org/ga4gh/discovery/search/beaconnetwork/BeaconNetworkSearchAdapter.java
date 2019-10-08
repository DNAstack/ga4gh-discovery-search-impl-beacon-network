/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.ga4gh.discovery.search.beaconnetwork;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import io.debezium.antlr.CaseChangingCharStream;
import io.debezium.ddl.parser.mysql.generated.MySqlLexer;
import io.debezium.ddl.parser.mysql.generated.MySqlParser;
import io.debezium.ddl.parser.mysql.generated.MySqlParserBaseListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.ParseTreeWalker;
import org.apache.http.client.utils.URIBuilder;
import org.ga4gh.discovery.search.model.SchemaProperty;
import org.ga4gh.discovery.search.model.SearchAdapter;
import org.ga4gh.table.SchemaId;
import org.ga4gh.table.model.*;
import org.json.JSONException;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 *
 * @author mfiume
 */
@Service
public class BeaconNetworkSearchAdapter implements SearchAdapter {

    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(BeaconQueryParser.class);

    String BEACON_NETWORK_API = "https://beacon-network.org/api";
    ListTablesResponse tableInfo;

    private void printElement(JsonElement e) {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        logger.info(gson.toJson(e));
    }

    public static enum PROPERTIES {
        VARIANT_BEACONID {
            @Override
            public String toString() {
                return "beaconId";
            }
        },
        VARIANT_CHROMOSOME {
            @Override
            public String toString() {
                return "chromosome";
            }
        },
        VARIANT_POSITION {
            @Override
            public String toString() {
                return "position";
            }
        },
        VARIANT_REFERENCE_ALLELE {
            @Override
            public String toString() {
                return "referenceAllele";
            }
        },
        VARIANT_ALLELE {
            @Override
            public String toString() {
                return "allele";
            }
        },
        VARIANT_REFERENCE {
            @Override
            public String toString() {
                return "reference";
            }
        },
        VARIANT_EXISTS {
            @Override
            public String toString() {
                return "exists";
            }
        };
    }

    public BeaconNetworkSearchAdapter() {
        List<TableInfo> tables = new ArrayList<>();
        
        tables.add(getOrganizationTable());
        tables.add(getBeaconTable());
        tables.add(getVariantTable());
        
        tableInfo = new ListTablesResponse(tables);
    }

    private TableInfo getOrganizationTable() {
        TableInfo ti = new TableInfo();
        ti.setName("organizations");
        ti.setDescription("Organizations hosting Beacons registered to the network");
        ObjectMapper mapper = new ObjectMapper();
        JsonNode node = mapper.valueToTree(getOrganizationProperties());
        SchemaId sid = SchemaId.createIgnoringFragment(ti.getName());
        Schema s = new Schema(sid, node);
        ti.setSchema(s);
        return ti;
    }
    
    private TableInfo getBeaconTable() {
        TableInfo ti = new TableInfo();
        ti.setName("variants");
        ti.setDescription("Variants, their existence, and metadata about them");
        ObjectMapper mapper = new ObjectMapper();
        JsonNode node = mapper.valueToTree(getVariantProperties());
        SchemaId sid = SchemaId.createIgnoringFragment(ti.getName());
        Schema s = new Schema(sid, node);
        ti.setSchema(s);
        return ti;
    }
    
    private TableInfo getVariantTable() {
        TableInfo ti = new TableInfo();
        ti.setName("beacons");
        ti.setDescription("Beacons registered to the network");
        ObjectMapper mapper = new ObjectMapper();
        JsonNode node = mapper.valueToTree(getBeaconProperties());
        SchemaId sid = SchemaId.createIgnoringFragment(ti.getName());
        Schema s = new Schema(sid, node);
        ti.setSchema(s);
        return ti;
    }
    
    private Map<String, SchemaProperty> getOrganizationProperties() {
        Map<String, SchemaProperty> properties = new HashMap<>();
        int index = 0;
        properties.put("id", new SchemaProperty("int", index++));
        properties.put("name", new SchemaProperty("string", index++));
        properties.put("description", new SchemaProperty("string", index++));
        properties.put("createdDate", new SchemaProperty("date", index++));
        properties.put("url", new SchemaProperty("string", index++));
        properties.put("address", new SchemaProperty("string", index++));
        properties.put("logo", new SchemaProperty("string", index++));
        
        return properties;
    }
    
    private Map<String, SchemaProperty> getBeaconProperties() {
        Map<String, SchemaProperty> properties = new HashMap<>();
        int index = 0;
        properties.put("id", new SchemaProperty("int", index++));
        properties.put("name", new SchemaProperty("string", index++));
        properties.put("url", new SchemaProperty("string", index++));
        properties.put("organization", new SchemaProperty("string", index++));
        properties.put("description", new SchemaProperty("string", index++));
        properties.put("homepage", new SchemaProperty("string", index++));
        properties.put("email", new SchemaProperty("string", index++));
        properties.put("aggregator", new SchemaProperty("boolean", index++));
        properties.put("enabled", new SchemaProperty("boolean", index++));
        properties.put("visible", new SchemaProperty("boolean", index++));
        properties.put("createdDate", new SchemaProperty("date", index++));
        properties.put("supportedReferences", new SchemaProperty("array", index++));
        properties.put("aggregatedBeacons", new SchemaProperty("array", index++));
        
        return properties;
    }
    
    private Map<String, SchemaProperty> getVariantProperties() {
        Map<String, SchemaProperty> properties = new HashMap<>();
        int index = 0;
        properties.put(PROPERTIES.VARIANT_BEACONID.toString(), new SchemaProperty("string", index++));
        properties.put(PROPERTIES.VARIANT_CHROMOSOME.toString(), new SchemaProperty("string", index++));
        properties.put(PROPERTIES.VARIANT_POSITION.toString(), new SchemaProperty("long", index++));
        properties.put(PROPERTIES.VARIANT_REFERENCE_ALLELE.toString(), new SchemaProperty("string", index++));
        properties.put(PROPERTIES.VARIANT_ALLELE.toString(), new SchemaProperty("string", index++));
        properties.put(PROPERTIES.VARIANT_REFERENCE.toString(), new SchemaProperty("string", index++));
        properties.put(PROPERTIES.VARIANT_EXISTS.toString(), new SchemaProperty("boolean", index++));
        
        return properties;
    }

    @Override
    public ListTablesResponse getTables() {
        return tableInfo;
    }

    @Override
    public TableInfo getTableInfo(String name) {
        for (TableInfo table : tableInfo.getTables()) {
            if (table.getName().equals(name)) {
                return table;
            }
        }

        throw new IllegalArgumentException("Unknown table " + name);
    }

    @Override
    public TableData getTableData(String name) {
        return new TableData(null, null, null);
    }

    @Override
    public TableData doSearch(String sql) {

        logger.info("Parsing query " + sql);

        CharStream stream = CharStreams.fromString(sql);
        CaseChangingCharStream upper = new CaseChangingCharStream(stream, true);
        MySqlLexer mySqlLexer = new MySqlLexer(upper);
        CommonTokenStream tokens = new CommonTokenStream(mySqlLexer);
        MySqlParser mySqlParser = new MySqlParser(tokens);
        ParseTree tree = mySqlParser.dmlStatement();
        BeaconQueryParser bqp = new BeaconQueryParser();
        MySqlParserBaseListener listener = bqp;
        ParseTreeWalker.DEFAULT.walk(listener, tree);

        System.out.println(bqp.getBeaconQuery());

        if (bqp.isValid()) {
            try {
                BeaconQuery beaconQuery = bqp.getBeaconQuery();
                System.out.println(beaconQuery);

                URIBuilder builder = new URIBuilder(BEACON_NETWORK_API + "/responses");
                builder.setParameter("allele", beaconQuery.getAllele())
                        .setParameter("beacon", beaconQuery.getBeaconId())
                        .setParameter("chrom", beaconQuery.getChromosome())
                        .setParameter("pos", beaconQuery.getPosition().toString())
                        .setParameter("ref", beaconQuery.getReference());

                URI uri = builder.build();
                System.out.println(uri.toString());
                
                JsonElement json = readJsonFromUrl(uri.toString());
                
                printElement(json);
                
                JsonElement root = json.getAsJsonArray().get(0);
                
                Map<String, SchemaProperty> properties = new HashMap<>();
                int index = 0;
                properties.put("beacon", new SchemaProperty("json", index++));
                properties.put("query", new SchemaProperty("json", index++));
                properties.put("response", new SchemaProperty("json", index++));
                properties.put("authHint", new SchemaProperty("json", index++));
                properties.put("fullBeaconResponse", new SchemaProperty("json", index++));
                
                ObjectMapper mapper = new ObjectMapper();
                JsonNode node = mapper.valueToTree(properties);
                SchemaId sid = SchemaId.createIgnoringFragment("beacon-network-result");
                Schema s = new Schema(sid, node);
                
                List<Map<String, Object>> objects = new ArrayList<>();
                Map<String, Object> map = new HashMap<>();
                
                map.put("beacon", fetchChildAsString(root,"beacon"));
                map.put("query", fetchChildAsString(root,"query"));
                map.put("response", fetchChildAsString(root,"response"));
                map.put("authHint", fetchChildAsString(root,"authHint"));
                map.put("fullBeaconResponse", fetchChildAsString(root,"fullBeaconResponse"));
                objects.add(map);
                
                TableData td = new TableData(s, objects, null);
                
                return td;
                
            } catch (URISyntaxException | IOException | JSONException ex) {
                ex.printStackTrace();
                throw new RuntimeException("Problem contacting the Beacon Network");
            }
        }

        throw new RuntimeException("Problem parsing Beacon query");
    }
    
    private String fetchChildAsString(JsonElement root, String childName) {
        JsonElement child = root.getAsJsonObject().get(childName);
        if (child == null) { 
            return null;
        } else {
            return child.toString();
        }
    }

    private static String readAll(Reader rd) throws IOException {
        StringBuilder sb = new StringBuilder();
        int cp;
        while ((cp = rd.read()) != -1) {
            sb.append((char) cp);
        }
        return sb.toString();
    }

    public static JsonElement readJsonFromUrl(String urlStr) throws IOException, JSONException {
        
        URL url = new URL(urlStr);
        
        logger.info("Requesting " + urlStr);
                
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        // Enable output for the connection.
        conn.setDoOutput(true);
        conn.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
        conn.setRequestProperty("Accept", "application/json");
        
        InputStream is = conn.getInputStream();
        try {
            BufferedReader rd = new BufferedReader(new InputStreamReader(is, Charset.forName("UTF-8")));
            //String jsonText = "{ \"results\" : [ " + readAll(rd) + " ] }";
            //logger.info("Received " + jsonText);
            JsonElement je = new JsonParser().parse(readAll(rd));
            
            return je;
        } finally {
            is.close();
        }
    }
}
