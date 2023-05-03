package com.example.sample.sample.myapplication;

import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SimpleItemAnimator;

import com.example.sample.sample.view.CustomAdapter;
import com.example.sample.sample.view.DefaultItemAnimator;
import com.example.sample.sample.view.MyItemClickListener;
import com.example.lib.effect.effect.widget.SpringRelativeLayout;
import com.example.sample.myapplication.R;


import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends Activity {

    private static final int DATASET_COUNT = 12;

    private SpringRelativeLayout mSpringLayout;
    private RecyclerView mRecyclerView;
    protected RecyclerView.LayoutManager mLayoutManager;
    protected String[] mDataset;
    protected CustomAdapter mAdapter;
    private boolean mDemoAnimAdd = true;
    private ArrayList<ViewGroup> mSpringLayoutList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initDataset();

        for (ViewGroup g: mSpringLayoutList) {
            mSpringLayoutList.size();
            g.setOverScrollMode(View.OVER_SCROLL_NEVER);
        }
        mSpringLayoutList.clear();
        mSpringLayoutList = null;

        mSpringLayout = (SpringRelativeLayout) findViewById(R.id.spring_layout);
        mSpringLayout.addSpringView(R.id.recyclerView);
        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        //mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mAdapter = new CustomAdapter(mDataset, mItemClickListener);
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setEdgeEffectFactory(mSpringLayout.createEdgeEffectFactory());

        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            int state = RecyclerView.SCROLL_STATE_IDLE;
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                //super.onScrollStateChanged(recyclerView, newState);
                state = newState;
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                if (state == RecyclerView.SCROLL_STATE_DRAGGING && dy != 0) {
                    //mSpringLayout.onRecyclerViewScrolled();
                }

                //super.onScrolled(recyclerView, dx, dy);
            }
        });

        DefaultItemAnimator itemAnimator = new DefaultItemAnimator();
        //itemAnimator.setRemoveDuration(10);
        //itemAnimator.setAddDuration(80);
        mRecyclerView.setItemAnimator(itemAnimator);
        //disableEdgeEffect(mRecyclerView);

    }

    @Override
    protected void onResume() {
        super.onResume();


    }

    private void initDataset() {
        mDataset = new String[DATASET_COUNT];
        for (int i = 0; i < DATASET_COUNT; i++) {
            mDataset[i] = "This is element #" + i;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent intent;
        switch(item.getItemId()) {
            case R.id.menu_anim:
                mDemoAnimAdd = !mDemoAnimAdd;
                item.setTitle(mDemoAnimAdd?R.string.anim_add:R.string.anim_remove);
                break;
            case R.id.menu_show_list:
                intent = new Intent(MainActivity.this,
                        ListActivity.class);
                startActivity(intent);
                break;
            case R.id.menu_show_grid:
                intent = new Intent(MainActivity.this,
                        GridViewActivity.class);
                startActivity(intent);
                break;
            case R.id.menu_show_scroll:
                intent = new Intent(MainActivity.this,
                        ScrollViewActivity.class);
                startActivity(intent);
                break;
            case R.id.menu_pull_refresh:
                intent = new Intent(MainActivity.this,
                        PullToRefreshActivity.class);
                startActivity(intent);
                break;
            case R.id.menu_horizontal_recyclerview:
                intent = new Intent(MainActivity.this,
                        HorizontalRecyclerViewActivity.class);
                startActivity(intent);
                break;
            case R.id.menu_webview:
                intent = new Intent(MainActivity.this,
                        WebViewActivity.class);
                startActivity(intent);
                break;
            case R.id.menu_pull_refresh_scrollview:
                intent = new Intent(MainActivity.this,
                        PullToRefreshScrollViewActivity.class);
                startActivity(intent);
                break;
            case R.id.menu_pull_refresh_scrollview2:
                intent = new Intent(MainActivity.this,
                        PullToRefreshScrollViewActivity2.class);
                startActivity(intent);
                break;
                /*
            case R.id.menu_refresh_scrollview:
                intent = new Intent(MainActivity.this,
                        RefreshScrollViewActivity.class);
                startActivity(intent);
                break;
                 */
            case R.id.menu_pull_refresh_recyclerview:
                intent = new Intent(MainActivity.this,
                        PullToRefreshRecyclerViewActivity.class);
                startActivity(intent);
                break;
            case R.id.menu_show_list2:
                intent = new Intent(MainActivity.this,
                        ListViewActivity2.class);
                startActivity(intent);
                break;
            case R.id.menu_horizontal_scrollview:
                intent = new Intent(MainActivity.this,
                        HorizontalScrollViewActivity.class);
                startActivity(intent);
                break;
            case R.id.menu_show_expand_list:
                intent = new Intent(MainActivity.this,
                        ExpandableListDemo.class);
                startActivity(intent);
                break;
            case R.id.menu_spring_recyclerview:
                intent = new Intent(MainActivity.this,
                        SpringRecyclerActivity.class);
                startActivity(intent);
                break;
            case R.id.menu_settings:
                intent = new Intent(MainActivity.this,
                        MySettingsActivity.class);
                startActivity(intent);
                break;
            case R.id.menu_nestscroll:
                intent = new Intent(MainActivity.this,
                        NestScrollActivity.class);
                startActivity(intent);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    MyItemClickListener mItemClickListener = new MyItemClickListener() {
        @Override
        public void onListItemClicked(View view) {
            int itemAdapterPosition = mRecyclerView.getChildAdapterPosition(view);
            if (itemAdapterPosition == RecyclerView.NO_POSITION) {
                return;
            }
            if (mDemoAnimAdd) {
                for (int c = 0; c < 1; c++)
                    mAdapter.addItemAtPosition(itemAdapterPosition);
                //mAdapter.addItemAtPosition(itemAdapterPosition);
            } else {
                //for (int c = 0; c < 1; c++)
                //    mAdapter.removeItemAtPosition(0);
                //mAdapter.removeItemAtPosition(0);
                mAdapter.removeItemAtPosition(itemAdapterPosition);
            }
        }
    };


    class FlyAnimator extends SimpleItemAnimator {
        List<RecyclerView.ViewHolder> removeHolders = new ArrayList<>();
        List<RecyclerView.ViewHolder> removeAnimators = new ArrayList<>();
        List<RecyclerView.ViewHolder> moveHolders = new ArrayList<>();
        List<RecyclerView.ViewHolder> moveAnimators = new ArrayList<>();
        @Override
        public boolean animateRemove(RecyclerView.ViewHolder holder) {
            removeHolders.add(holder);
            return true;
        }
        @Override
        public boolean animateAdd(RecyclerView.ViewHolder holder) {
            return false;
        }
        @Override
        public boolean animateMove(RecyclerView.ViewHolder holder, int fromX, int fromY, int toX, int toY) {
            holder.itemView.setTranslationY(fromY - toY);
            moveHolders.add(holder);
            return true;
        }
        @Override
        public boolean animateChange(RecyclerView.ViewHolder oldHolder, RecyclerView.ViewHolder newHolder, int fromLeft, int fromTop, int toLeft, int toTop) {
            return false;
        }
        @Override
        public void runPendingAnimations() {
            if(!removeHolders.isEmpty()) {
                for(RecyclerView.ViewHolder holder : removeHolders) {
                    remove(holder);
                }
                removeHolders.clear();
            }
            if(!moveHolders.isEmpty()){
                for(RecyclerView.ViewHolder holder : moveHolders) {
                    move(holder);
                }
                moveHolders.clear();
            }
        }
        @Override
        public void endAnimation(RecyclerView.ViewHolder item) {
        }
        @Override
        public void endAnimations() {
        }
        @Override
        public boolean isRunning() {
            return !(removeHolders.isEmpty() && removeAnimators.isEmpty() && moveHolders.isEmpty() && moveAnimators.isEmpty());
        }

        private void remove(final RecyclerView.ViewHolder holder){
            removeAnimators.add(holder);
            //TranslateAnimation animation = new TranslateAnimation(0, 0, 0, 0);
            AlphaAnimation animation = new AlphaAnimation(1.0f, 0.0f);
            animation.setDuration(300);
            animation.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {
                    dispatchRemoveStarting(holder);
                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    removeAnimators.remove(holder);
                    dispatchRemoveFinished(holder);
                    if(!isRunning()){
                        dispatchAnimationsFinished();
                    }
                }

                @Override
                public void onAnimationRepeat(Animation animation) {

                }
            });
            holder.itemView.startAnimation(animation);
        }

        private void move(final RecyclerView.ViewHolder holder){
            moveAnimators.add(holder);
            ObjectAnimator animator = ObjectAnimator.ofFloat(holder.itemView,
                    "translationY", holder.itemView.getTranslationY(), 0);
            animator.setDuration(300);
            animator.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationStart(android.animation.Animator animation) {
                    dispatchMoveStarting(holder);
                }

                @Override
                public void onAnimationEnd(android.animation.Animator animation) {
                    dispatchMoveFinished(holder);
                    moveAnimators.remove(holder);
                    if(!isRunning()) dispatchAnimationsFinished();
                }
            });
            animator.start();
        }

    }
    void disableEdgeEffect(View scrollView) {
        ViewParent parent = scrollView.getParent();
        if (parent.getClass().getName().contains("SpringRelativeLayout")) {

            try {
                Class<?> superClass = scrollView.getClass().getSuperclass();
                Log.d("ferro", "superClass " + superClass);
                if (superClass.getName().contains("android.widget.ScrollView")) {
                    return;
                } else if (superClass.getName().contains("android.webkit.WebView")) {
                    return;
                }

                for (Field field : parent.getClass().getDeclaredFields()) {
                    Log.d("ferro", " field " + field.getGenericType().getTypeName());
                    if (field.getGenericType().getTypeName().equals("android.util.SparseBooleanArray")) {
                        Log.d("ferro", "mattcchchh yes");
                    }
                }

                    Field f1 = parent.getClass().getDeclaredField("mSpringViews");
                f1.setAccessible(true);
                Class<?> targetType = f1.getType();
                Object objectValue = targetType.newInstance();

                Object value = f1.get(parent);
                if (value instanceof SparseBooleanArray) {
                    //mOriginalEdgeEffectMember = (SparseBooleanArray) value;
                }

                f1.set(parent, new SparseBooleanArray());
            } catch (Exception e) {
                Log.e("TAG", "Exception " + e);
            }
        } else if (scrollView.getClass().getName().contains("SpringRecyclerView")) {
            //mOriginalOverScrollMode = scrollView.getOverScrollMode();
            scrollView.setOverScrollMode(View.OVER_SCROLL_NEVER);
        } else if (scrollView.getClass().getName().contains("SpringListView2")) {
            //mOriginalOverScrollMode = scrollView.getOverScrollMode();
            scrollView.setOverScrollMode(View.OVER_SCROLL_NEVER);
        }

    }

    AlertDialog.Builder builder;
    void createDialog() {
        builder = new AlertDialog.Builder(this);
        // Get the layout inflater
        LayoutInflater inflater = getLayoutInflater();
        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the
        // dialog layout
        builder.setTitle("Parameter");
        builder.setCancelable(false);
        builder.setView(inflater.inflate(R.layout.seek, null))
                // Add action buttons
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                    }
                });
        builder.create();
        builder.show();
    }


}
