package com.database;


import java.sql.SQLException;
import java.util.List;

import com.database.table.tableDetail;
import net.sf.jsqlparser.eval.Eval;
import net.sf.jsqlparser.expression.DateValue;
import net.sf.jsqlparser.expression.DoubleValue;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.Function;
import net.sf.jsqlparser.expression.LongValue;
import net.sf.jsqlparser.expression.PrimitiveValue;
import net.sf.jsqlparser.expression.operators.relational.ExpressionList;

public class solveFunction {

    public static PrimitiveValue mathFunction(tableList tl, tableDetail td, Function f, List<rowClass> rowResult) throws Exception {

        ExpressionList expl = f.getParameters();
        String fname = f.getName().toLowerCase();

        switch(fname) {

            case "date" :

                PrimitiveValue PV = calcExpression(tl,td,rowResult,f.getParameters().getExpressions().get(0));
                return new DateValue(PV.toString().replace("'", ""));
            case "date_part" :
            case "datepart" :
                PrimitiveValue PV2 = calcExpression(tl,td,rowResult,f.getParameters().getExpressions().get(1));
                long temp = 0;
                String s = f.getParameters().getExpressions().get(0).toString().toLowerCase().replace("'", "");
                switch(s) {
                    case "year":
                    case "yyyy":
                    case "yy":
                        temp = new DateValue(PV2.toString().replace("'", "")).getYear();
                        temp = temp + 1900;
                        break;
                    case "month":
                    case "mm" :
                    case "m" :
                        temp = new DateValue(PV2.toString().replace("'", "")).getMonth();
                        break;
                    case "day" :
                    case "d" :
                    case "dd" :
                        temp = new DateValue(PV2.toString().replace("'", "")).getDate();
                        break;
                }
                return new LongValue(temp);
            case "count" :
                return new DoubleValue(1);
            default:
                return calcExpression(tl,td,rowResult,f.getParameters().getExpressions().get(0));
        }
    }



    private static PrimitiveValue calcExpression(tableList tl, tableDetail td, List<rowClass> rowResult,
                                                 Expression e) throws Exception {
        return getPrimitiveValue(tl, td, rowResult, e);
    }

    private static PrimitiveValue getPrimitiveValue(tableList tl, tableDetail td, List<rowClass> rowResult, Expression e) throws SQLException {
        if(rowResult.get(0) != null) {
            Eval eval = new evaluator(td, tl, rowResult,null);
            PrimitiveValue val = eval.eval(e);
            return val;
        }
        else {
            Eval eval = new evaluator(td, tl,null,null);
            PrimitiveValue val = eval.eval(e);
            return val;
        }
    }

}
