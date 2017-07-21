# QSRefreshLayout 
![](https://github.com//tohodog/QSRefreshLayout/raw/master/top.gif)
![](https://github.com//tohodog/QSRefreshLayout/raw/master/bottom.gif)
<br>
[============DEMO.APK=============](https://github.com//tohodog/QSRefreshLayout/raw/master/qsrefresh-1.0.apk)
<br>



安卓上下拉框架
====
  * 刷新view模块化,可自由更换扩展,head foot可通用
  * 轻松实现各种刷新效果,不用自己处理触摸事件
  * 支持任意可滑动的控件
  * 更多效果更新中...

## XML
```
  <org.song.refreshlayout.QSRefreshLayout
        android:id="@+id/qs"
        android:layout_width="match_parent"
        android:layout_height="match_parent">
        
        <!--head-->
        <org.song.refreshlayout.refreshview.BarRefreshView
            android:layout_width="match_parent"
            android:layout_height="2dp" />

        <android.support.v7.widget.RecyclerView
            android:id="@+id/list"
            android:layout_width="match_parent"
            android:layout_height="match_parent"
            android:background="#ffffff" />
            
        <!--foot-->
        <org.song.refreshlayout.refreshview.CircleImageView
            android:layout_width="wrap_content"
            android:layout_height="wrap_content" />

    </org.song.refreshlayout.QSRefreshLayout>
```
## JAVA
```
QSRefreshLayout qsRefreshLayout = (QSRefreshLayout) findViewById(R.id.qs);

qsRefreshLayout.setHeadRefreshView(new CircleImageView(this));
                    
qsRefreshLayout.setFootRefreshView(new BarRefreshView(this));
                    
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
            }
        });
```

## DIY
实现IRefreshView接口
```
    View getView();

    void updateStatus(int status);//更新刷新状态

    void updateProgress(float progress);//刷新进度 0 ~ 1(触发刷新)~ 更大

    boolean isBringToFront();//是否view放在顶层

    float dragRate();//滑动速度控制

    int triggerDistance();//触发刷新的距离 [实际触摸距离*dragRate()>triggerDistance() 触发刷新

    int maxDistance();//最大滑动距离 <=0不限制

    int getThisViewOffset(int offset);//根据触摸位移 确定该view的位移 大于0=headview 小于0=footview

    int getTargetOffset(int offset);//根据触摸位移 确定滚动view的位移 大于0=headview 小于0=footview

    int completeAnimaDuration();//完成刷新后到消失 的动画时间, <=0使用默认时间

    void isHeadView(boolean isHead);//是否顶部刷新view
```



