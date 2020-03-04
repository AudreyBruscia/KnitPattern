package xyz.audbru.knitpattern;

import java.io.Serializable;

/**
 * Created by User on 4/19/2016.
 */
public class Row implements Serializable{
    private int m_rowNum;
    private String m_patternText;

    public Row() {
        this.m_rowNum = 0;
        this.m_patternText = "";
    }

    // Should always use this Constructor
    public Row(int rowNum, String patternText) {
        this.m_rowNum = rowNum;
        this.m_patternText = patternText;
    }

    public Row(int rowNum) {
        this.m_rowNum = rowNum;
        this.m_patternText = "";
    }

    public void setRowNum(int rowNum) { this.m_rowNum = rowNum; }

    public int getRowNum() { return this.m_rowNum; }

    public void setPatternText(String patternText) { this.m_patternText = patternText; }

    public String getPatternText() { return this.m_patternText; }

}
