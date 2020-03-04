package xyz.audbru.knitpattern;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by User on 4/19/2016.
 */
public class Pattern implements Serializable {
    protected int m_projectNum;
    protected String m_projectName;
    protected ArrayList<Row> m_arrRows;

    protected Pattern(){
        m_arrRows = new ArrayList<Row>();
    }

    public void setProjectName(String projectName) { this.m_projectName = projectName; }

    public String getProjectName() { return this.m_projectName; }

    public void setProjectNum(int projectNum) { this.m_projectNum = projectNum; }

    public int getProjectNum() { return this.m_projectNum; }

    public void setArrRows(ArrayList<Row> arrRows) { this.m_arrRows = arrRows; }

    public ArrayList<Row> getArrRows() { return m_arrRows; }

    public String toString() {
        return m_projectName;
    }
}
