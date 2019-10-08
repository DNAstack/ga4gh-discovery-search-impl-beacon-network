package org.ga4gh.discovery.search;

import org.ga4gh.discovery.search.beaconnetwork.BeaconNetworkSearchAdapter;
import org.ga4gh.discovery.search.model.Query;
import org.ga4gh.table.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;

@RestController
public class SearchController {
    
    @Autowired
    BeaconNetworkSearchAdapter adapter;
    
    @RequestMapping("/tables")
    @CrossOrigin(origins = "*")
    public ListTablesResponse getTables() {
        return adapter.getTables();
    }
    
    @RequestMapping("/table/{name}/info")
    @CrossOrigin(origins = "*")
    public TableInfo getTableInfo(@PathVariable String name) {
        return adapter.getTableInfo(name);
    }
    
    @RequestMapping("/table/{name}/data")
    @CrossOrigin(origins = "*")
    public TableData getTableData(@PathVariable String name) {
        return adapter.getTableData(name);
    }
    
    @PostMapping("/search")
    @CrossOrigin(origins = "*")
    public TableData doSearch(@RequestBody Query query) {
        return adapter.doSearch(query.getQuery());
    }
}
