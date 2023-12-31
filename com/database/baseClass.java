package com.database;

import com.database.table.tableDetail;

public abstract class baseClass {
    public abstract boolean isNext() throws Exception;
    public abstract void clear();
    public abstract tableDetail get() throws Exception;
}
