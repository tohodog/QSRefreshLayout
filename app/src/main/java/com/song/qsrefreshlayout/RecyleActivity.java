package com.song.qsrefreshlayout;

import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import org.song.refreshlayout.QSRefreshLayout;

public class RecyleActivity extends AppCompatActivity {

    QSRefreshLayout qsRefreshLayout;
    RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recyle);
        qsRefreshLayout = (QSRefreshLayout) findViewById(R.id.qs);
        recyclerView = (RecyclerView) findViewById(R.id.list);
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
                    }, 3000);
                }
            }
        });

        LinearLayoutManager l = new LinearLayoutManager(this);
        l.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(l);
        recyclerView.setAdapter(new Adpter());
    }


    class Adpter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            TextView tv = new TextView(RecyleActivity.this);
            tv.setPadding(0,50,0,50);
            return new RecyclerView.ViewHolder(tv) {
            };
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            ((TextView) holder.itemView).setText("RecyclerView==" + position);

        }

        @Override
        public int getItemCount() {
            return 20;
        }
    }
}
