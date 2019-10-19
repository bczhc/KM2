package pers.zhc.km;

import android.content.ContentValues;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class Addition extends MainActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.addition_activity);
        EditText et = findViewById(R.id.et);
        Button btn = findViewById(R.id.btn);
        btn.setOnClickListener(v -> {
            SQLiteDatabase db = getDB();
            long currentTimeMillis = System.currentTimeMillis();
            ContentValues cv = new ContentValues();
            cv.put("t", currentTimeMillis);
            cv.put("content", et.getText().toString());
            cv.put("mark", 2);
            try {
                db.insertOrThrow("doc", null, cv);
            } catch (SQLException e) {
                e.printStackTrace();
                Toast.makeText(this, e.toString(), Toast.LENGTH_SHORT).show();
            }
            Snackbar snackbar = Snackbar.make(btn, R.string.recording_success, Snackbar.LENGTH_SHORT);
            snackbar.setAction("×", v1 -> snackbar.dismiss()).show();
        });
    }
}