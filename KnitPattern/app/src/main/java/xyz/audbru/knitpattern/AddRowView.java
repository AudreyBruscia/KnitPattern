package xyz.audbru.knitpattern;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * Created by User on 4/19/2016.
 */
public class AddRowView extends LinearLayout {
    // Displays the Row Num
    private TextView m_vwRowText;

    // Displays the pattern text
    private EditText m_vwPatternText;

    // Data version of the view
    private Row m_row;

    /**
     * Basic Constructor that takes only an application Context.
     *
     * @param context
     *            The application Context in which this view is being added.
     *
     * @param row
     * 			  The Joke this view is responsible for displaying.
     */
    public AddRowView(Context context, Row row) {
        super(context);

        m_row = row;
        LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.add_pattern_row, this, true);
        this.m_vwRowText = (TextView) findViewById(R.id.rowNumber);
        this.m_vwPatternText = (EditText) findViewById(R.id.enterPattern);

        m_vwPatternText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                m_row.setPatternText(m_vwPatternText.getText().toString());
            }
        });

        this.setRow(row);

        requestLayout();
    }

    /**
     * Mutator method for changing the Joke object this View displays. This View
     * will be updated to display the correct contents of the new Joke.
     *
     * @param row
     *            The Joke object which this View will display.
     */
    public void setRow(Row row) {
        this.m_row = row;
        this.m_vwRowText.setText("Row " + row.getRowNum() + ":");
        if (row.getPatternText() == null || row.getPatternText() == "") {
            this.m_vwPatternText.setText("");
            this.m_vwPatternText.setHint(R.string.edit_text_hint);
        }
        else {
            this.m_vwPatternText.setText(row.getPatternText());
        }

    }
}

