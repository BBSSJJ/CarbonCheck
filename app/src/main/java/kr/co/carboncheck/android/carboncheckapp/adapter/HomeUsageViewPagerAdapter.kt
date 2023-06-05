package kr.co.carboncheck.android.carboncheckapp.adapter

import android.view.View
import android.view.ViewGroup
import androidx.viewpager.widget.PagerAdapter
import kr.co.carboncheck.android.carboncheckapp.R

class HomeUsageViewPagerAdapter : PagerAdapter() {
    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        var resId = 0
        when (position) {
            0 -> {
                resId = R.id.constraint_home_usage_recycler_view

            }
            1 -> {
                resId = R.id.constraint_home_recent_usage
            }
        }
        return container.findViewById(resId)
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        container.removeView(`object` as View?)
    }

    override fun getCount(): Int {
        return 2
    }

    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        return (view == `object`)
    }
}