package pers.zhc.km;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.*;

import java.io.File;

public class MainActivity extends Activity {
    private SQLiteDatabase db;
    private LinearLayout ll;

    //忘记 1； 有印象2； 熟悉3；
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
        firstShow();
    }

    private void firstShow() {
        for (int i = 1; i <= 3; i++) {
            ContentBean[] markedContentBean = getMarkedContentBean(i);
            if (!(markedContentBean == null || markedContentBean.length == 0)) {
                show(markedContentBean[0].content);
                break;
            }
        }
    }

    private ContentBean[] getMarkedContentBean(int mark) {
        ContentBean[] contentBean = null;
        try {
            Cursor cursor = db.rawQuery("SELECT * FROM doc WHERE mark=?"
                    , new String[]{String.valueOf(mark)});
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
    }

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
                    "    mark integer\n" +
                    ")");
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, e.toString(), Toast.LENGTH_SHORT).show();
        }
        return db;
    }


    private File getDBFile() {
        File d = new File(getFilesDir() + File.separator + "db");
        if (!d.exists()) System.out.println("d.mkdirs() = " + d.mkdirs());
        return new File(d + File.separator + "doc.db");
    }

    private static int rand(int aWeight, int bWeight, int cWeight) {
        double random = Math.random();
        double all = aWeight + bWeight + cWeight;
        return random < aWeight / all ? 1 : (random < (aWeight + bWeight) / all ? 2 : 3);
    }

    private class ContentBean {
        private long t_mills;

        private String content;

        ContentBean(long t_mills, String content) {
            this.t_mills = t_mills;
            this.content = content;
        }
    }
}