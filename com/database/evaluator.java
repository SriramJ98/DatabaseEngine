package com.database;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import com.database.selection.extractSubSelect;
import com.database.table.tableDetail;
import org.apache.commons.collections4.CollectionUtils;
import net.sf.jsqlparser.eval.Eval;
import net.sf.jsqlparser.expression.BooleanValue;
import net.sf.jsqlparser.expression.DateValue;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.Function;
import net.sf.jsqlparser.expression.PrimitiveValue;
import net.sf.jsqlparser.expression.operators.relational.Between;
import net.sf.jsqlparser.expression.operators.relational.ExistsExpression;
import net.sf.jsqlparser.expression.operators.relational.ExpressionList;
import net.sf.jsqlparser.expression.operators.relational.InExpression;
import net.sf.jsqlparser.schema.Column;
import net.sf.jsqlparser.schema.Table;
import net.sf.jsqlparser.statement.create.table.ColumnDefinition;
import net.sf.jsqlparser.statement.select.SubSelect;

public class evaluator extends Eval {

    public tableDetail tdetail;
    public tableList tlist;
    public Map<Column, PrimitiveValue> colResult;
    public List<rowClass> rowResult;
    public List<PrimitiveValue> inRowResult;

    public evaluator(tableDetail td, tableList tl, List<rowClass> rowResult ,Map<Column, PrimitiveValue> colResult) {
        this.tdetail = td;
        this.colResult = colResult;
        this.tlist = tl;
        this.rowResult = rowResult;
        this.inRowResult = new ArrayList<>();
    }

    @Override
    public PrimitiveValue eval(Function f) throws SQLException {
        return getPrimitiveValue(f);
    }

    private PrimitiveValue getPrimitiveValue(Function f) {
        try {
            PrimitiveValue pv = solveFunction.mathFunction(tlist, tdetail, f,rowResult);
            return pv;
        } catch (Exception e1) {
            e1.printStackTrace();
            return null;
        }
    }

    @Override
    public PrimitiveValue eval(Column c) throws SQLException {
        Table t = c.getTable();
        if(t.getName() == null || t == null) {
            for (Column col : tdetail.colAliasTable.keySet()) {
                if(col.getColumnName().equals(c.getColumnName()))
                    t = col.getTable();
            }
            c.setTable(t);
        }


        int pos = tdetail.colAliasTable.get(c);

        if(tdetail.colAliasTable.get(c) == null) {
            return colResult.get(c);
        }
        return rowResult.get(0).row[pos];
    }

    @Override
    public PrimitiveValue eval(Between b) throws SQLException {
        if(b.getLeftExpression() instanceof Column) {
            Column c = (Column) b.getLeftExpression();
            Table t = c.getTable();
            if(t.getName() == null || t == null) {
                for (Column col : tdetail.colAliasTable.keySet()) {
                    if(col.getColumnName().equals(c.getColumnName()))
                        t = col.getTable();
                }
                c.setTable(t);
            }
            String tableName = tdetail.tableAlias.get(t.getName().toUpperCase());
            ColumnDefinition cd = tlist.tableInfo.get(tableName).colCD.get(c.getColumnName());
            String str = cd.getColDataType().getDataType().toUpperCase();
            DataType dt = DataType.valueOf(str);
            exp(b, dt);

        }
        return super.eval(b);
    }

    private void exp(Between b, DataType dt) {
        if(dt.equals(DataType.DATE)) {
            String s1;
            String s2;
            s1 = b.getBetweenExpressionEnd().toString().replace("'", "");
            s2 = b.getBetweenExpressionStart().toString().replace("'", "");
            b.setBetweenExpressionEnd(new DateValue(s1));
            b.setBetweenExpressionStart(new DateValue(s2));
        }
    }

    @Override
    public PrimitiveValue eval(InExpression in) throws SQLException {

        if(in.getItemsList() instanceof SubSelect) {
            if(CollectionUtils.isEmpty(inRowResult)) {
                SubSelect ss = (SubSelect) in.getItemsList();
                baseClass res = null;
                res = getBaseClass(ss, res);

                try {
                    while(res.isNext()) {
                        for (rowClass pv : res.get().rowResult) {
                            inRowResult.add(pv.row[0]);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        else {
            List<Expression> exp = ((ExpressionList) in.getItemsList()).getExpressions();
            for(Expression e : exp) {
                Eval evalIn = new evaluator(tdetail, tlist,rowResult,null);
                inRowResult.add(evalIn.eval(e));
            }
        }
        Eval leftEval = new evaluator(tdetail, tlist,rowResult,null);
        PrimitiveValue leftPv = leftEval.eval(in.getLeftExpression());
        if(inRowResult.contains(leftPv))
            return BooleanValue.TRUE;
        else
            return BooleanValue.FALSE;
    }

    private baseClass getBaseClass(SubSelect ss, baseClass res) {
        res = getaClass(ss, res);
        return res;
    }

    private baseClass getaClass(SubSelect ss, baseClass res) {
        try {
            res = new extractSubSelect(ss, tlist);
        } catch (Exception e) {

            e.printStackTrace();
        }
        return res;
    }

    @Override
    public PrimitiveValue eval(ExistsExpression exist) throws SQLException {
        Eval eval = new evaluator(tdetail, tlist,rowResult,colResult);
        PrimitiveValue pv = null;
        if(exist.getRightExpression() instanceof SubSelect)
        {
            pv = getPrimitiveValue(exist, pv);
            return pv;
        }
        else
            return eval.eval(exist.getRightExpression());
    }

    private PrimitiveValue getPrimitiveValue(ExistsExpression exist, PrimitiveValue pv) {
        try {
            pv = subSelectEval((SubSelect) exist.getRightExpression());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return pv;
    }

    public PrimitiveValue subSelectEval(SubSelect ss) throws Exception {

        Map<Column, PrimitiveValue> colResult = new LinkedHashMap<>();
        cres(colResult);
        baseClass res = new extractSubSelect(tlist,ss,colResult);
        if(res.isNext())
            return BooleanValue.TRUE;
        else
            return BooleanValue.FALSE;
    }

    private void cres(Map<Column, PrimitiveValue> colResult) {
        for(Entry<Column, Integer> e : tdetail.colAliasTable.entrySet()) {
            colResult.put(e.getKey(), rowResult.get(0).row[e.getValue()]);
        }
    }

}

