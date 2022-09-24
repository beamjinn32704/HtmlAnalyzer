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
public class HtmlTagData implements Comparable<HtmlTagData> {
    
    private final Attrib[] tribs;
    private String parentInside;
    private String inside;
    private final boolean start;
    private final boolean autoClose;
    private int rank;

    public HtmlTagData(Attrib[] tribs, String inside, String parentInside, boolean start, boolean autoClose, int rank) {
        this.tribs = tribs;
        this.parentInside = parentInside;
        this.start = start;
        this.autoClose = autoClose;
        this.rank = rank;
        this.inside = inside;
//        Arrays.sort(tribs);
    }

    public String getParentInsidee() {
        return parentInside;
    }

    public void setParentInsidee(String parentInside) {
        this.parentInside = parentInside;
    }

    public void addInside(String i) {
        inside += i;
    }

    public String getInside() {
        return inside;
    }

    public Attrib[] getTribs() {
        return tribs;
    }

    public boolean isAutoClose() {
        return autoClose;
    }

    public boolean isStart() {
        return start;
    }

    public void setRank(int rank) {
        this.rank = rank;
    }

    public int getRank() {
        return rank;
    }

    @Override
    public int compareTo(HtmlTagData o) {
        int tribLenComp = tribs.length - o.tribs.length;
        return tribLenComp;
    }
}
