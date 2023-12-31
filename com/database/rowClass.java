package com.database;

import java.util.Arrays;

import net.sf.jsqlparser.expression.PrimitiveValue;

public class rowClass  {
    PrimitiveValue[] row = null;

    public rowClass(PrimitiveValue[] row) {
        this.row = row;
    }

    @Override
    public int hashCode() {
        int p = 37;
        int r = 1;
        r = p * r + Arrays.hashCode(row);
        return r;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null)
            return false;
        if (getClass() != o.getClass())
            return false;
        rowClass temp = (rowClass) o;
        if (!Arrays.equals(row,temp.row))
            return false;
        else
            return true;
    }
}
