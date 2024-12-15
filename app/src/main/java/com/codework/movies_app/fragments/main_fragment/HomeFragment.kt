package com.codework.movies_app.fragments.main_fragment
import android.animation.ArgbEvaluator
import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.HorizontalScrollView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.widget.SearchView
import androidx.core.content.ContextCompat

import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import androidx.viewpager2.widget.ViewPager2
import com.codework.movies_app.R
import com.codework.movies_app.adapters.ItemAdapter
import com.codework.movies_app.adapters.ViewPagerAdapter
import com.codework.movies_app.data.Item
import com.codework.movies_app.databinding.FragmentHomeBinding
import com.codework.movies_app.viewmodes.ItemViewModel
import dagger.hilt.android.AndroidEntryPoint
import org.w3c.dom.Text


class HomeFragment: Fragment() {
    private lateinit var viewModel: ItemViewModel
    private lateinit var binding: FragmentHomeBinding
    private lateinit var recyclerView: RecyclerView
    private lateinit var itemAdapter: ItemAdapter
    private lateinit var viewPager: ViewPager2
    private lateinit var viewPagerAdapter: ViewPagerAdapter
    private lateinit var searchView: SearchView
    private lateinit var horizontalScrollView: HorizontalScrollView
    private lateinit var HSV_linearLayout: LinearLayout
    private lateinit var textChoice1: TextView
    private lateinit var textChoice2: TextView
    private lateinit var textChoice3: TextView
    private lateinit var textChoice4: TextView
    private lateinit var textChoice5: TextView
    private lateinit var textChoice6: TextView
    private lateinit var swipeRefreshLayout: SwipeRefreshLayout
    private var handler = Handler(Looper.getMainLooper())
    private var handler2 = Handler(Looper.getMainLooper())
    private var autoScrollRunnable = object : Runnable {
        override fun run() {
            val nextItem = (viewPager.currentItem + 1) % (viewPager.adapter?.itemCount ?: 1)
            viewPager.setCurrentItem(nextItem, true)
            handler.postDelayed(this, 2000)
        }
    }
    private var downX: Float = 0f
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeBinding.inflate(inflater)
        recyclerView = binding.recyclerView
        viewPager = binding.viewPager
        searchView = binding.searchView
        horizontalScrollView = binding.horizontalScrollView
        HSV_linearLayout = binding.HSVLinearLayout
        textChoice1 = binding.textChoice1
        textChoice2 = binding.textChoice2
        textChoice3 = binding.textChoice3
        textChoice4 = binding.textChoice4
        textChoice5 = binding.textChoice5
        textChoice6 = binding.textChoice6
        swipeRefreshLayout = binding.swiperefreshlayout
        swipeRefreshLayout.setOnRefreshListener {
            reloadFragment()
        }
        textChoice1.textSize = 24f
        textChoice1.setTextColor(ContextCompat.getColor(requireContext(), R.color.n_green))
        recyclerView.layoutManager = GridLayoutManager(requireContext(), 3)
//        recyclerView.itemAnimator = CustomItemAnimator()
        itemAdapter = ItemAdapter(emptyList(), this)
        recyclerView.adapter = itemAdapter
        viewPagerAdapter = ViewPagerAdapter(emptyList(), this)
        viewPager.adapter = viewPagerAdapter
        return binding.root

    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val rootView = binding.root
        rootView.setOnTouchListener{ v, event ->
            if (event.action == MotionEvent.ACTION_DOWN) {
                searchView.clearFocus()
            }
            false
        }
        viewModel = ViewModelProvider(this).get(ItemViewModel::class.java)
        viewModel.itemResponse.observe(viewLifecycleOwner, Observer {
                apiResponse ->
            if (apiResponse.status) {
                itemAdapter = ItemAdapter(apiResponse.items, this)
                recyclerView.adapter = itemAdapter
                viewPagerAdapter = ViewPagerAdapter(apiResponse.items.take(5), this)
                viewPager.adapter = viewPagerAdapter
            }
            else {
                Log.d("Load HomePage data:", "Failed!")
            }
        })
        viewModel.searchResponse.observe(viewLifecycleOwner, Observer {
                searchResponse ->
            if(searchResponse.isNotEmpty()){
                itemAdapter = ItemAdapter(searchResponse, this )
                recyclerView.adapter = itemAdapter
            } else {
//                viewModel.getNewFilms(1, 20)
                Log.d("Load Search Data: ", "Failed!")
            }
        })
        viewModel.seriesResponse.observe(viewLifecycleOwner, Observer {
                seriesResponse ->
            if(seriesResponse.status) {
                itemAdapter = ItemAdapter(seriesResponse.items, this)
                recyclerView.adapter = itemAdapter
            } else {
                Log.d("Load PhimBo/Le data:", "Failed!")
            }
        })
        viewModel.categoriesResponse.observe(viewLifecycleOwner, Observer {
                categoriesResponse ->
            if(categoriesResponse.status) {
                itemAdapter = ItemAdapter(categoriesResponse.items, this)
                recyclerView.adapter = itemAdapter
            } else {
                Log.d("Load phim nhieu the loai:", "Failed!")
            }
        })
        viewModel.getNewFilms(1, 50)

