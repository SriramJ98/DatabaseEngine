package com.database.utils;

import java.util.ArrayList;
import java.util.List;

import com.database.baseClass;
import com.database.rowClass;
import com.database.table.tableDetail;
import net.sf.jsqlparser.statement.select.Limit;
import net.sf.jsqlparser.statement.select.PlainSelect;

public class extractLimit extends baseClass {

    Limit l;
    long pos = 0;
    List<rowClass> rowResult = new ArrayList<>();
    baseClass res = null;

    public extractLimit(PlainSelect ps, baseClass res) {
        this.l = ps.getLimit();
        this.res = res;
    }




    @Override
    public boolean isNext() throws Exception {
        if (pos == l.getRowCount() || res == null || !res.isNext()) {
            return false;
        }
        else
            return true;
    }


    @Override
    public void clear() {

        pos =0;
        res.clear();
    }

    @Override
    public tableDetail get() throws Exception {
        pos++;
        return res.get();
    }


}

