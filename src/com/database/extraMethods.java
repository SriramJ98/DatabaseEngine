package com.database;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;
import net.sf.jsqlparser.expression.PrimitiveValue;

public class extraMethods {

    public static List<String> extractQuery(String path) {
        List<String> query = new ArrayList<String>();
        String str = new String();
        StringBuffer str_sb = new StringBuffer();

        try {
            FileReader FR = new FileReader(new File(path));
            BufferedReader br = new BufferedReader(FR);

            while((str = br.readLine()) != null) {
                str_sb.append(str + " ");
            }
            br.close();
            String[] inst = str_sb.toString().replaceAll("( )+", " ").split("; ");

            for(int i = 0; i<inst.length; i++) {
                query.add(inst[i]);
            }
        }

        catch(Exception e) {
            System.out.println("*** Error : "+e.toString());
            System.out.println(str_sb.toString());
        }
        return query;
    }


    public static String[] combine(String[] str1,String[] str2) {
        int len = str1.length + str2.length;
        String[] str3 = new String[len];
        System.arraycopy(str1, 0, str3, 0, str1.length);
        System.arraycopy(str2, 0, str3, str1.length,str2.length);
        return str3;
    }

    public static rowClass joinArray(PrimitiveValue[] pv1, PrimitiveValue[] pv2) {
        int len = pv1.length + pv2.length;
        PrimitiveValue[] pv3 = new PrimitiveValue[len];
        System.arraycopy(pv1, 0, pv3, 0, pv1.length);
        System.arraycopy(pv2, 0, pv3, pv1.length, pv2.length);
        return new rowClass(pv3);
    }
}
