package com.song.qsrefreshlayout;

import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import org.song.refreshlayout.QSRefreshLayout;

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
                    }, 2000);
                }
            }
        });

        List<Map<String, Object>> listems = new ArrayList<Map<String, Object>>();
        for (int i = 0; i < 20; i++) {
            Map<String, Object> listem = new HashMap<String, Object>();
            listem.put("name", "=======" + i);
            listems.add(listem);
        }
        listView.setAdapter(new SimpleAdapter(this, listems, android.R.layout.simple_list_item_1, new String[]{"name"},
                new int[]{android.R.id.text1}) {
            public View getView(int position, View convertView, ViewGroup parent) {
                Log.e("===",""+position);
                return super.getView(position, convertView, parent);
            }
        });

    }
}