        searchView.setOnQueryTextListener( object: SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                query?.let {
                    viewModel.searchItems(it)
                }
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                newText?.let {
                    if(it.isNotEmpty()) {
                        viewModel.searchItems(it)
                    } else {
                        viewModel.getNewFilms(1, 20)
                    }
                }
                return true
            }
        })


        textChoice1.setOnTouchListener {v, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    downX = event.x
                    Log.d("keke downX:", downX.toString())
                    animateBackgroundColor(textChoice1, ContextCompat.getColor(requireContext(), R.color.g_blue_gray200))
                    true
                }
                MotionEvent.ACTION_MOVE -> {
                    val deltaX = Math.abs(event.x - downX)
                    Log.d("keke deltaX;", deltaX.toString())
                    if(deltaX != 0f) {
                        false
                    } else true
                }
                MotionEvent.ACTION_CANCEL -> {
                    animateBackgroundColor(textChoice1, ContextCompat.getColor(requireContext(), R.color.dark_color))
                    true
                }
                MotionEvent.ACTION_UP -> {
                    Log.d("keke event.x:", event.x.toString())
                    textChoice1.textSize = 24f
                    textChoice1.setTextColor(ContextCompat.getColor(requireContext(), R.color.n_green))
                    for ( i in 0 until HSV_linearLayout.childCount) {
                        val child = HSV_linearLayout.getChildAt(i)
                        if (child is TextView && child.id != textChoice1.id) {
                            child.textSize = 18f
                            child.setTextColor(ContextCompat.getColor(requireContext(), R.color.white))
                        }
                    }
                    viewModel.getNewFilms(1, 20)
                    animateBackgroundColor(textChoice1, ContextCompat.getColor(requireContext(), R.color.dark_color))
                    true
                }
                else -> false
            }
        }
        textChoice2.setOnTouchListener {v, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    downX = event.x
                    Log.d("keke downX:", downX.toString())
                    animateBackgroundColor(textChoice2, ContextCompat.getColor(requireContext(), R.color.g_blue_gray200))
                    true
                }
                MotionEvent.ACTION_MOVE -> {
                    val deltaX = Math.abs(event.x - downX)
                    Log.d("keke downX:", downX.toString())
                    Log.d("keke eventx:", event.x.toString())
                    Log.d("keke deltaX;", deltaX.toString())
                    if (deltaX != 0f) {
                        false
                    } else true
                }
                MotionEvent.ACTION_CANCEL -> {
                    animateBackgroundColor(textChoice2, ContextCompat.getColor(requireContext(), R.color.dark_color))
                    true
                }
                MotionEvent.ACTION_UP -> {
                    Log.d("keke eventX;", event.x.toString())
                    textChoice2.textSize = 24f
                    textChoice2.setTextColor(ContextCompat.getColor(requireContext(), R.color.n_green))
                    for ( i in 0 until HSV_linearLayout.childCount) {
                        val child = HSV_linearLayout.getChildAt(i)
                        if (child is TextView && child.id != textChoice2.id) {
                            child.textSize = 18f
                            child.setTextColor(ContextCompat.getColor(requireContext(), R.color.white))
                        }
                    }
                    viewModel.getSeries("Series")
                    animateBackgroundColor(textChoice2, ContextCompat.getColor(requireContext(), R.color.dark_color))
                    true
                }
                else -> false
            }
        }
        textChoice3.setOnTouchListener {v, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    downX = event.x
                    animateBackgroundColor(textChoice3, ContextCompat.getColor(requireContext(), R.color.g_blue_gray200))
                    true
                }
                MotionEvent.ACTION_MOVE -> {
                    val deltaX = Math.abs(event.x - downX)
                    if (deltaX != 0f) {
                        false
                    } else true
                }
                MotionEvent.ACTION_CANCEL -> {
                    animateBackgroundColor(textChoice3, ContextCompat.getColor(requireContext(), R.color.dark_color))
                    true
                }
                MotionEvent.ACTION_UP -> {
                        textChoice3.textSize = 24f
                        textChoice3.setTextColor(ContextCompat.getColor(requireContext(), R.color.n_green))
                        for ( i in 0 until HSV_linearLayout.childCount) {
                            val child = HSV_linearLayout.getChildAt(i)
                            if (child is TextView && child.id != textChoice3.id) {
                                child.textSize = 18f
                                child.setTextColor(ContextCompat.getColor(requireContext(), R.color.white))
                            }
                        }
                        viewModel.getSeries("Movie")
                        animateBackgroundColor(textChoice3, ContextCompat.getColor(requireContext(), R.color.dark_color))

                    true
                }
                else -> false
            }
        }
        textChoice4.setOnTouchListener {v, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    downX = event.x
                    animateBackgroundColor(textChoice4, ContextCompat.getColor(requireContext(), R.color.g_blue_gray200))
                    true
                }
                MotionEvent.ACTION_MOVE -> {
                    val deltaX = Math.abs(event.x - downX)
                    if (deltaX != 0f) {
                        false
                    } else true
                }
                MotionEvent.ACTION_CANCEL -> {
                    animateBackgroundColor(textChoice4, ContextCompat.getColor(requireContext(), R.color.dark_color))
                    true
                }
                MotionEvent.ACTION_UP -> {
                    textChoice4.textSize = 24f
                    textChoice4.setTextColor(ContextCompat.getColor(requireContext(), R.color.n_green))
                    for ( i in 0 until HSV_linearLayout.childCount) {
                        val child = HSV_linearLayout.getChildAt(i)
                        if (child is TextView && child.id != textChoice4.id) {
                            child.textSize = 18f
                            child.setTextColor(ContextCompat.getColor(requireContext(), R.color.white))
                        }
                    }
                    viewModel.getCategories("phiêu lưu")
                    animateBackgroundColor(textChoice4, ContextCompat.getColor(requireContext(), R.color.dark_color))
                    true
                }
                else -> false
            }
        }
        textChoice5.setOnTouchListener {v, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    downX = event.x
                    animateBackgroundColor(textChoice5, ContextCompat.getColor(requireContext(), R.color.g_blue_gray200))
                    true
                }
                MotionEvent.ACTION_MOVE -> {
                    val deltaX = Math.abs(event.x - downX)
                    if (deltaX != 0f) {
                        false
                    } else true
                }
                MotionEvent.ACTION_CANCEL -> {
                    animateBackgroundColor(textChoice5, ContextCompat.getColor(requireContext(), R.color.dark_color))
                    true
                }
                MotionEvent.ACTION_UP -> {
                    textChoice5.textSize = 24f
                    textChoice5.setTextColor(ContextCompat.getColor(requireContext(), R.color.n_green))
                    for ( i in 0 until HSV_linearLayout.childCount) {
                        val child = HSV_linearLayout.getChildAt(i)
                        if (child is TextView && child.id != textChoice5.id) {
                            child.textSize = 18f
                            child.setTextColor(ContextCompat.getColor(requireContext(), R.color.white))
                        }
                    }
                    viewModel.getCategories("kinh dị")
                    animateBackgroundColor(textChoice5, ContextCompat.getColor(requireContext(), R.color.dark_color))
                    true
                }
                else -> false
            }
        }
        textChoice6.setOnTouchListener {v, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    downX = event.x
                    animateBackgroundColor(textChoice6, ContextCompat.getColor(requireContext(), R.color.g_blue_gray200))
                    true
                }
                MotionEvent.ACTION_MOVE -> {
                    val deltaX = Math.abs(event.x - downX)
                    if (deltaX != 0f) {

                        false
                    } else true
                }
                MotionEvent.ACTION_CANCEL -> {
                    animateBackgroundColor(textChoice6, ContextCompat.getColor(requireContext(), R.color.dark_color))
                    true
                }
                MotionEvent.ACTION_UP -> {
                    textChoice6.textSize = 24f
                    textChoice6.setTextColor(ContextCompat.getColor(requireContext(), R.color.n_green))
                    for ( i in 0 until HSV_linearLayout.childCount) {
                        val child = HSV_linearLayout.getChildAt(i)
                        if (child is TextView && child.id != textChoice6.id) {
                            child.textSize = 18f
                            child.setTextColor(ContextCompat.getColor(requireContext(), R.color.white))
                        }
                    }
                    viewModel.getCategories("viễn tưởng")
                    animateBackgroundColor(textChoice6, ContextCompat.getColor(requireContext(), R.color.dark_color))
                    true
                }
                else -> false
            }
        }

    }

    override fun onResume() {
        super.onResume()
        handler.postDelayed(autoScrollRunnable, 3000)
    }

    override fun onPause() {
        super.onPause()
        handler.removeCallbacks(autoScrollRunnable)
    }

    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacks(autoScrollRunnable)
    }
    private fun animateBackgroundColor(view: View, color: Int) {
        val colorFrom = (view.background as? ColorDrawable)?.color ?: ContextCompat.getColor(requireContext(), R.color.dark_color)
        val colorTo = color

        val colorAnimation = ValueAnimator.ofObject(ArgbEvaluator(), colorFrom, colorTo)
        colorAnimation.duration = 100 // Thời gian chuyển đổi màu
        colorAnimation.addUpdateListener { animator ->
            view.setBackgroundColor(animator.animatedValue as Int)
        }
        colorAnimation.start()
    }
    private fun reloadFragment() {
        swipeRefreshLayout.isRefreshing = true
        handler2.postDelayed({
            viewModel.getNewFilms(1, 50)
            swipeRefreshLayout.isRefreshing = false  // Stop the refreshing spinner
        }, 1000)
    }

}
class CustomItemAnimator : DefaultItemAnimator() {
    override fun animateAdd(holder: RecyclerView.ViewHolder?): Boolean {
        val animation = ObjectAnimator.ofFloat(holder?.itemView, "translationY", 100f, 0f)
        animation.duration = 3000
        animation.start()
        return super.animateAdd(holder)
    }

    override fun animateRemove(holder: RecyclerView.ViewHolder?): Boolean {
        val animation = ObjectAnimator.ofFloat(holder?.itemView, "alpha", 1f, 0f)
        animation.duration = 3000
        animation.start()
        return super.animateRemove(holder)
    }

}
