package com.database;

import java.io.Reader;
import java.io.StringReader;
import java.util.Arrays;
import java.util.List;
import com.database.selection.extractSelectBody;
import com.database.table.tableInformation;
import net.sf.jsqlparser.expression.PrimitiveValue;
import net.sf.jsqlparser.parser.CCJSqlParser;
import net.sf.jsqlparser.parser.ParseException;
import net.sf.jsqlparser.schema.Table;
import net.sf.jsqlparser.statement.Statement;
import net.sf.jsqlparser.statement.create.table.ColumnDefinition;
import net.sf.jsqlparser.statement.create.table.CreateTable;
import net.sf.jsqlparser.statement.select.Select;

public class Main {

    public static void main(String[] args) throws Exception {

        String sql_path = args[0];
        tableList tl = new tableList();
        tl.table_path = args[1];
        List<String> query = extraMethods.extractQuery(sql_path);
        for (String stat : query) {

            Reader reader = new StringReader(stat);
            CCJSqlParser parser = new CCJSqlParser(reader);
            try {
                Statement st = parser.Statement();
                if (st instanceof CreateTable) {
                    tableSchema(st, tl);
                } else if (st instanceof Select) {
                    Select s = (Select) st;
                    baseClass res = new extractSelectBody(tl, s.getSelectBody(), null);
                    while (res.isNext()) {
                        List<rowClass> pvlist = res.get().rowResult;
                        for (rowClass pvarr : pvlist) {
//                            for (PrimitiveValue pv : pvarr.row) {
                            for(int i = 0; i < pvarr.row.length; i++){
                                System.out.print(pvarr.row[i]);
                                if (i != (pvarr.row.length-1)){
//                                    System.out.print(i);
                                    System.out.print("|");
//                                    System.out.print(pvarr.row.length);
                                }

//                               if(Character.isDigit(pv)){
//                                    break;
//                                }
//                                if(){
//
//                                }
//                                String value = pv.toString();
//                                if(value == null){
//                                    System.out.print("**");
//                                }
//                                System.out.print(pvarr.row.length);
//                                System.out.print(value + "|");
//                                System.out.print(pv.toString().getClass().getName());
//                                System.out.print(Arrays.toString(pvarr.row));
                  //              System.out.println("*");
                            }
                            System.out.println();
                        }
                    }

                }
            }

            catch (ParseException e) {
                e.printStackTrace();
            }
        }

    }

    static void tableSchema(Statement statement, tableList tl) {
        CreateTable ct = (CreateTable) statement;
        String name = ct.getTable().getName();
        Table t = new Table(name);
        List<ColumnDefinition> cd = ct.getColumnDefinitions();
        tableInformation ti = new tableInformation(t, cd);
        tl.tableInfo.put(name.toUpperCase(), ti);
    }
}
