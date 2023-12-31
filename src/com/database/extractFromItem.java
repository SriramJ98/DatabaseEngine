package com.database;

import com.database.selection.extractSubSelect;
import com.database.table.tableDetail;
import net.sf.jsqlparser.schema.Table;
import net.sf.jsqlparser.statement.select.FromItem;
import net.sf.jsqlparser.statement.select.SubSelect;

public class extractFromItem extends baseClass {

    baseClass res = null;

    public extractFromItem(FromItem fm, tableList tl) throws Exception {

        tbitem(fm, tl);


    }

    private void tbitem(FromItem fm, tableList tl) throws Exception {
        if(fm instanceof Table) {
            res = new extractTable(fm, tl);
        }

        if(fm instanceof SubSelect) {
            SubSelect ss = (SubSelect) fm;
            res = new extractSubSelect(ss, tl);
        }
    }

    @Override
    public boolean isNext() throws Exception {
        if(!res.isNext() || res == null)
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
        return res.get();
    }


}

