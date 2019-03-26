package com.youngseaz.memorandum;

import android.content.ClipData;
import android.support.v7.app.AppCompatActivity;
import android.database.sqlite.SQLiteDatabase;
import android.database.Cursor;
import android.os.Bundle;
import android.app.AlarmManager;
import android.icu.text.*;
import android.os.Build;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.DialogInterface;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.AdapterView.*;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.ListView;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MenuInflater;
import android.app.AlertDialog.Builder;

import java.util.*;

public class MainActivity extends AppCompatActivity
    implements OnItemClickListener, OnItemLongClickListener
{
    private TextView textView;
    private TextView tv_content;
    private SimpleAdapter simpleAdapter;
    private List<Map<String, Object>> dataList;
    private ListView listview;
    private NoteDateBaseHelper DbHelper;
    private SQLiteDatabase DB;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        InitView();
    }

    // 进入活动或者重新进入活动刷新备忘录列表
    @Override
    protected void onStart()
    {
        super.onStart();
        RefreshNotesList();
    }

    // 备忘录ListView初始化，由onCreate调用
    private void InitView()
    {
        tv_content = findViewById(R.id.tv_content);
        listview = findViewById(R.id.list_view);
        dataList = new ArrayList<>();
        DbHelper = new NoteDateBaseHelper(this);
        DB = DbHelper.getReadableDatabase();
        listview.setOnItemClickListener(this);
        listview.setOnItemLongClickListener(this);
    }



    // 单击ListView中某一项的点击监听事件
    @Override
    public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
        // 获取ListView中此个item中的内容
        String content = listview.getItemAtPosition(arg2) + "";
        String content1 = content.substring(content.indexOf("=") + 1,
                content.indexOf(","));
        Intent intent = new Intent(MainActivity.this, addActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString("info", content1);
        bundle.putInt("enter_state", 1);
        intent.putExtras(bundle);
        startActivity(intent);

    }

    // 长按ListView中的某一项时弹出对话框是否删除
    @Override
    public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
                                   final int arg2, long arg3)
    {
        Builder builder = new Builder(this);
        builder.setTitle("delete this note");
        builder.setMessage("delete this note？");
        builder.setPositiveButton("ok", new DialogInterface.OnClickListener() {
            @Override
            // 获取L中istView被长按的项目的内容
            public void onClick(DialogInterface dialog, int which)
            {
                String content = listview.getItemAtPosition(arg2) + "";
                String content1 = content.substring(content.indexOf("=") + 1,
                        content.indexOf(","));
                DB.delete("note", "content = ?", new String[]{content1});
                // 某一项被删除后刷新ListView
                RefreshNotesList();
            }
        });
        builder.setNegativeButton("cancel", new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
            }
        });
        builder.create();
        builder.show();
        return true;
    }


    // 刷新ListView
   public void RefreshNotesList()
    {
        // 如果dataList已经有的内容，全部删掉
        // 并且更新simpleAdapter
        int size = dataList.size();
        if (size > 0) {
            dataList.removeAll(dataList);
            simpleAdapter.notifyDataSetChanged();
        }

        // 数据库使用SQLite，从数据库读取信息
        Cursor cursor = DB.query("note", null, null, null, null, null, null);
        while (cursor.moveToNext()) {
            String name = cursor.getString(cursor.getColumnIndex("content"));
            String date = cursor.getString(cursor.getColumnIndex("date"));
            Map<String, Object> map = new HashMap<>();     //HashMap<String, Object>();
            map.put("tv_content", name);
            map.put("tv_date", date);
            dataList.add(map);
        }
        simpleAdapter = new SimpleAdapter(this, dataList, R.layout.notes_item,
                new String[]{"tv_content", "tv_date"}, new int[]{
                R.id.tv_content, R.id.tv_date});
        listview.setAdapter(simpleAdapter);
    }


    //  初始化activity_main的toolbar
    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_toolbar, menu);
        return true;
    }

    @Override
    // activity_main的toolbar菜单事件处理
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            // 点击add菜单转跳到addActivity
            case R.id.add_action:
                Intent intent = new Intent(MainActivity.this, addActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("info", "");
                bundle.putInt("enter_state", 0);
                intent.putExtras(bundle);
                startActivity(intent);
                break;
        }
        return true;
    }

}

