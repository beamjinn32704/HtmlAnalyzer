package com.mindblown.htmlanalyzer;


import java.util.Arrays;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author beamj
 */
public class HtmlTag implements Comparable<HtmlTag>{
    
    private final String tagName;
    private final HtmlTagData data;
    
    public HtmlTag(String name, HtmlTagData d) {
        tagName = name;
        data = d;
    }

    public String getTagName() {
        return tagName;
    }
    
    public static boolean isTag(String l, String front, String back, String attrib){
        String line = l.strip();
        return (line.startsWith(front) && line.endsWith(back) && line.contains(attrib + "=\""));
    }

    public Attrib getAttrib(String name) {
        int ind = Arrays.binarySearch(data.getTribs(), name);
        if(ind >= 0){
            return data.getTribs()[ind];
        }
        return null;
    }

    public HtmlTagData getData() {
        return data;
    }

    @Override
    public int compareTo(HtmlTag o) {
            int nameComp = tagName.compareTo(o.tagName);
            if(nameComp != 0){
                return nameComp;
            }
            return data.compareTo(o.data);
    }
    
    @Override
    public String toString() {
        String mess = "<";
        if(!data.isStart()){
            mess += "/";
        }
//        mess += tagName + " ";
        mess += tagName;
        Attrib[] tribs = data.getTribs();
        for(int i = 0; i < tribs.length; i++){
//            mess += tribs[i].toString();
            mess += " " + tribs[i].toString();
//            if(i != tribs.length - 1){
//                mess += " ";
//            }
        }
        //If the tag closes itself and isn't a closing tag itself,
        //add a slash before the '>' to signify that the tag is a self-closing tag
        if(data.isAutoClose() && data.isStart()){
            mess += "/";
        }
        mess += ">";
        return mess;
    }
}
