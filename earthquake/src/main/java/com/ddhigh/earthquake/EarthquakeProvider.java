package com.ddhigh.earthquake;

import android.app.SearchManager;
import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;

import java.util.HashMap;

/**
 * @project Study
 * @package com.ddhigh.earthquake
 * @user xialeistudio
 * @date 2016/2/23 0023
 */
public class EarthquakeProvider extends ContentProvider {
    //发布CONTENT_URI
    public static final Uri CONTENT_URI = Uri.parse("content://com.ddhigh.earthquakeprovider/earthquakes");
    //创建共有变量，描述数据库中使用的列名
    public static final String KEY_ID = "_id";
    public static final String KEY_DATE = "date";
    public static final String KEY_DETAILS = "details";
    public static final String KEY_SUMMARY = "summary";
    public static final String KEY_LOCATION_LAT = "latitude";
    public static final String KEY_LOCATION_LNG = "longitude";
    public static final String KEY_MAGNITUDE = "magnitude";
    public static final String KEY_LINK = "link";

    EarthquakeDatabaseHelper dbHelper;

    //创建用来区分不同URI请求的常量
    private static final int QUAKES = 1;
    private static final int QUAKE_ID = 2;
    private static final int SEARCH = 3;

    private static final UriMatcher uriMatcher;


    private static final HashMap<String, String> SEARCH_PROJECTION_MAP;

    static {
        uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcher.addURI("com.ddhigh.earthquakeprovider", "earthquakes", QUAKES);
        uriMatcher.addURI("com.ddhigh.earthquakeprovider", "earthquakes/#", QUAKE_ID);
        uriMatcher.addURI("com.ddhigh.earthquakeprovider", SearchManager.SUGGEST_URI_PATH_QUERY, SEARCH);
        uriMatcher.addURI("com.ddhigh.earthquakeprovider", SearchManager.SUGGEST_URI_PATH_QUERY + "/*", SEARCH);
        uriMatcher.addURI("com.ddhigh.earthquakeprovider", SearchManager.SUGGEST_URI_PATH_SHORTCUT, SEARCH);
        uriMatcher.addURI("com.ddhigh.earthquakeprovider", SearchManager.SUGGEST_URI_PATH_SHORTCUT + "/*", SEARCH);


        SEARCH_PROJECTION_MAP = new HashMap<>();
        SEARCH_PROJECTION_MAP.put(SearchManager.SUGGEST_COLUMN_TEXT_1, KEY_SUMMARY + " As " + SearchManager.SUGGEST_COLUMN_TEXT_1);
        SEARCH_PROJECTION_MAP.put("_id", KEY_ID + " AS " + "_id");
    }

    @Override
    public boolean onCreate() {
        Context context = getContext();

        dbHelper = new EarthquakeDatabaseHelper(context, EarthquakeDatabaseHelper.DATABASE_NAME, null, EarthquakeDatabaseHelper.DATABASE_VERSION);

        return true;
    }

    @Nullable
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        SQLiteDatabase database = dbHelper.getWritableDatabase();
        SQLiteQueryBuilder qb = new SQLiteQueryBuilder();

