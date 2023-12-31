package com.database.utils;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import com.database.*;
import com.database.table.tableDetail;
import net.sf.jsqlparser.eval.Eval;
import net.sf.jsqlparser.expression.PrimitiveValue;
import net.sf.jsqlparser.statement.select.OrderByElement;
import net.sf.jsqlparser.statement.select.PlainSelect;

public class extractOrderBy extends baseClass {

    Iterator<rowClass> it = null;
    List<rowClass> rowResult = null;
    tableDetail td = null;
    tableList tl = null;
    List<OrderByElement> o;

    public extractOrderBy(tableList tl, PlainSelect ps, baseClass res) throws Exception {
        this.tl = tl;
        this.o = ps.getOrderByElements();
        rowResult = new ArrayList<>();

        while (res.isNext()) {
            tableDetail temp = res.get();
            if (td == null) {
                td = new tableDetail();
                td.colAliasTable.putAll(temp.colAliasTable);
                td.colCD.putAll(temp.colCD);
                td.table.addAll(temp.table);
                td.tableAlias.putAll(temp.tableAlias);
            }
            rowResult.addAll(temp.rowResult);
        }
        rowResult.sort(new compareClass());
        it = rowResult.iterator();
    }

    private class compareClass implements Comparator<rowClass> {

        @Override
        public int compare(rowClass pv1,rowClass pv2) {
            int com = 0;
            for (OrderByElement obe : o) {
                try {
                    List<rowClass> temp1 = new ArrayList<>();
                    temp1.add(pv1);
                    List<rowClass> temp2 = new ArrayList<>();
                    temp2.add(pv2);
                    Eval evaluator_1 = new evaluator(td,tl,temp1,null);
                    Eval e2 = new evaluator(td,tl,temp2,null);
                    PrimitiveValue pvres1 = evaluator_1.eval(obe.getExpression());
                    PrimitiveValue pvres2 = e2.eval(obe.getExpression());
                    switch (pvres1.getType()) {
                        case DOUBLE: {
                            if (pvres1.toDouble() < pvres2.toDouble())
                                com = -1;
                            else if (pvres1.toDouble() > pvres2.toDouble())
                                com = 1;
                            break;
                        }
                        case LONG: {
                            if (pvres1.toLong() < pvres2.toLong())
                                com = -1;
                            else if (pvres1.toLong() > pvres2.toLong())
                                com = 1;
                            break;
                        }
                        default:
                            com = pvres1.toRawString().compareToIgnoreCase(pvres2.toRawString());
                    }

                    if (com != 0) {
                        if (!obe.isAsc()) {
                            return com * -1;
                        }
                        return com;
                    }
                } catch (Exception ex) {

                }
            }
            return com;
        }

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
