package xyz.audbru.knitpattern;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class DisplayRowActivity extends AppCompatActivity {
    protected Pattern m_pattern;
    protected ArrayList<Row> m_arrRowList;
    protected int m_rowsIndex = 0;
    protected int m_stitchCount = 0;
    protected int m_spareCount = 0;
    protected int m_rowInProject = 1;

    protected boolean lockEnabled = false;

    protected TextView m_vwRowDisplay;
    protected TextView m_vwPatternDisplay;
    protected EditText m_vwStitchCounter;
    protected EditText m_vwSpareCounter;
    protected TextView m_vwRowInProject;
    protected EditText m_vwRowInProjectEditText;
    protected ImageView m_vwBackButton;
    protected ImageView m_vwForwardButton;
    protected ImageView m_vwStitchPlus;
    protected ImageView m_vwStitchMinus;
    protected ImageView m_vwSparePlus;
    protected ImageView m_vwSpareMinus;

    protected static final String SAVED_PATTERN_INDEX = "Save Pattern Index ";
    protected static final String SAVED_PATTERN_ROW_PROJECT = "Save Pattern Row Project ";
    protected static final String SAVED_PATTERN_STITCH_COUNT = "Save Pattern Stitch Count ";
    protected static final String SAVED_PATTERN_SPARE_COUNT = "Save Pattern Spare Count ";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_row);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDefaultDisplayHomeAsUpEnabled(true);

        initLayout();
        initClickListeners();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.editmenu, menu);
