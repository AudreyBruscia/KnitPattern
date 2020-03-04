package xyz.audbru.knitpattern;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.ActionMode;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by User on 4/19/2016.
 */
public class CreateProjectActivity extends AppCompatActivity {

    protected ArrayList<Row> m_arrRowList;

    protected RowListAdapter m_rowAdapter;

    protected ListView m_vwRowLayout;

    protected TextView m_vwRowNum;

    protected EditText m_vwRowEditText;

    protected EditText m_vwTitleEditText;  //Is the project title field

    protected int positionToDelete;

    protected Pattern m_pattern;

    protected int m_rowNum;  //Tells how many rows are in this project

    protected boolean editMode = false;

    protected android.support.v7.view.ActionMode actionMode;
    protected android.support.v7.view.ActionMode.Callback callback;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //need to get the projectNum and rowNum (which is the number of rows contained in the project)********

        m_arrRowList = new ArrayList<Row>();
        m_rowAdapter = new RowListAdapter(this, m_arrRowList);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDefaultDisplayHomeAsUpEnabled(true);

        getSupportActionBar().setTitle(R.string.navbar_new_project_title);

        initLayout();
        m_vwTitleEditText.setSelection(m_vwTitleEditText.getText().length());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.savemenu, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent intent = new Intent();
        intent.putExtra(ProjectSelectionActivity.PATTERN_KEY, m_pattern);
        switch (item.getItemId()) {
            case android.R.id.home:
                //saveProjectToDatabase();
                setResult(RESULT_OK, intent);
                finishActivity(19);
                super.onBackPressed();
                break;
            case R.id.menu_save:
                if (TextUtils.isEmpty(m_vwTitleEditText.getText().toString().trim())) {
                    // Don't save project if title is empty
                    Toast.makeText(this, "Title is empty", Toast.LENGTH_SHORT).show();
                    break;
                }
                else if (editMode) {
                    PatternSQLHelper patternSQLHelper = new PatternSQLHelper(this);
                    patternSQLHelper.deleteRowsById(m_pattern.getProjectNum());
                    saveRowsToDatabase(m_pattern.getProjectNum());

                    if (!m_vwTitleEditText.getText().toString().equals(m_pattern.getProjectName())) {  //They edited the title
                        modifyProjectNameInDatabase(m_pattern.getProjectNum(), m_vwTitleEditText.getText().toString());
                    }
                }
                else {
                    saveProjectToDatabase();
                }
                setResult(RESULT_OK, intent);
                finishActivity(19);
                //super.onBackPressed();
                finish();
                break;
            case R.id.menu_add_row:
                //On addRowButton press
                //Row newRow = new Row(++m_rowNum, null);  //new Row containing a row number, but no text yet
                Row newRow = new Row(m_arrRowList.size() + 1, null);  //new Row containing a row number, but no text yet
                addRow(newRow);
                break;
            default:
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * Method is used to encapsulate the code that initializes and sets the
     * Layout for this Activity.
     */
    protected void initLayout() {
        setContentView(R.layout.activity_create_project);
        m_vwRowLayout = (ListView) findViewById(R.id.addProjectListView);
        m_vwRowLayout.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                if (actionMode != null) {
                    return false;
                }

                positionToDelete = position;

                // Start the CAB using the ActionMode.Callback defined above
                actionMode = CreateProjectActivity.this.startSupportActionMode(callback);
                view.setSelected(true);
                return true;
            }
        });
        m_vwRowLayout.setAdapter(m_rowAdapter);

        m_vwTitleEditText = (EditText) findViewById(R.id.addTitle);
        m_vwRowNum = (TextView) findViewById(R.id.rowNumber);
        m_vwRowEditText = (EditText) findViewById(R.id.enterPattern);


        m_rowNum = 1; //*****  Change this, it's just for default right now ****

        getPatternFromIntent();

        callback = new ActionMode.Callback() {
            @Override
            public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                MenuInflater inflater = mode.getMenuInflater();
                inflater.inflate(R.menu.actionmenu, menu);

                return true;
            }

            @Override
            public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
                return false;
            }

            @Override
            public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.menu_remove:
                        m_arrRowList.remove(positionToDelete);
                        for (int i = positionToDelete; i < m_arrRowList.size(); i++) {
                            m_arrRowList.get(i).setRowNum(i + 1);
                        }
                        if (m_arrRowList.size() == 0) {
                            addFirstRow();
                        }
                        m_rowAdapter.notifyDataSetChanged();
                        mode.finish(); // Action picked, so close the CAB
                        return true;
                    default:
                        return false;
                }
            }

            @Override
            public void onDestroyActionMode(ActionMode mode) {
                actionMode = null;
            }
        };
    }

    private void getPatternFromIntent() {
        m_pattern = (Pattern) getIntent().getSerializableExtra(ProjectSelectionActivity.PATTERN_KEY);
        if (m_pattern != null) {
            editMode = true;
            getSupportActionBar().setTitle(R.string.navbar_edit_title); //set navbar title to Edit
            //Won't need to fetch from database again because now getting it from Intent

            m_vwTitleEditText.setText(m_pattern.getProjectName());
            PatternSQLHelper patternSQLHelper = new PatternSQLHelper(this);

            m_arrRowList.clear();
            m_arrRowList.addAll(m_pattern.getArrRows());
            m_rowAdapter.notifyDataSetChanged();
            m_rowNum = m_arrRowList.size();  //TODO: not sure if right
        }
        else {
            editMode = false;
            addFirstRow();
            m_pattern = new Pattern();
        }
    }

    //helper method
    protected void addFirstRow() {
        Row newRow = new Row(1, null);  //first row that will show up on the add
        addRow(newRow);
    }

    // Add new blank row
    protected void addRow(Row row) {
        m_arrRowList.add(row);
        m_rowAdapter.notifyDataSetChanged();
    }

    public void saveProjectToDatabase() {
        if (TextUtils.isEmpty(m_vwTitleEditText.getText().toString())) {
            // Don't save project if title is empty
        }
        else {
            PatternSQLHelper patternSQLHelper = new PatternSQLHelper(this);

            m_pattern.m_projectNum = (int) patternSQLHelper.insertInProjectTable(m_vwTitleEditText.getText().toString());
            m_pattern.setProjectName(m_vwTitleEditText.getText().toString());

            saveRowsToDatabase(m_pattern.m_projectNum);
        }
    }

    public void saveRowsToDatabase(int projectNum) {
        PatternSQLHelper patternSQLHelper = new PatternSQLHelper(this);

        List<Row> rows = m_rowAdapter.getRowListArray();

        for (Row row : rows) {
            patternSQLHelper.insertInPatternTable(projectNum, row.getRowNum(), row.getPatternText());
        }
    }

    public void modifyProjectNameInDatabase(int projectNum, String newName) {
        PatternSQLHelper patternSQLHelper = new PatternSQLHelper(this);

        patternSQLHelper.modifyProjectName(projectNum, newName);
    }
}