package com.myedu.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.navigation.Navigation
import androidx.viewpager.widget.PagerAdapter
import coil.load
import com.myedu.R
import com.myedu.utils.PrefManager

class OnBoardingAdapter() :
    PagerAdapter(), View.OnClickListener {

    private val posters = intArrayOf(
        R.drawable.ic_illustration_b_one,
        R.drawable.ic_illustration_b_two,
        R.drawable.ic_illustration_b_three
    )

    private val positions = intArrayOf(
        R.drawable.ic_circle_one,
        R.drawable.ic_circle_two,
        R.drawable.ic_circle_three
    )

    private val descriptions = arrayOf(
        "Page one",
        "Page two",
        "page three"
    )

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val view: View = LayoutInflater.from(container.context)
            .inflate(R.layout.item_on_boarding, container, false)
        val imgPosition = view.findViewById<ImageView>(R.id.imgNumber)
        val poster = view.findViewById<ImageView>(R.id.imgPoster)
        val btnSkip = view.findViewById<Button>(R.id.btnSkip)
        val description = view.findViewById<TextView>(R.id.txtDescription)
        val btnGetStarted = view.findViewById<Button>(R.id.btnGetStarted)
        poster.load(posters[position])
        imgPosition.load(positions[position])
        description.text = descriptions[position]
        container.addView(view)
        btnSkip.visibility = if (position != count - 1) View.VISIBLE else View.INVISIBLE
        btnGetStarted.visibility = if (position == count - 1) View.VISIBLE else View.INVISIBLE
        btnSkip.setOnClickListener(this)
        btnGetStarted.setOnClickListener(this)
        return view
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        container.removeView(`object` as RelativeLayout)
    }

    override fun getCount(): Int {
        return posters.size
    }

    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        return view === `object`
    }

    override fun onClick(p0: View?) {
        p0?.also {
            if (it.id == R.id.btnGetStarted || it.id == R.id.btnSkip) {
                PrefManager(p0.context).isFirsTimer = false
                Navigation
                    .createNavigateOnClickListener(R.id.action_onBoardingFragment_to_loginFragment)
                    .onClick(it)
            }
        }
    }
}