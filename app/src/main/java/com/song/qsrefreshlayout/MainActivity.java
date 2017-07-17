package com.song.qsrefreshlayout;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import org.song.refreshlayout.QSRefreshLayout;
import org.song.refreshlayout.refreshview.BarRefreshView;
import org.song.refreshlayout.refreshview.PlainRefreshView;
import org.song.refreshlayout.refreshview.XMLRefreshView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class MainActivity extends AppCompatActivity {

    QSRefreshLayout qsRefreshLayout;
    ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        qsRefreshLayout = (QSRefreshLayout) findViewById(R.id.qs);
        listView = (ListView) findViewById(R.id.list);
        initDate();

        startActivity(new Intent(this,RecyleActivity.class));
    }

    private void initDate() {
        qsRefreshLayout.setRefreshListener(new QSRefreshLayout.RefreshListener() {
            @Override
            public void changeStatus(boolean b, int status) {
                if (status == QSRefreshLayout.STATUS_REFRESHING) {
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            qsRefreshLayout.refreshComplete();
                        }
                    }, 3000);
                }
            }
        });
        qsRefreshLayout.setHeadRefreshView(new XMLRefreshView(this));
        List<Map<String, Object>> listems = new ArrayList<>();
        for (int i = 0; i < 20; i++) {
            Map<String, Object> listem = new HashMap<>();
            listem.put("name", "半透明scroll=" + i);
            listems.add(listem);
        }
        listView.setAdapter(new SimpleAdapter(this, listems, android.R.layout.simple_list_item_1, new String[]{"name"},
                new int[]{android.R.id.text1}) {
            public View getView(int position, View convertView, ViewGroup parent) {
                return super.getView(position, convertView, parent);
            }
        });
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                qsRefreshLayout.enterHeadRefreshing(true);
            }
        },555);

    }
}