        qb.setTables(EarthquakeDatabaseHelper.EARTHQUAKE_TABLE);
        //如果是行查询，就把结果集限制为传入的行
        switch (uriMatcher.match(uri)) {
            case QUAKE_ID:
                qb.appendWhere(KEY_ID + "=" + uri.getPathSegments().get(1));
                break;
            case SEARCH:
                qb.appendWhere(KEY_SUMMARY + " LIKE \"%" + uri.getPathSegments().get(1) + "%\"");
                qb.setProjectionMap(SEARCH_PROJECTION_MAP);
                break;
            default:
                break;
        }
        //如果没有排序，则按时间排序
        String orderBy;
        if (TextUtils.isEmpty(sortOrder)) {
            orderBy = KEY_DATE;
        } else {
            orderBy = sortOrder;
        }
        //查询SQLite
        Cursor c = qb.query(database, projection, selection, selectionArgs, null, null, orderBy);
        //注册当游标结果集改变时将通知的上下文ContentResolver
        c.setNotificationUri(getContext().getContentResolver(), uri);
        return c;
    }

    @Nullable
    @Override
    public String getType(Uri uri) {
        switch (uriMatcher.match(uri)) {
            case QUAKES:
                return "vnd.android.cursor.dir/vnd.ddhigh.earthquake";
            case QUAKE_ID:
                return "vnd.android.cursor.item/vnd.ddhigh.earthquake";
            case SEARCH:
                return SearchManager.SUGGEST_MIME_TYPE;
            default:
                throw new IllegalArgumentException("Unsupported URI: " + uri);
        }
    }

    @Nullable
    @Override
    public Uri insert(Uri uri, ContentValues values) {
        SQLiteDatabase database = dbHelper.getWritableDatabase();

        long rowID = database.insert(EarthquakeDatabaseHelper.EARTHQUAKE_TABLE, "quake", values);
        if (rowID > 0) {
            Uri uri1 = ContentUris.withAppendedId(CONTENT_URI, rowID);
            getContext().getContentResolver().notifyChange(uri, null);
            return uri;
        } else {
            throw new SQLException("Failed to insert row into " + uri);
        }
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        SQLiteDatabase database = dbHelper.getWritableDatabase();
        int count;
        switch (uriMatcher.match(uri)) {
            case QUAKES:
                count = database.delete(EarthquakeDatabaseHelper.EARTHQUAKE_TABLE, selection, selectionArgs);
                break;
            case QUAKE_ID:
                String segment = uri.getPathSegments().get(1);
                count = database.delete(EarthquakeDatabaseHelper.EARTHQUAKE_TABLE, KEY_ID + "=" + segment + (!TextUtils.isEmpty(selection) ? " AND (" + selection + ')' : ""), selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Unsupported URI: " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return count;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        SQLiteDatabase database = dbHelper.getWritableDatabase();
        int count;
        switch (uriMatcher.match(uri)) {
            case QUAKES:
                count = database.update(EarthquakeDatabaseHelper.EARTHQUAKE_TABLE, values, selection, selectionArgs);
                break;
            case QUAKE_ID:
                String segment = uri.getPathSegments().get(1);
                count = database.update(EarthquakeDatabaseHelper.EARTHQUAKE_TABLE, values, KEY_ID + "=" + segment + (!TextUtils.isEmpty(selection) ? " AND (" + selection + ')' : ""), selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Unsupported URI: " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return count;
    }

    private static class EarthquakeDatabaseHelper extends SQLiteOpenHelper {
        private static final String TAG = "EarthquakeProvider";

        private static final String DATABASE_NAME = "earthquake.db";
        private static final int DATABASE_VERSION = 1;
        private static final String EARTHQUAKE_TABLE = "earthquakes";

        private static final String DATABASE_CREATE =
                "create table " + EARTHQUAKE_TABLE + " ("
                        + KEY_ID + " integer primary key autoincrement, "
                        + KEY_DATE + " INTEGER, "
                        + KEY_DETAILS + " TEXT, "
                        + KEY_SUMMARY + " TEXT, "
                        + KEY_LOCATION_LAT + " FLOAT, "
                        + KEY_LOCATION_LNG + " FLOAT, "
                        + KEY_MAGNITUDE + " FLOAT, "
                        + KEY_LINK + " TEXT);";

        //数据库句柄
        private SQLiteDatabase earthquakeDB;

        public EarthquakeDatabaseHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
            super(context, name, factory, version);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(DATABASE_CREATE);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            Log.w(TAG, "Upgrading database from version " + oldVersion + " to " + newVersion + ", which will destroy all old data.");

            db.execSQL("DROP TABLE IF EXISTS " + EARTHQUAKE_TABLE);
            db.execSQL(DATABASE_CREATE);
        }
    }
}
