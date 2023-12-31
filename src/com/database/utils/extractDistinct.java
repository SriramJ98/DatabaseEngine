package com.database.utils;

import com.database.baseClass;
import com.database.rowClass;
import com.database.table.tableDetail;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class extractDistinct extends baseClass {

    Iterator<rowClass> it = null;
    List<rowClass> rowResult = null;
    tableDetail td = null;

    public extractDistinct(baseClass res) throws Exception {
        List<rowClass> resrow = new ArrayList<>();
        while (res.isNext()) {
            tableDetail temp = res.get();
            if (td == null) {
                td = new tableDetail();
                td.colAliasTable.putAll(temp.colAliasTable);
                td.colCD.putAll(temp.colCD);
                td.table.addAll(temp.table);
                td.tableAlias.putAll(temp.tableAlias);
            }
            resrow.addAll(temp.rowResult);
        }
        rowResult = solveDistinct(resrow);
        it = rowResult.iterator();
    }

    private static List<rowClass> solveDistinct(List<rowClass> pvl) {
        List<rowClass> distinctpvl = new ArrayList<>();
        Goto1: for (rowClass pv1 : pvl) {
            Goto2: for (rowClass pv2 : distinctpvl) {
                if (!pv1.equals(pv2)) {
                    continue Goto2;
                }
                continue Goto1;
            }
            distinctpvl.add(pv1);
        }

        return distinctpvl;
    }







    @Override
    public tableDetail get() {
        td.rowResult = new ArrayList<>();
        td.rowResult.add(it.next());
        return td;
    }

    @Override
    public boolean isNext() {
        return it.hasNext();
    }

    @Override
    public void clear() {
        it = rowResult.iterator();
    }



}
