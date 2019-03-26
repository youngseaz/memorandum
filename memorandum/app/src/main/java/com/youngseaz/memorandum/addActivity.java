package com.youngseaz.memorandum;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MenuInflater;
import android.widget.EditText;
import android.widget.TextView;
import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Date;

public class addActivity extends AppCompatActivity
{
    private TextView tv_date;
    private EditText et_content;
    private NoteDateBaseHelper DBHelper;
    public int enter_state = 0;             // 用来区分是新建的note还是更改原来的note
    public String last_content;             // 用来获取edit_text内容

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);

        InitView();    // 初始化界面
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)        //  初始化toolbar
    {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.add_toolbar, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)   // toolbar菜单事件处理
    {
        switch (item.getItemId())
        {
            case R.id.done_action:
                SQLiteDatabase db = DBHelper.getReadableDatabase();
                // 获取edit_text内容
                String content = et_content.getText().toString();

                // 添加一个新的日志
                if (enter_state == 0)
                {
                    if (!content.equals(""))
                    {
                        // 获取当前时间
                        Date date = new Date();
                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
                        String dateString = sdf.format(date);

                        // 将日期、编辑内容写入数据库
                        ContentValues values = new ContentValues();
                        values.put("content", content);
                        values.put("date", dateString);
                        db.insert("note", null, values);
                        Intent intent = new Intent(addActivity.this, MainActivity.class);
                        startActivity(intent);
                        finish();

                    }
                    else
                    {
                        Toast.makeText(addActivity.this, "nothing be written!", Toast.LENGTH_SHORT).show();
                    }
                }

                // 查看并修改一个已有的日志
                else
                {
                    ContentValues values = new ContentValues();
                    values.put("content", content);
                    db.update("note", values, "content = ?", new String[]{last_content});
                    Intent intent = new Intent(addActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                }

                break;
            case R.id.cancel_action:
                Intent intent = new Intent(addActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
                break;
        }
        return true;
    }

    private void InitView()
    {
        tv_date = findViewById(R.id.tv_date);
        et_content = findViewById(R.id.et_content);
        DBHelper = new NoteDateBaseHelper(this);

        //获取此时时刻时间
        Date date = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        String dateString = sdf.format(date);
        tv_date.setText(dateString);

        //接收内容和id
        Bundle myBundle = this.getIntent().getExtras();
        last_content = myBundle.getString("info");
        enter_state = myBundle.getInt("enter_state");
        et_content.setText(last_content);

    }

}

