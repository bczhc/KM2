package pers.zhc.km;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.*;

import java.io.File;

public class MainActivity extends Activity {
    private SQLiteDatabase db;
    private LinearLayout ll;
    private Mark mark;

    //熟悉1 有印象2 忘记3
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        db = getDB();
        Button forgetBtn = findViewById(R.id.forget);
        Button haveImpressionBtn = findViewById(R.id.have_impression);
        Button familiarBtn = findViewById(R.id.familiar);
        ImageView iv = findViewById(R.id.ib);
        ll = findViewById(R.id.ll);
        iv.setOnClickListener(v -> startActivity(new Intent(this, Addition.class)));
//        toggle();//first show
        mark = new Mark();
        forgetBtn.setOnClickListener(v -> toggle());
    }

    private void toggle() {
        ContentBean[] earliestThreeMarkedCB = getEarliestThreeMarkedCB();
        ContentBean contentBean = earliestThreeMarkedCB[mark.nextMark()];
        show(contentBean.content);
        ContentValues cv = new ContentValues();
        db.update("doc", cv, "do_t=?", new String[]{String.valueOf(contentBean.do_t)});
    }

    private ContentBean[] getEarliestThreeMarkedCB() {
        ContentBean[] contentBeans1 = new ContentBean[3];
        for (int i = 1; i <= 3; i++) {
            Cursor cursor = db.rawQuery("SELECT MIN(do_t) AND mark=?", new String[]{String.valueOf(i)});
            cursor.moveToFirst();
            contentBeans1[0] = new ContentBean(
                    cursor.getInt(2)
                    , cursor.getLong(0)
                    , cursor.getString(1)
                    , cursor.getLong(3));
            cursor.close();
        }
        return contentBeans1;
    }

    /*private void updateMyContent() {
        for (int i = 1; i <= 3; i++) {
            Cursor cursor = db.rawQuery("SELECT * FROM doc WHERE mark=?", new String[]{String.valueOf(i)});
            int cursorCount = cursor.getCount();
            cursor.moveToFirst();
            for (int j = 0; j < cursorCount; j++) {
                cursor.moveToNext();
            }
            cursor.close();
        }
    }*/

    /*private ContentBean[] getMarkedContentBean(int mark, Cursor cursor) {
        ContentBean[] contentBean = null;
        try {
            contentBean = new ContentBean[cursor.getCount()];
            if (cursor.moveToFirst()) {
                int i = 0;
                do {
                    contentBean[i] = new ContentBean(cursor.getLong(0), cursor.getString(1));
                    ++i;
                } while (cursor.moveToNext());
            }
            cursor.close();
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, e.toString(), Toast.LENGTH_SHORT).show();
        }
        return contentBean;
    }*/

    private void show(String content) {
        TextView tv2 = new TextView(this);
        tv2.setText(content);
        tv2.setTextSize(20F);
        ll.addView(tv2);
    }

    SQLiteDatabase getDB() {
        SQLiteDatabase db = SQLiteDatabase.openOrCreateDatabase(getDBFile(), null);
        try {
            db.execSQL("CREATE TABLE IF NOT EXISTS doc(\n" +
                    "    t long,\n" +
                    "    content text NOT NULL,\n" +
                    "    mark integer,\n" +
                    "    do_t long DEFAULT (0)\n" +
                    ")");
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, e.toString(), Toast.LENGTH_SHORT).show();
        }
        return db;
    }


    private File getDBFile() {
        File d = new File(getFilesDir().getParent() + File.separator + "databases");
        if (!d.exists()) System.out.println("d.mkdirs() = " + d.mkdirs());
        return new File(d + File.separator + "doc.db");
    }

    /*private static int rand(int aWeight, int bWeight, int cWeight) {
        double random = Math.random();
        double all = aWeight + bWeight + cWeight;
        return random < aWeight / all ? 1 : (random < (aWeight + bWeight) / all ? 2 : 3);
    }*/

    private class ContentBean {
        private int mark;

        private long t_mills;

        private String content;

        private long do_t;

        ContentBean(int mark, long t_mills, String content, long do_t) {
            this.mark = mark;
            this.t_mills = t_mills;
            this.content = content;
            this.do_t = do_t;
        }
    }

    private class Mark {
        private int mark = 1;

        private int nextMark() {
            if (mark >= 4) mark = 1;
            return mark++;
        }
    }
}