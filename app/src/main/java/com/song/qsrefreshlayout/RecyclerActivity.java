package com.song.qsrefreshlayout;

import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.song.qsrefreshlayout.refreshview.ElemeRefreshView;
import com.song.qsrefreshlayout.refreshview.JDRefreshView;
import com.song.qsrefreshlayout.refreshview.PlainRefreshView;
import com.song.qsrefreshlayout.refreshview.SunRefreshView;

import org.song.refreshlayout.IRefreshView;
import org.song.refreshlayout.QSRefreshLayout;
import org.song.refreshlayout.refreshview.BarRefreshView;
import org.song.refreshlayout.refreshview.CircleImageView;
import org.song.refreshlayout.refreshview.IOSRefreshView;
import org.song.refreshlayout.refreshview.XMLRefreshView;

import java.util.ArrayList;
import java.util.List;

public class RecyclerActivity extends AppCompatActivity {

    QSRefreshLayout qsRefreshLayout;
    RecyclerView recyclerView;

    List<IRefreshView> lists = new ArrayList<>();
    List<IRefreshView> listsF = new ArrayList<>();

    int index;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recyle);
        lists.add(new IOSRefreshView(this));
        lists.add(new CircleImageView(this));
        lists.add(new BarRefreshView(this));
        lists.add(new XMLRefreshView(this));
        lists.add(new PlainRefreshView(this));
        lists.add(new SunRefreshView(this));
        lists.add(new JDRefreshView(this));
        lists.add(new ElemeRefreshView(this));

        listsF.add(new IOSRefreshView(this));
        listsF.add(new CircleImageView(this));
        listsF.add(new BarRefreshView(this));
        listsF.add(new XMLRefreshView(this));
        listsF.add(new PlainRefreshView(this));
        listsF.add(new SunRefreshView(this));
        listsF.add(new JDRefreshView(this));
        listsF.add(new ElemeRefreshView(this));


        qsRefreshLayout = (QSRefreshLayout) findViewById(R.id.qs);
        recyclerView = (RecyclerView) findViewById(R.id.list);
        initDate();
    }

    private void initDate() {
        //qsRefreshLayout.setFootRefreshView(new IOSRefreshView(this));
        qsRefreshLayout.enterHeadRefreshing(true);
        qsRefreshLayout.setRefreshListener(new QSRefreshLayout.RefreshListener() {
            @Override
            public void changeStatus(boolean isHead, int status) {
                if (status == QSRefreshLayout.STATUS_REFRESHING) {
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            qsRefreshLayout.refreshComplete();
                        }
                    }, 3000);
                }
                if (status == QSRefreshLayout.STATUS_NORMAL) {
                    index++;
                    if (index >= lists.size())
                        index = 0;
                    qsRefreshLayout.setHeadRefreshView(lists.get(index));
                    qsRefreshLayout.setFootRefreshView(listsF.get(index));
                }
            }
        });

        LinearLayoutManager l = new LinearLayoutManager(this);
        l.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(l);
        recyclerView.setAdapter(new Adpter());
    }


    private class Adpter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            TextView tv = new TextView(RecyclerActivity.this);
            tv.setPadding(0, 50, 0, 50);
            tv.setLayoutParams(new ViewGroup.LayoutParams(-1,-2));
            return new RecyclerView.ViewHolder(tv) {
            };
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            String s = "RecyclerView==" + position;
            if (position == 0)
                s += "  点击自动进入刷新-有动画";
            if (position == 1)
                s += "  点击自动进入刷新-无动画";
            ((TextView) holder.itemView).setText(s);
            holder.itemView.setTag(position);
            holder.itemView.setOnClickListener(l);
        }

        View.OnClickListener l = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int tag = (int) v.getTag();
                if (tag == 0)
                    qsRefreshLayout.enterHeadRefreshing(true);
                if (tag == 1)
                    qsRefreshLayout.enterHeadRefreshing(false);

            }
        };

        @Override
        public int getItemCount() {
            return 20;
        }
    }
}
