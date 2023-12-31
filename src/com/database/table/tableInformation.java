package com.database.table;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import net.sf.jsqlparser.schema.Column;
import net.sf.jsqlparser.schema.Table;
import net.sf.jsqlparser.statement.create.table.ColumnDefinition;

public class tableInformation {
    public Table t = new Table();
    public List<ColumnDefinition> cd = new  ArrayList<>();
    public Map<String, ColumnDefinition > colCD = new LinkedHashMap<>();
    public List<Column> col = new ArrayList<>();

    public tableInformation(Table t, List<ColumnDefinition> CD) {
        this.t = t;

        int index = 0;
        tdcol(t, CD);
    }

    private void tdcol(Table t, List<ColumnDefinition> CD) {
        tabcd(CD, t);
    }

    public tableInformation(Table t, Collection<ColumnDefinition> CD) {
        this.t = t;

        int index = 0;
        tabcd(CD, t);
    }

    private void tabcd(Collection<ColumnDefinition> CD, Table t) {
        for(ColumnDefinition temp: CD) {
            Column c = new Column(t, temp.getColumnName());
            colCD.put(c.getColumnName(), temp);
            cd.add(temp);
            col.add(c);
        }
    }

    public boolean hasColumn(String colName) {
        for (Column c: this.col) {
            if(c.getColumnName().equals(colName)) {
                return true;
            }
        }
        return false;

    }

}
