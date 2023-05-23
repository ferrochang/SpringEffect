package com.example.sample.myapplication

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.lib.effect.effect.widget.SpringRelativeLayout
import com.example.sample.myapplication.databinding.FragmentMainBinding
import com.example.sample.sample.view.CustomAdapter
import com.example.sample.sample.view.DefaultItemAnimator
import com.example.sample.sample.view.MyItemClickListener

class  MainFragment : Fragment() {
    private val viewModel: GameViewModel by viewModels()
    private var mDemoAnimAdd = true
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = FragmentMainBinding.inflate(inflater)
        Log.d("MainFragment", "onCreateView");
        binding.lifecycleOwner = this
        binding.viewModel = viewModel

        val springLayout = binding.root.findViewById<View>(R.id.spring_layout) as SpringRelativeLayout
        springLayout.addSpringView(R.id.recyclerView)

        val recyclerView = binding.root.findViewById<RecyclerView>(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.adapter = CustomAdapter(viewModel.data, object : MyItemClickListener {
            override fun onListItemClicked(view: View?) {
                val itemAdapterPosition = recyclerView.getChildAdapterPosition(view!!)

                if (itemAdapterPosition == RecyclerView.NO_POSITION) {
                    return
                }
                val adapter = (recyclerView.adapter) as CustomAdapter
                if (mDemoAnimAdd) {
                    for (c in 0..0) adapter.addItemAtPosition(itemAdapterPosition)
                } else {
                    adapter.removeItemAtPosition(itemAdapterPosition)
                }

            }

        })
        recyclerView.edgeEffectFactory = springLayout.createEdgeEffectFactory()
        recyclerView.itemAnimator = DefaultItemAnimator()
        return binding.root
    }
}