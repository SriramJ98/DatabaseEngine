package com.database.selection;

import java.util.Map;

import com.database.*;
import com.database.table.tableDetail;
import com.database.utils.aggregate;
import com.database.utils.extractDistinct;
import com.database.utils.extractLimit;
import com.database.utils.extractOrderBy;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.Function;
import net.sf.jsqlparser.expression.PrimitiveValue;
import net.sf.jsqlparser.schema.Column;
import net.sf.jsqlparser.statement.select.AllColumns;
import net.sf.jsqlparser.statement.select.AllTableColumns;
import net.sf.jsqlparser.statement.select.FromItem;
import net.sf.jsqlparser.statement.select.Join;
import net.sf.jsqlparser.statement.select.PlainSelect;
import net.sf.jsqlparser.statement.select.SelectBody;
import net.sf.jsqlparser.statement.select.SelectExpressionItem;
import net.sf.jsqlparser.statement.select.SelectItem;

public class extractPlainSelect extends baseClass {

    baseClass res;



    public extractPlainSelect(tableList tl, SelectBody sb, Map<Column, PrimitiveValue> colResult) throws Exception {
        PlainSelect ps = (PlainSelect) sb;
        FromItem fm = ps.getFromItem();
        res = new extractFromItem(fm,tl);

        if(ps.getJoins() != null) {
            for(Join j : ps.getJoins()) {
                baseClass temp = new extractFromItem(j.getRightItem(),tl);
                res = new extractJoin(res,temp);
            }
        }
        if(ps.getWhere() != null) {
            res = new extractWhere(res,tl,ps,colResult);
        }

        if (ps.getGroupByColumnReferences() != null) {
            res = new extractGroupBy(res,tl,ps.getGroupByColumnReferences(),ps.getSelectItems());
        }
        else {
            boolean flag = false;
            if(!(ps.getSelectItems().get(0) instanceof AllTableColumns) && !(ps.getSelectItems().get(0) instanceof AllColumns)) {
                first : for(SelectItem s : ps.getSelectItems()) {
                    SelectExpressionItem sei = ((SelectExpressionItem) s);
                    Expression e = sei.getExpression();
                    if(e instanceof Function) {
                        Function f = (Function) e;
                        String str = f.getName().toUpperCase();
                        switch(str) {
                            case "MAX" :
                            case "MIN" :
                            case "COUNT" :
                            case "AVG" :
                            case "SUM" :
                                flag = true;
                                break first;
                        }
                    }
                }
                if(flag == true) {
                    res = new aggregate(tl,res,ps.getSelectItems());
                }
                else {
                    res = new extractSelectItem(res,tl,ps);
                }


            }
        }


        if(ps.getDistinct() != null) {
            res = new extractDistinct(res);
        }


        if(ps.getOrderByElements() != null) {
            res = new extractOrderBy(tl,ps,res);
        }
        if(ps.getLimit() != null) {
            res = new extractLimit(ps,res);
        }


    }

    @Override
    public boolean isNext() throws Exception {
        if(res == null || (!res.isNext())) {
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
