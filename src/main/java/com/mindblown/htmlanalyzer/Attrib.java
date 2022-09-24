package com.mindblown.htmlanalyzer;


import com.mindblown.util.ArrayListUtil;
import com.mindblown.util.ArrayUtil;
import com.mindblown.util.Util;
import java.util.ArrayList;
import java.util.Collections;

/*
* To change this license header, choose License Headers in Project Properties.
* To change this template file, choose Tools | Templates
* and open the template in the editor.
*/

/**
 *
 * @author beamj
 */
public class Attrib implements Comparable<Object>{
    
    private String type = null;
    private String val = null;
    
    public Attrib(String t, String v) {
        type = t;
        val = v;
    }
    
    public String getType() {
        return type;
    }
    
    public String getVal() {
        return val;
    }
    
    public static Attrib[] getAttribs(String t, int start, int end){
        ArrayList<Attrib> tribs = new ArrayList<>();
        String tag = t.subSequence(start, end).toString();
        AttribScanner scan = new AttribScanner(tag);
        boolean go = true;
        while(go){
            Attrib trib = scan.nextTrib();
            if(trib != null){
                tribs.add(trib);
            } else {
                go = false;
            }
        }
//        Collections.sort(tribs);
        Object[] o = tribs.toArray();
        Attrib[] attribs = new Attrib[o.length];
        for(int i = 0; i < o.length; i++){
            attribs[i] = (Attrib)o[i];
        }
        return attribs;
    }
    
    private static class AttribScanner {
        private String line;
        
        public AttribScanner(String line) {
            this.line = line;
        }
        
        public Attrib nextTrib(){
            line = Util.strip(line);
            int equals = line.indexOf("=");
            if(equals < 0){
                return null;
            }
            String tribName = backwardsTribName(equals);
            String tribVal = nextTribVal(equals);
            if(tribName == null || tribVal == null){
                return null;
            }
            line = Util.strip(line);
            return new Attrib(tribName, tribVal);
        }
        
        private String nextTribVal(int equalsInd){
            boolean inQuotes = false;
            int inQuotesMode = 0;
            int valStart = -1;
            for(int i = equalsInd+1; i < line.length(); i++){
                char c = line.charAt(i);
                if(c == '\"' || c == '\''){
                    if(c == '\''){
                        inQuotesMode = 1;
                    }
                    inQuotes = true;
                    valStart = i+1;
                    i = line.length();
                } else if(Character.isAlphabetic(c) || Character.isDigit(c)){
                    valStart = i;
                    i = line.length();
                } else if(c == '>'){
                    return null;
                }
            }
            String val;
            int end;
            if(inQuotes){
                if(inQuotesMode == 0){
                    end = line.indexOf("\"", valStart);
                } else {
                    end = line.indexOf("'", valStart);
                }
                if(end == -1){
                    int a = line.indexOf(">", valStart);
                    int last = line.length();
                    if(a == -1){
                        end = last;
                    } else {
                        end = Math.min(a, last);
                    }
                }
                val = line.substring(valStart, end);
            } else {
                int space = line.indexOf(" ", valStart);
                int arr = line.indexOf(">", valStart);
                if(space != -1 && arr != -1){
                    end = Math.min(space, arr);
                } else if(space == -1 && arr == -1){
                    end = line.length();
                } else if(space == -1){
                    end = arr;
                } else {
                    end = space;
                }
                val = line.substring(valStart, end);
            }
            if(end != line.length()){
                line = line.substring(end + 1);
            } else {
                line = "";
            }
            line = Util.strip(line);
            return val;
        }
        
        private String backwardsTribName(int equalsInd){
            int nameEnd = -1;
            int nameStart = -1;
            for(int i = equalsInd - 1; i >= 0; i--){
                char c = line.charAt(i);
                if(nameEnd == -1){
                    if(Character.isAlphabetic(c) || Character.isDigit(c)){
                        nameEnd = i;
                    } else {
                        if(!Character.isWhitespace(c)){
                            return null;
                        }
                    }
                } else {
                    if(!Character.isAlphabetic(c) && !Character.isDigit(c)){
                        nameStart = i+1;
                        i = -1;
                    }
                }
            }
            if(nameStart == -1){
                nameStart = 0;
            }
            String tribName = line.substring(nameStart, nameEnd+1);
            line = Util.strip(line);
            return tribName;
        }
    }
    
    @Override
    public int compareTo(Object o) {
        if(o instanceof Attrib){
            return type.compareTo(((Attrib) o).type);
        } else if(o instanceof String){
            return type.compareTo((String) o);
        } else {
            return hashCode() - o.hashCode();
        }
    }

    @Override
    public String toString() {
        return type + "=\"" + val + "\"";
    }
}