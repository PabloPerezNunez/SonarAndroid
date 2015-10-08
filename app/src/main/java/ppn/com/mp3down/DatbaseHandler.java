package ppn.com.mp3down;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeSet;

import Webs.Cancion;
import Webs.CancionMp3Freex;

/**
 * Created by PPN on 01/09/2015.
 */
public class DatbaseHandler extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "mp3downdb";
    //Tabla historial
    private static final String TABLE_HISTORY = "history";
    private static final String KEY_TITLE = "title";
    private static final String KEY_ARTIST = "artist";
    private static final String KEY_LINK = "link";

    //Tabla busquedas
    private static final String TABLE_SEARCHES= "searches";
    private static final String KEY_SEARCH = "search";

    //Tabla error descargas
    private static final String TABLE_ERROR_DOWNLOADS= "error_downloads";

    public DatbaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_HISTORY_TABLE = "CREATE TABLE " + TABLE_HISTORY + "("
                + KEY_TITLE + " TEXT," + KEY_ARTIST
                + " TEXT," + KEY_LINK + " TEXT PRIMARY KEY)";

        db.execSQL(CREATE_HISTORY_TABLE);

        String CREATE_SEARCHES_TABLE = "CREATE TABLE " + TABLE_SEARCHES + "("
                + KEY_SEARCH + " TEXT PRIMARY KEY)";

        db.execSQL(CREATE_SEARCHES_TABLE);

        String CREATE_ERROR_DOWNLOADS_TABLE = "CREATE TABLE " + TABLE_ERROR_DOWNLOADS + "("
                + KEY_TITLE + " TEXT," + KEY_ARTIST
                + " TEXT," + KEY_LINK + " TEXT PRIMARY KEY)";

        db.execSQL(CREATE_ERROR_DOWNLOADS_TABLE);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {

    }

    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public void addHistorySong (Cancion c){

        try {
            SQLiteDatabase db = this.getWritableDatabase();

            ContentValues values = new ContentValues();
            values.put(KEY_TITLE, c.getTitle());
            values.put(KEY_ARTIST, c.getArtist());
            values.put(KEY_LINK, c.getLink().toString());

            db.insertOrThrow(TABLE_HISTORY, null, values);
            db.close();
        }catch (Exception e){
            Principal.log("Ya existe esta cancion en el historial, no se anadira");
        }

    }

    public void deleteHistorySong (Cancion c){

        SQLiteDatabase db = this.getWritableDatabase();

        db.delete(TABLE_HISTORY, KEY_LINK + " = ?", new String[]{String.valueOf(c.getLink())});
        db.close();

    }

    public List<Cancion> getAllHistorySongs(){
        List <Cancion> cancionList = new ArrayList<Cancion>();
        String selectQuery = "SELECT * FROM " + TABLE_HISTORY ;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery,null);

        if (cursor.moveToFirst()){
            do {
                try {
                    String title = cursor.getString(0);
                    String artist = cursor.getString(1);
                    URL link = new URL(cursor.getString(2));

                    Cancion cancion = new Cancion(title, artist, link);

                    if (link.toString().contains("mp3freex")){
                        cancion = new CancionMp3Freex(title, artist, link);
                    }

                    cancionList.add(cancion);

                }catch (Exception e){
                    e.printStackTrace();
                }

            }while (cursor.moveToNext());
        }

        return cancionList;
    }

    public void dropHistoryTable(){

        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_HISTORY);
        db.close();


    }

    public int clearHistoryTable() {

        SQLiteDatabase db = this.getWritableDatabase();
        int ret = db.delete(TABLE_HISTORY, "1", null);
        db.close();

        return ret;
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public void addSearchSong (String c){

        try {
            SQLiteDatabase db = this.getWritableDatabase();

            ContentValues values = new ContentValues();
            values.put(KEY_SEARCH, c);

            db.insertOrThrow(TABLE_SEARCHES, null, values);
            db.close();

        }catch (Exception e){
            Principal.log("Ya existe esta busqueda, no se anadira");
        }

    }

    public void deleteSearchSong (String c){

        SQLiteDatabase db = this.getWritableDatabase();

        db.delete(TABLE_SEARCHES, KEY_SEARCH + " = ?", new String[]{c});
        db.close();

    }

    public String[] getAllSearchSongs(){

        String[] busquedas;
        String selectQuery = "SELECT * FROM " + TABLE_SEARCHES ;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery,null);

        busquedas = new String[cursor.getCount()];
        int i =0;
        if (cursor.moveToFirst()){
            do {
                try {
                    String title = cursor.getString(0);

                    busquedas[i]=title;

                    i++;

                }catch (Exception e){
                    e.printStackTrace();
                }

            }while (cursor.moveToNext());
        }

        return busquedas;
    }

    public int clearSearchesTable() {

        SQLiteDatabase db = this.getWritableDatabase();
        int ret = db.delete(TABLE_SEARCHES, "1", null);
        db.close();

        return ret;
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    public void addErrorDownload (Cancion c){

        try {
            SQLiteDatabase db = this.getWritableDatabase();

            ContentValues values = new ContentValues();
            values.put(KEY_TITLE, c.getTitle());
            values.put(KEY_ARTIST, c.getArtist());
            values.put(KEY_LINK, c.getLink().toString());

            db.insertOrThrow(TABLE_ERROR_DOWNLOADS, null, values);
            db.close();
        }catch (Exception e){
            Principal.log("Ya existe esta cancion, no se anadira");
        }

    }

    public void deleteErrorDownload (Cancion c){

        SQLiteDatabase db = this.getWritableDatabase();

        db.delete(TABLE_ERROR_DOWNLOADS, KEY_LINK + " = ?", new String[]{String.valueOf(c.getLink())});
        db.close();


    }

    public TreeSet<Cancion> getAllErrorDownloads(){

        TreeSet <Cancion> cancionList = new TreeSet<Cancion>();
        String selectQuery = "SELECT * FROM " + TABLE_ERROR_DOWNLOADS ;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery,null);

        if (cursor.moveToFirst()){
            do {
                try {
                    String title = cursor.getString(0);
                    String artist = cursor.getString(1);
                    URL link = new URL(cursor.getString(2));

                    Principal.log(title+","+artist+","+link.toString());

                    Cancion cancion = new Cancion(title, artist, link);

                    if (link.toString().contains("mp3freex")){
                        cancion = new CancionMp3Freex(title, artist, link);
                    }

                    cancionList.add(cancion);

                }catch (Exception e){
                    e.printStackTrace();
                }

            }while (cursor.moveToNext());
        }

        return cancionList;
    }

}
