package com.database;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import com.database.table.tableDetail;
import org.apache.commons.collections4.CollectionUtils;
import net.sf.jsqlparser.eval.Eval;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.PrimitiveValue;
import net.sf.jsqlparser.schema.Column;
import net.sf.jsqlparser.schema.PrimitiveType;
import net.sf.jsqlparser.statement.select.PlainSelect;

public class extractWhere extends baseClass{
    baseClass res = null;
    tableList tl = null;
    tableDetail td = null;
    Expression e = null;
    Map<Column, PrimitiveValue> colResult = null;

    public extractWhere(baseClass res, tableList tl, PlainSelect ps) {
        this.e = ps.getWhere();
        this.res= res;
        this.tl=tl;
    }

    public extractWhere(baseClass res, tableList tl, PlainSelect ps, Map<Column, PrimitiveValue> colResult) {
        this.e = ps.getWhere();
        this.res = res;
        this.tl = tl;
        this.colResult = colResult;
    }

    @Override
    public boolean isNext() throws Exception {

        if(res == null) {
            return false;
        }

        List<rowClass> newRes = new ArrayList<>();
        while(res.isNext() && CollectionUtils.isEmpty(newRes)) {
            td = res.get();
            for(rowClass pv : td.rowResult) {
                List<rowClass> pvlist = new ArrayList<>();
                pvlist.add(pv);
                Eval eval = new evaluator(td,tl,pvlist,colResult);
                PrimitiveValue pvres = eval.eval(e);
                if(pvres.toBool()) {
                    if(pvres.getType().equals(PrimitiveType.BOOL)) {
                        newRes.add(pv);
                    }
                }
            }
        }
        td.rowResult = newRes;
        if(CollectionUtils.isNotEmpty(newRes))
            return true;
        else
            return false;
    }

    @Override
    public void clear() {
        res.clear();
    }

    @Override
    public tableDetail get() throws Exception {
        return td;
    }

}
