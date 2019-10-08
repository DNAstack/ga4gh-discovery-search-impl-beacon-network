package org.ga4gh.discovery.search.model;

import org.ga4gh.table.model.*;

/**
 *
 * @author mfiume
 */
public interface SearchAdapter {

    public ListTablesResponse getTables();

    public TableInfo getTableInfo(String name);

    public TableData getTableData(String name);

    public TableData doSearch(String sql);
}
