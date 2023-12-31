package com.database;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import com.database.table.tableDetail;
import com.database.table.tableInformation;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.LineIterator;
import net.sf.jsqlparser.expression.DateValue;
import net.sf.jsqlparser.expression.DoubleValue;
import net.sf.jsqlparser.expression.LongValue;
import net.sf.jsqlparser.expression.PrimitiveValue;
import net.sf.jsqlparser.expression.StringValue;
import net.sf.jsqlparser.schema.Column;
import net.sf.jsqlparser.schema.Table;
import net.sf.jsqlparser.statement.create.table.ColumnDefinition;
import net.sf.jsqlparser.statement.select.FromItem;

public class extractTable extends baseClass {

    File f = null;
    tableDetail td = null;
    LineIterator lineIterator = null;
    tableInformation temp = null;
    List<Column> columnList = null;

    public extractTable(FromItem fm, tableList tl) throws Exception {
        Table t = (Table) fm;
        String path = tl.table_path + t.getName() + ".dat";
        f = new File(path);
        lineIterator = FileUtils.lineIterator(f);

        this.temp = tl.tableInfo.get(t.getName().toUpperCase());
        td = new tableDetail();
        if(t.getAlias() == null)
            td.tableAlias.put(t.getName().toUpperCase(), t.getName().toUpperCase());
        else
            td.tableAlias.put(t.getAlias().toUpperCase(), t.getName().toUpperCase());
        td.table.add(t);
        this.columnList = temp.col;
        td.colCD = this.temp.colCD;
        int pos = 0;
        for(Column c: this.temp.col) {

            if(t.getAlias() == null)
                td.colAliasTable.put((new Column(new Table(t.getName()),c.getColumnName())),pos);
            else
                td.colAliasTable.put((new Column(new Table(t.getAlias()),c.getColumnName())),pos);
            pos++;
        }
    }


    @Override
    public boolean isNext() {
        if(lineIterator.hasNext()) {
            return true;
        }
        else
            return false;


    }

    @Override
    public void clear()  {
        try {
            lineIterator = FileUtils.lineIterator(f);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Override
    public tableDetail get() {
        List<rowClass> rc = new ArrayList<>();
        String s = lineIterator.nextLine();
        String[] str = s.split("\\|");
        int len = str.length;
        PrimitiveValue[] pv = new PrimitiveValue[len];
        for(int i = 0; i < len; i++) {
            ColumnDefinition CD = temp.colCD.get(columnList.get(i).getColumnName());
            String S = CD.getColDataType().getDataType().toUpperCase();
            DataType dt = DataType.valueOf(S);
            pv[i] = switchCase(str,i,dt);
        }
        rc.add(new rowClass(pv));
        td.rowResult = rc;
        return td;
    }


    public PrimitiveValue switchCase(String[] str,int i,DataType dt) {
        switch(dt) {
            case INT:
                return new LongValue(str[i]);
            case DOUBLE:
            case DECIMAL:
                return new DoubleValue(str[i]);
            case DATE:
                return new DateValue(str[i]);
            default:
                return new StringValue(str[i]);
        }
    }

}
