package xyz.audbru.knitpattern;

import android.provider.BaseColumns;

/**
 * Created by User on 4/19/2016.
 */
public class PatternContract {
    public PatternContract() {}

    public static abstract class PatternEntry implements BaseColumns {
        public static final String PROJECT_TABLE_NAME = "Project";
        public static final String PATTERN_TABLE_NAME = "Pattern";
        public static final String ID = "table_id";
        public static final String COLUMN_NAME_PROJECT_TITLE = "project_title";
        public static final String COLUMN_NAME_PROJECT_NUM = "project_num";
        public static final String COLUMN_NAME_ROW_NUM = "row_num";
        public static final String COLUMN_NAME_ROW_TEXT = "row_text";
    }
}
