package xyz.audbru.knitpattern;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.List;

/**
 * Created by User on 4/19/2016.
 */
public class RowListAdapter extends BaseAdapter {
    /** The application Context in which this RowListAdapter is being used. */
    private Context m_context;

    /** The data set to which this JokeListAdapter is bound. */
    private List<Row> m_rowList;  //from my database

    public RowListAdapter(Context context, List<Row> rowList) {
        this.m_context = context;
        this.m_rowList = rowList;
    }

    // This sets the ArrayList to contain all the rows associated with a project, ordered by row number
    public void setRowListArray(List<Row> newArrayList) {
        m_rowList = newArrayList;
        this.notifyDataSetChanged();
    }

    public List<Row> getRowListArray() { return m_rowList; }

    @Override
    public int getCount() {
        if (m_rowList == null) {
            return 0;
        }
        return m_rowList.size();
    }

    @Override
    public Object getItem(int position) {
        return m_rowList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Row row;

        if (convertView == null) {
            AddRowView addRowView = new AddRowView(m_context, m_rowList.get(position));
            return addRowView;
        }

        row = m_rowList.get(position);
        ((AddRowView) convertView).setRow(row);

        return convertView;
    }
}
