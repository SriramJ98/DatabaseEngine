package com.database.selection;

import java.util.Map;

import com.database.baseClass;
import com.database.table.tableDetail;
import com.database.tableList;
import net.sf.jsqlparser.expression.PrimitiveValue;
import net.sf.jsqlparser.schema.Column;
import net.sf.jsqlparser.schema.Table;
import net.sf.jsqlparser.statement.select.SubSelect;

public class extractSubSelect extends baseClass {

    baseClass res = null;
    tableList tl = null;
    tableDetail td = null;
    SubSelect ss = null;


    public extractSubSelect(SubSelect ss, tableList tl) throws Exception {
        this.tl = tl;
        this.ss = ss;
        res = new extractSelectBody(tl, ss.getSelectBody(), null);
    }

    public extractSubSelect(tableList tl, SubSelect ss, Map<Column, PrimitiveValue> colResult) throws Exception {
        this.tl = tl;
        this.ss = ss;
        res = new extractSelectBody(tl,ss.getSelectBody(),colResult);
    }

    @Override
    public boolean isNext() throws Exception {
        if(res == null || !res.isNext())
            return false;
        else
            return true;
    }

    @Override
    public void clear() {
        res.clear();
    }

    @Override
    public tableDetail get() throws Exception {
        tableDetail oldtd = res.get();
        if(td == null) {
            td = new tableDetail();
            addSubSelectInfo(oldtd,td);
        }
        td.rowResult = oldtd.rowResult;
        return td;
    }


    private void addSubSelectInfo(tableDetail oldtd, tableDetail td) {
        String tableAliasName = ss.getAlias();
        td.table.add(new Table(tableAliasName));
        td.tableAlias.put(tableAliasName.toUpperCase(),tableAliasName.toUpperCase());
        for(Map.Entry<Column, Integer> e : oldtd.colAliasTable.entrySet()) {
            Column c = new Column(new Table(tableAliasName), e.getKey().getColumnName());
            td.colAliasTable.put(c, e.getValue());
        }
        td.colCD = oldtd.colCD;
    }

}
