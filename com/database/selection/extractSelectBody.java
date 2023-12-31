package com.database.selection;

import java.util.Map;
import com.database.baseClass;
import com.database.table.tableDetail;
import com.database.tableList;
import net.sf.jsqlparser.expression.PrimitiveValue;
import net.sf.jsqlparser.schema.Column;
import net.sf.jsqlparser.statement.select.PlainSelect;
import net.sf.jsqlparser.statement.select.SelectBody;

public class extractSelectBody extends baseClass {

    baseClass res = null;


    public extractSelectBody(tableList tl, SelectBody sb, Map<Column, PrimitiveValue> colResult) throws Exception {
        if(sb instanceof PlainSelect) {
            res = new extractPlainSelect(tl,sb,colResult);
        }
    }

    @Override
    public boolean isNext() throws Exception {
        if(!res.isNext() || res == null) {
            return false;
        }
        else
            return true;
    }

    @Override
    public void clear() {
        res.clear();

    }

    @Override
    public tableDetail get() throws Exception {
        return res.get();

    }

}
