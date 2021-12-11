package com.myedu.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.viewpager.widget.PagerAdapter
import coil.load
import com.myedu.R

class OnBoardingAdapter() :
    PagerAdapter() {
    private val posters = intArrayOf(
        R.drawable.ic_splash,
        R.drawable.ic_splash,
        R.drawable.ic_splash
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
        val btnSkip = view.findViewById<Button>(R.id.txtSkip)
        val description = view.findViewById<TextView>(R.id.txtDescription)
        val btnGetStarted = view.findViewById<Button>(R.id.btnGetStarted)
        poster.load(posters[position])
        imgPosition.load(positions[position])
        description.text = descriptions[position]
        container.addView(view)
        btnSkip.visibility = if (position != count - 1) View.VISIBLE else View.INVISIBLE
        btnGetStarted.visibility = if (position == count - 1) View.VISIBLE else View.INVISIBLE
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
}