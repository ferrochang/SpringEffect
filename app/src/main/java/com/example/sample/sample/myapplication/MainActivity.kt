package com.example.sample.sample.myapplication

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ObjectAnimator
import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.util.SparseBooleanArray
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.animation.AlphaAnimation
import android.view.animation.Animation
import android.view.animation.Animation.AnimationListener
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.SimpleItemAnimator
import com.example.lib.effect.effect.widget.SpringRelativeLayout
import com.example.sample.myapplication.R
import com.example.sample.sample.view.CustomAdapter
import com.example.sample.sample.view.DefaultItemAnimator
import com.example.sample.sample.view.MyItemClickListener

class MainActivity : Activity() {
    //private var mSpringLayout: SpringRelativeLayout? = null
    private var mRecyclerView: RecyclerView? = null
    //protected var mLayoutManager: RecyclerView.LayoutManager? = null
    //protected var mDataset: Array<String?>
    protected var mAdapter: CustomAdapter? = null
    private var mDemoAnimAdd = true
    //private var mSpringLayoutList: ArrayList<ViewGroup>? = ArrayList()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        //initDataset()
        /*
        for (g in mSpringLayoutList!!) {
            mSpringLayoutList!!.size
            g.overScrollMode = View.OVER_SCROLL_NEVER
        }

        mSpringLayoutList!!.clear()
        mSpringLayoutList = null

         */
        val mSpringLayout = findViewById<View>(R.id.spring_layout) as SpringRelativeLayout
        mSpringLayout.addSpringView(R.id.recyclerView)
        mRecyclerView = findViewById<View>(R.id.recyclerView) as RecyclerView
        //mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        //var mLayoutManager = LinearLayoutManager(this)
        mRecyclerView!!.layoutManager = LinearLayoutManager(this)
        //mAdapter = CustomAdapter(mDataset, mItemClickListener)
        mAdapter = CustomAdapter(Array<String?>(DATASET_COUNT) { "This is element #$it" }, mItemClickListener)
        mRecyclerView!!.adapter = mAdapter
        mRecyclerView!!.edgeEffectFactory = mSpringLayout.createEdgeEffectFactory()
        mRecyclerView!!.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            var state = RecyclerView.SCROLL_STATE_IDLE
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                //super.onScrollStateChanged(recyclerView, newState);
                state = newState
            }

            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                if (state == RecyclerView.SCROLL_STATE_DRAGGING && dy != 0) {
                    //mSpringLayout.onRecyclerViewScrolled();
                }

                //super.onScrolled(recyclerView, dx, dy);
            }
        })
        val itemAnimator = DefaultItemAnimator()
        //itemAnimator.setRemoveDuration(10);
        //itemAnimator.setAddDuration(80);
        mRecyclerView!!.itemAnimator = itemAnimator
        //disableEdgeEffect(mRecyclerView);
    }

    override fun onResume() {
        super.onResume()
    }

    /*
    private fun initDataset() {
        mDataset = arrayOfNulls(DATASET_COUNT)
        for (i in 0 until DATASET_COUNT) {
            mDataset[i] = "This is element #$i"
        }
    }

     */

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val intent: Intent
        when (item.itemId) {
            R.id.menu_anim -> {
                mDemoAnimAdd = !mDemoAnimAdd
                item.setTitle(if (mDemoAnimAdd) R.string.anim_add else R.string.anim_remove)
            }

            R.id.menu_show_list -> {
                intent = Intent(
                    this@MainActivity,
                    ListActivity::class.java
                )
                startActivity(intent)
            }

            R.id.menu_show_grid -> {
                intent = Intent(
                    this@MainActivity,
                    GridViewActivity::class.java
                )
                startActivity(intent)
            }

            R.id.menu_show_scroll -> {
                intent = Intent(
                    this@MainActivity,
                    ScrollViewActivity::class.java
                )
                startActivity(intent)
            }

            R.id.menu_pull_refresh -> {
                intent = Intent(
                    this@MainActivity,
                    PullToRefreshActivity::class.java
                )
                startActivity(intent)
            }

            R.id.menu_horizontal_recyclerview -> {
                intent = Intent(
                    this@MainActivity,
                    HorizontalRecyclerViewActivity::class.java
                )
                startActivity(intent)
            }

            R.id.menu_webview -> {
                intent = Intent(
                    this@MainActivity,
                    WebViewActivity::class.java
                )
                startActivity(intent)
            }

            R.id.menu_pull_refresh_scrollview -> {
                intent = Intent(
                    this@MainActivity,
                    PullToRefreshScrollViewActivity::class.java
                )
                startActivity(intent)
            }

            R.id.menu_pull_refresh_scrollview2 -> {
                intent = Intent(
                    this@MainActivity,
                    PullToRefreshScrollViewActivity2::class.java
                )
                startActivity(intent)
            }

            R.id.menu_refresh_scrollview -> {
                intent = Intent(
                    this@MainActivity,
                    RefreshScrollViewActivity::class.java
                )
                startActivity(intent)
            }

            R.id.menu_pull_refresh_recyclerview -> {
                intent = Intent(
                    this@MainActivity,
                    PullToRefreshRecyclerViewActivity::class.java
                )
                startActivity(intent)
            }

            R.id.menu_show_list2 -> {
                intent = Intent(
                    this@MainActivity,
                    ListViewActivity2::class.java
                )
                startActivity(intent)
            }

            R.id.menu_horizontal_scrollview -> {
                intent = Intent(
                    this@MainActivity,
                    HorizontalScrollViewActivity::class.java
                )
                startActivity(intent)
            }

            R.id.menu_show_expand_list -> {
                intent = Intent(
                    this@MainActivity,
                    ExpandableListDemo::class.java
                )
                startActivity(intent)
            }

            R.id.menu_spring_recyclerview -> {
                intent = Intent(
                    this@MainActivity,
                    SpringRecyclerActivity::class.java
                )
                startActivity(intent)
            }

            R.id.menu_settings -> {
                intent = Intent(
                    this@MainActivity,
                    MySettingsActivity::class.java
                )
                startActivity(intent)
            }

            R.id.menu_nestscroll -> {
                intent = Intent(
                    this@MainActivity,
                    NestScrollActivity::class.java
                )
                startActivity(intent)
            }
        }
        return super.onOptionsItemSelected(item)
    }

    var mItemClickListener: MyItemClickListener = object : MyItemClickListener {
        override fun onListItemClicked(view: View?) {
            val itemAdapterPosition = mRecyclerView!!.getChildAdapterPosition(view!!)
            if (itemAdapterPosition == RecyclerView.NO_POSITION) {
                return
            }
            if (mDemoAnimAdd) {
                for (c in 0..0) mAdapter!!.addItemAtPosition(itemAdapterPosition)
                //mAdapter.addItemAtPosition(itemAdapterPosition);

            } else {
                //for (int c = 0; c < 1; c++)
                //    mAdapter.removeItemAtPosition(0);
                //mAdapter.removeItemAtPosition(0);
                mAdapter!!.removeItemAtPosition(itemAdapterPosition)
            }
        }
    }

    internal inner class FlyAnimator : SimpleItemAnimator() {
        var removeHolders: MutableList<RecyclerView.ViewHolder> = ArrayList()
        var removeAnimators: MutableList<RecyclerView.ViewHolder> = ArrayList()
        var moveHolders: MutableList<RecyclerView.ViewHolder> = ArrayList()
        var moveAnimators: MutableList<RecyclerView.ViewHolder> = ArrayList()
        override fun animateRemove(holder: RecyclerView.ViewHolder): Boolean {
            removeHolders.add(holder)
            return true
        }

        override fun animateAdd(holder: RecyclerView.ViewHolder): Boolean {
            return false
        }

        override fun animateMove(
            holder: RecyclerView.ViewHolder,
            fromX: Int,
            fromY: Int,
            toX: Int,
            toY: Int
        ): Boolean {
            holder.itemView.translationY = (fromY - toY).toFloat()
            moveHolders.add(holder)
            return true
        }

        override fun animateChange(
            oldHolder: RecyclerView.ViewHolder,
            newHolder: RecyclerView.ViewHolder,
            fromLeft: Int,
            fromTop: Int,
            toLeft: Int,
            toTop: Int
        ): Boolean {
            return false
        }

        override fun runPendingAnimations() {
            if (!removeHolders.isEmpty()) {
                for (holder in removeHolders) {
                    remove(holder)
                }
                removeHolders.clear()
            }
            if (!moveHolders.isEmpty()) {
                for (holder in moveHolders) {
                    move(holder)
                }
                moveHolders.clear()
            }
        }

        override fun endAnimation(item: RecyclerView.ViewHolder) {}
        override fun endAnimations() {}
        override fun isRunning(): Boolean {
            return !(removeHolders.isEmpty() && removeAnimators.isEmpty() && moveHolders.isEmpty() && moveAnimators.isEmpty())
        }

        private fun remove(holder: RecyclerView.ViewHolder) {
            removeAnimators.add(holder)
            //TranslateAnimation animation = new TranslateAnimation(0, 0, 0, 0);
            val animation = AlphaAnimation(1.0f, 0.0f)
            animation.duration = 300
            animation.setAnimationListener(object : AnimationListener {
                override fun onAnimationStart(animation: Animation) {
                    dispatchRemoveStarting(holder)
                }

                override fun onAnimationEnd(animation: Animation) {
                    removeAnimators.remove(holder)
                    dispatchRemoveFinished(holder)
                    if (!isRunning) {
                        dispatchAnimationsFinished()
                    }
                }

                override fun onAnimationRepeat(animation: Animation) {}
            })
            holder.itemView.startAnimation(animation)
        }

        private fun move(holder: RecyclerView.ViewHolder) {
            moveAnimators.add(holder)
            val animator = ObjectAnimator.ofFloat(
                holder.itemView,
                "translationY", holder.itemView.translationY, 0f
            )
            animator.duration = 300
            animator.addListener(object : AnimatorListenerAdapter() {
                override fun onAnimationStart(animation: Animator) {
                    dispatchMoveStarting(holder)
                }

                override fun onAnimationEnd(animation: Animator) {
                    dispatchMoveFinished(holder)
                    moveAnimators.remove(holder)
                    if (!isRunning) dispatchAnimationsFinished()
                }
            })
            animator.start()
        }
    }

    fun disableEdgeEffect(scrollView: View) {
        val parent = scrollView.parent
        if (parent.javaClass.name.contains("SpringRelativeLayout")) {
            try {
                val superClass: Class<*> = scrollView.javaClass.superclass
                Log.d("ferro", "superClass $superClass")
                if (superClass.name.contains("android.widget.ScrollView")) {
                    return
                } else if (superClass.name.contains("android.webkit.WebView")) {
                    return
                }
                for (field in parent.javaClass.declaredFields) {
                    Log.d("ferro", " field " + field.genericType.typeName)
                    if (field.genericType.typeName == "android.util.SparseBooleanArray") {
                        Log.d("ferro", "mattcchchh yes")
                    }
                }
                val f1 = parent.javaClass.getDeclaredField("mSpringViews")
                f1.isAccessible = true
                val targetType = f1.type
                val objectValue = targetType.newInstance()
                val value = f1[parent]
                if (value is SparseBooleanArray) {
                    //mOriginalEdgeEffectMember = (SparseBooleanArray) value;
                }
                f1[parent] = SparseBooleanArray()
            } catch (e: Exception) {
                Log.e("TAG", "Exception $e")
            }
        } else if (scrollView.javaClass.name.contains("SpringRecyclerView")) {
            //mOriginalOverScrollMode = scrollView.getOverScrollMode();
            scrollView.overScrollMode = View.OVER_SCROLL_NEVER
        } else if (scrollView.javaClass.name.contains("SpringListView2")) {
            //mOriginalOverScrollMode = scrollView.getOverScrollMode();
            scrollView.overScrollMode = View.OVER_SCROLL_NEVER
        }
    }

    var builder: AlertDialog.Builder? = null
    fun createDialog() {
        builder = AlertDialog.Builder(this)
        // Get the layout inflater
        val inflater = layoutInflater
        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the
        // dialog layout
        builder!!.setTitle("Parameter")
        builder!!.setCancelable(false)
        builder!!.setView(inflater.inflate(R.layout.seek, null)) // Add action buttons
            .setPositiveButton("OK") { dialog, id -> dialog.dismiss() }
        builder!!.create()
        builder!!.show()
    }

    companion object {
        private const val DATASET_COUNT = 12
    }
}