//        MenuItem menuItem = (MenuItem) menu.findItem(R.id.menu_edit);    //Makes menu item colored the text color
//        menuItem.getIcon().mutate().setColorFilter(ContextCompat.getColor(this, R.color.colorRowText), PorterDuff.Mode.SRC_IN);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                super.onBackPressed();
                break;
            case R.id.menu_edit:
                goToEditPattern(m_pattern);
                break;
            case R.id.menu_lock_screen:
                lockEnabled = !lockEnabled;
                if (lockEnabled) {
                    getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
                    item.setTitle(R.string.unlock_screen_menuitem);
                    Toast.makeText(this, getResources().getString(R.string.sleep_disabled_message), Toast.LENGTH_SHORT).show();
                }
                else {
                    getWindow().clearFlags(android.view.WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
                    item.setTitle(R.string.lock_screen_menuitem);
                    Toast.makeText(this, getResources().getString(R.string.sleep_enabled_message), Toast.LENGTH_SHORT).show();
                }
                break;
            default:
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    protected void initLayout() {
        m_vwRowDisplay = (TextView) findViewById(R.id.rowDisplay);
        m_vwPatternDisplay = (TextView) findViewById(R.id.patternDisplay);
        m_vwStitchCounter = (EditText) findViewById(R.id.stitchCounter);
        m_vwSpareCounter = (EditText) findViewById(R.id.spareCounter);
        m_vwRowInProject = (TextView) findViewById(R.id.rowInProject);
        m_vwRowInProjectEditText = (EditText) findViewById(R.id.rowInProjectEditText);
        m_vwBackButton = (ImageView) findViewById(R.id.backButton);
        m_vwForwardButton = (ImageView) findViewById(R.id.forwardButton);
        m_vwStitchPlus = (ImageView) findViewById(R.id.stitchPlus);
        m_vwStitchMinus = (ImageView) findViewById(R.id.stitchMinus);
        m_vwSparePlus = (ImageView) findViewById(R.id.sparePlus);
        m_vwSpareMinus = (ImageView) findViewById(R.id.spareMinus);

        m_pattern = (Pattern) getIntent().getSerializableExtra(ProjectSelectionActivity.PATTERN_KEY);

        if (m_pattern == null) {
            finish();
        }
    }

    protected void getRowsFromDatabase() {
        PatternSQLHelper patternSQLHelper = new PatternSQLHelper(this);
        m_pattern = patternSQLHelper.getFullPattern(m_pattern);
        m_arrRowList = m_pattern.getArrRows();
    }

    protected void initClickListeners() {
        m_vwBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                backRow();
            }
        });

        m_vwForwardButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                forwardRow();
            }
        });

        m_vwBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                backRow();
            }
        });

        m_vwStitchPlus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (m_stitchCount < Integer.MAX_VALUE) {
                    m_stitchCount++;
                    setCounters();
                }
            }
        });

        m_vwStitchMinus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (m_stitchCount != 0) {
                    m_stitchCount--;
                    setCounters();
                }
            }
        });

        m_vwSparePlus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (m_spareCount < Integer.MAX_VALUE) {
                    m_spareCount++;
                    setCounters();
                }
            }
        });

        m_vwSpareMinus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (m_spareCount != 0) {
                    m_spareCount--;
                    setCounters();
                }
            }
        });

        m_vwRowInProjectEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!TextUtils.isEmpty(m_vwRowInProjectEditText.getText())){
                    if (tryParse(m_vwRowInProjectEditText.getText().toString()) != null) {
                        m_rowInProject = Integer.parseInt(m_vwRowInProjectEditText.getText().toString());
                        m_vwRowInProjectEditText.setSelection(m_vwRowInProjectEditText.getText().length());
                    }
                }
            }
        });

        m_vwStitchCounter.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!TextUtils.isEmpty(m_vwStitchCounter.getText())){
                    if (tryParse(m_vwStitchCounter.getText().toString()) != null) {
                        m_stitchCount = tryParse(m_vwStitchCounter.getText().toString());
                        m_vwStitchCounter.setSelection(m_vwStitchCounter.getText().length());
                    }
                }
            }
        });

        m_vwSpareCounter.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!TextUtils.isEmpty(m_vwSpareCounter.getText())){
                    if (tryParse(m_vwSpareCounter.getText().toString()) != null) {
                        m_spareCount = tryParse(m_vwSpareCounter.getText().toString());
                        m_vwSpareCounter.setSelection(m_vwSpareCounter.getText().length());
                    }
                }
            }
        });
    }

    //helper method
    public static Integer tryParse(String text) {
        try {
            return Integer.parseInt(text);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    @Override
    protected void onPause() {
        super.onPause();

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        //Save the pattern with the SAVED_PATTERN_STATE with the current project num appended
        editor.putInt(SAVED_PATTERN_INDEX + m_pattern.getProjectNum(), m_rowsIndex);
        editor.putInt(SAVED_PATTERN_ROW_PROJECT  + m_pattern.getProjectNum(), m_rowInProject);
        editor.putInt(SAVED_PATTERN_STITCH_COUNT + m_pattern.getProjectNum(), m_stitchCount);
        editor.putInt(SAVED_PATTERN_SPARE_COUNT + m_pattern.getProjectNum(), m_spareCount);

        editor.apply();
    }

    @Override
    protected void onResume() {
        super.onResume();

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        m_rowsIndex = sharedPreferences.getInt(SAVED_PATTERN_INDEX + m_pattern.getProjectNum(), 0);
        m_rowInProject = sharedPreferences.getInt(SAVED_PATTERN_ROW_PROJECT + m_pattern.getProjectNum(), 1);
        m_stitchCount = sharedPreferences.getInt(SAVED_PATTERN_STITCH_COUNT + m_pattern.getProjectNum(), 0);
        m_spareCount= sharedPreferences.getInt(SAVED_PATTERN_SPARE_COUNT + m_pattern.getProjectNum(), 0);

        getRowsFromDatabase();
        setPattern();
        getSupportActionBar().setTitle(m_pattern.getProjectName());
    }

    //Sets the screen to display the pattern at index m_rowsIndex and sets the display to reflect the counter variables
    private void setPattern() {
        if (m_rowsIndex >= m_arrRowList.size()) {
            m_rowsIndex = 0;
            m_stitchCount = 0;
            m_spareCount = 0;
            m_rowInProject = 1;
        }
        if (m_arrRowList.size() != 0) {
            m_vwRowDisplay.setText(getString(R.string.row_num_display, m_arrRowList.get(m_rowsIndex).getRowNum()));
            if (TextUtils.isEmpty(m_arrRowList.get(m_rowsIndex).getPatternText())) {
                m_vwPatternDisplay.setText(R.string.no_text_entered);
            }
            else {
                m_vwPatternDisplay.setText(m_arrRowList.get(m_rowsIndex).getPatternText()); //set pattern text if not empty
            }
        }
        else {
            Toast.makeText(this, "Error: no pattern found", Toast.LENGTH_SHORT).show();
        }
        setCounters();
        setRowInProject();
    }

    private void setCounters() {
        m_vwStitchCounter.setText(m_stitchCount + "");
        m_vwSpareCounter.setText(m_spareCount + "");
    }

    private void setRowInProject() {
        //m_vwRowDisplay.setText(getString(R.string.row_num_display, m_arrRowList.get(m_rowsIndex).getRowNum()));

        m_vwRowInProject.setText(R.string.row_in_project_display);
        m_vwRowInProjectEditText.setText(m_rowInProject + "");
    }

    protected void forwardRow() {
        if (m_rowsIndex == m_arrRowList.size() - 1) {
            m_rowsIndex = 0;
        }
        else {
            m_rowsIndex++;
        }
        m_stitchCount = 0;
        m_spareCount = 0;
        if (m_rowInProject != Integer.MAX_VALUE) {
            m_rowInProject++;
        }

        setPattern();
    }

    protected void backRow() {
        if (m_rowInProject != 1) {
            if (m_rowsIndex == 0) {
                m_rowsIndex = m_arrRowList.size() - 1;
            }
            else {
                m_rowsIndex--;
            }
            m_stitchCount = 0;
            m_spareCount = 0;
            m_rowInProject--;

            setPattern();
        }
    }

    private void goToEditPattern(Pattern pattern) {
        Intent intent = new Intent(this, CreateProjectActivity.class);

        if (pattern != null) {
            intent.putExtra(ProjectSelectionActivity.PATTERN_KEY, pattern);
        }

        startActivityForResult(intent, 19);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 19 && resultCode == RESULT_OK) {
            m_pattern = (Pattern) data.getSerializableExtra(ProjectSelectionActivity.PATTERN_KEY);
        }
    }
}
