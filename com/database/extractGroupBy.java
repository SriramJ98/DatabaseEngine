package com.database;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import com.database.table.tableDetail;
import net.sf.jsqlparser.eval.Eval;
import net.sf.jsqlparser.expression.DoubleValue;
import net.sf.jsqlparser.expression.PrimitiveValue;
import net.sf.jsqlparser.schema.Column;
import net.sf.jsqlparser.statement.select.SelectExpressionItem;
import net.sf.jsqlparser.statement.select.SelectItem;

public class extractGroupBy extends baseClass {
    Iterator<rowClass> it = null;
    Collection<rowClass> rowResult = null;
    tableDetail td = null;

    public extractGroupBy (baseClass res, tableList tl, List<Column> gcol, List<SelectItem> si) throws Exception {

        td = new tableDetail();
        copySchema(td,si);
        Map<rowClass, rowClass> groupBy = solveGroupBy(tl,si,res,gcol);
        rowResult = groupBy.values();
        it = rowResult.iterator();
    }

    private Map<rowClass, rowClass> solveGroupBy(tableList tl, List<SelectItem> si,baseClass res, List<Column> gcol) throws Exception {

        Map<rowClass, rowClass> temp1 = new LinkedHashMap();
        Map<rowClass, Long> temp2 = new LinkedHashMap();
        while(res.isNext()) {
            tableDetail grouptd = res.get();
            for(rowClass rc: grouptd.rowResult) {
                int len = gcol.size();
                PrimitiveValue pvArr[] = new PrimitiveValue[len];
                primval(tl, gcol, grouptd, rc, len, pvArr);
                rowClass rc2 = new rowClass(pvArr);
                if(!temp1.containsKey(rc2)) {
                    PrimitiveValue pv[] = new PrimitiveValue[si.size()];
                    temp1.put(rc2, new rowClass(pv));
                    temp2.put(rc2, (long) 0);
                }
                temp2.put(rc2, temp2.get(rc2) + 1);
                for(int i = 0; i < si.size(); i++) {
                    net.sf.jsqlparser.expression.Expression e = ((SelectExpressionItem) si.get(i)).getExpression();
                    if(e instanceof Column) {
                        temp1.get(rc2).row[i] = rc2.row[gcol.indexOf(e)];
                    }
                    else {
                        Eval eval = new evaluator(grouptd,tl,Arrays.asList(rc),null);
                        PrimitiveValue PV = eval.eval(e);

                        if(temp1.get(rc2).row[i] != null) {
                            if(e.toString().toLowerCase().contains("count")) {
                                PV = new DoubleValue(temp2.get(rc2));
                            }
                            else if(e.toString().toLowerCase().contains("max")) {
                                double max = PV.toDouble();
                                if(max < temp1.get(rc2).row[i].toDouble()) {
                                    PV = temp1.get(rc2).row[i];
                                }
                            }
                            else if(e.toString().toLowerCase().contains("min")) {
                                double min = PV.toDouble();
                                if(min > temp1.get(rc2).row[i].toDouble()) {
                                    PV = temp1.get(rc2).row[i];
                                }
                            }
                            else if(e.toString().toLowerCase().contains("sum")) {
                                double sum;
                                sum = temp1.get(rc2).row[i].toDouble() + PV.toDouble();
                                PV = new DoubleValue(sum);
                            }
                            else if(e.toString().toLowerCase().contains("avg")) {
                                double avg;
                                avg = temp1.get(rc2).row[i].toDouble() * (temp2.get(rc2) - 1);
                                PV = new DoubleValue((PV.toDouble() + avg)/temp2.get(rc2));
                            }
                        }
                        temp1.get(rc2).row[i] = PV;
                    }
                }

            }

        }
        return temp1;
    }

    private void primval(tableList tl, List<Column> gcol, tableDetail grouptd, rowClass rc, int len, PrimitiveValue[] pvArr) throws SQLException {
        for (int i = 0; i < len; i++) {
            Column c = gcol.get(i);
            List<rowClass> temp = new ArrayList<>();
            temp.add(rc);
            Eval eval = new evaluator(grouptd, tl,temp,null);
            pvArr[i] = eval.eval(c);
        }
    }

    private void copySchema(tableDetail td, List<SelectItem> si) throws Exception {

        int position = 0;
        for(SelectItem s: si) {
            net.sf.jsqlparser.expression.Expression e = ((SelectExpressionItem) s).getExpression();
            String cname = e.toString();
            if(e instanceof Column) {
                cname = ((Column) e).getColumnName();
            }
            String calias;
            if(((SelectExpressionItem) s).getAlias() != null)
                calias = ((SelectExpressionItem) s).getAlias();
            else
                calias = cname;
            td.colAliasTable.put(new Column(null,calias),position);
        }
        position++;
    }

    @Override
    public boolean isNext() throws Exception {
        return it.hasNext();
    }

    @Override
    public void clear() {
        it = rowResult.iterator();

    }

    @Override
    public tableDetail get() throws Exception {
        td.rowResult = new ArrayList<>();
        td.rowResult.add(it.next());
        return td;
    }
}
