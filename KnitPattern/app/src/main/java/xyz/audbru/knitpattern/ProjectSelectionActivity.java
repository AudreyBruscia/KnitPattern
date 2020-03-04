package xyz.audbru.knitpattern;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.ActionMode;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class ProjectSelectionActivity extends AppCompatActivity {

    protected ListView m_vwProjectSelectView;
    protected TextView m_vwNoProjects;

    protected ArrayAdapter<Pattern> m_projectAdapter;

    protected FloatingActionButton m_vwAddProjectButton;

    public static final String PATTERN_KEY = "PATTERN_KEY";

    protected int positionToDelete;

    protected android.support.v7.view.ActionMode actionMode;
    protected android.support.v7.view.ActionMode.Callback callback;

    @Override
    protected void onResume() {
        super.onResume();
        m_projectAdapter.clear();

        PatternSQLHelper patternSQLHelper = new PatternSQLHelper(this);
        Pattern[] projectArray = patternSQLHelper.getAllProjectTitles().toArray(new Pattern[0]);
        m_projectAdapter.addAll(projectArray);
        if (projectArray.length > 0) {
            m_vwNoProjects.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_project_selection);

        m_vwProjectSelectView = (ListView) findViewById(R.id.selectProject);
        m_vwAddProjectButton = (FloatingActionButton) findViewById(R.id.addProjectButton);
        m_vwNoProjects = (TextView) findViewById(R.id.no_projects);


        m_projectAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1);
        m_vwProjectSelectView.setAdapter(m_projectAdapter);

        m_vwProjectSelectView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                goToDisplayPattern(m_projectAdapter.getItem(position));
            }
        });

        m_vwProjectSelectView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                if (actionMode != null) {
                    return false;
                }

                positionToDelete = position;

                // Start the CAB using the ActionMode.Callback defined above
                actionMode = ProjectSelectionActivity.this.startSupportActionMode(callback);
                view.setSelected(true);
                return true;
            }
        });

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
                        //Remove it from database and notify the adapter
                        int deleteProjectId = m_projectAdapter.getItem(positionToDelete).getProjectNum();

                        //remove from SharedPreferences
                        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(ProjectSelectionActivity.this);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.remove(DisplayRowActivity.SAVED_PATTERN_INDEX + deleteProjectId);
                        editor.remove(DisplayRowActivity.SAVED_PATTERN_ROW_PROJECT  + deleteProjectId);
                        editor.remove(DisplayRowActivity.SAVED_PATTERN_STITCH_COUNT + deleteProjectId);
                        editor.remove(DisplayRowActivity.SAVED_PATTERN_SPARE_COUNT + deleteProjectId);
                        boolean removed =  editor.commit();

                        PatternSQLHelper patternSQLHelper = new PatternSQLHelper(ProjectSelectionActivity.this);
                        patternSQLHelper.deleteProjectById(deleteProjectId);

                        m_projectAdapter.remove(m_projectAdapter.getItem(positionToDelete));
                        m_projectAdapter.notifyDataSetChanged();

                        if (m_projectAdapter.getCount() < 1) {
                            m_vwNoProjects.setVisibility(View.VISIBLE);
                        }
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


        m_vwAddProjectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToCreateProject();
            }
        });
    }


    private void goToDisplayPattern(Pattern editPattern) {
        Intent intent = new Intent(this, DisplayRowActivity.class);

        if (editPattern != null) {
            intent.putExtra(PATTERN_KEY, editPattern);
        }

        startActivity(intent);
    }

    private void goToCreateProject() {
        Intent intent = new Intent(this, CreateProjectActivity.class);
        startActivity(intent);
    }

}
