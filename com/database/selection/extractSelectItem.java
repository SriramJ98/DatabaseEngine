package com.database.selection;

import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;

import com.database.*;
import com.database.table.tableDetail;
import com.database.table.tableInformation;
import net.sf.jsqlparser.eval.Eval;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.Function;
import net.sf.jsqlparser.expression.PrimitiveValue;
import net.sf.jsqlparser.schema.Column;
import net.sf.jsqlparser.statement.select.AllColumns;
import net.sf.jsqlparser.statement.select.AllTableColumns;
import net.sf.jsqlparser.statement.select.PlainSelect;
import net.sf.jsqlparser.statement.select.SelectExpressionItem;
import net.sf.jsqlparser.statement.select.SelectItem;


public class extractSelectItem extends baseClass {

    baseClass res = null;
    List<SelectItem> si = null;
    tableList tl = null;
    tableDetail td = null;


    public extractSelectItem(baseClass res, tableList tl, PlainSelect ps) throws Exception {

        this.si = ps.getSelectItems();
        this.res = res;
        this.tl = tl;
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

        tableDetail oldtd = res.get();
        detold(oldtd);
        return solveExpItem(oldtd,td);
    }

    private void detold(tableDetail oldtd) {
        if(td == null) {
            td = new tableDetail();
            updateCD(oldtd,td);
        }
    }


    private tableDetail solveExpItem(tableDetail oldtd, tableDetail newtd) throws Exception {
        if(si.get(0) instanceof AllColumns) {

            newtd.rowResult = oldtd.rowResult;
        }
        else {

            List<Expression> expl = new ArrayList<>();
            for (SelectItem temp : si) {

                if(temp instanceof SelectExpressionItem) {
                    Expression e =((SelectExpressionItem) temp).getExpression();
                    expl.add(e);
                }
                else if (temp instanceof AllTableColumns) {
                    AllTableColumns atc = (AllTableColumns) temp;
                    tableInformation ti = tl.tableInfo.get(newtd.tableAlias.get(atc.getTable().getName().toUpperCase()));
                    for(Column col : ti.col) {
                        Expression exp = (Expression) new Column(atc.getTable(), col.getColumnName());
                        expl.add(exp);
                    }
                }
            }
            newtd.rowResult = new ArrayList<>();
            for(rowClass rc : oldtd.rowResult) {
                PrimitiveValue[] pvarr = new PrimitiveValue[expl.size()];
                for(int i = 0; i < expl.size(); i++) {

                    Eval eval = new evaluator(oldtd, tl, oldtd.rowResult,null);
                    pvarr[i] = eval.eval(expl.get(i));
                }

                newtd.rowResult.add(new rowClass(pvarr));
            }

        }

        return newtd;

    }






    private void updateCD(tableDetail oldtd, tableDetail newtd) {
        newtd.table.addAll(oldtd.table);
        newtd.tableAlias.putAll(oldtd.tableAlias);
        newtd.colCD.putAll(oldtd.colCD);

        if (si.get(0) instanceof AllColumns)
        {
            newtd.colAliasTable.putAll(oldtd.colAliasTable);
        }
        else {
            int pos = 0;
            for (SelectItem s : si) {
                if (s instanceof AllTableColumns) {
                    for(Entry<Column, Integer> e : oldtd.colAliasTable.entrySet())
                        newtd.colAliasTable.put(e.getKey(), pos);
                }
                else if (s instanceof SelectExpressionItem) {
                    SelectExpressionItem sei = (SelectExpressionItem) s;
                    Expression e = sei.getExpression();
                    if (!(e instanceof Function)) {
                        String cname = e.toString();
                        if (e instanceof Column) {
                            Column c = (Column) e;
                            cname = c.getColumnName();
                        }
                        String colAlias = null;
                        if(sei.getAlias() != null)
                            colAlias = sei.getAlias();
                        else
                            colAlias= cname;
                        Column col = new Column(null, colAlias);
                        newtd.colAliasTable.put(col, pos);
                    }
                }
                pos++;
            }
        }
    }

}
