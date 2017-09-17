package org.hackzurich2017.draw2fashion

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.view.ViewGroup
import android.util.SparseArray
import android.support.v4.app.FragmentStatePagerAdapter


abstract class StateAdapter(fragmentManager: FragmentManager) : FragmentStatePagerAdapter(fragmentManager) {

    // Sparse array to keep track of registered fragments in memory
    private val mRegisteredFragments = SparseArray<Fragment>()

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val fragment = super.instantiateItem(container, position) as Fragment
        mRegisteredFragments.put(position, fragment)
        return fragment
    }

    override fun destroyItem(container: ViewGroup?, position: Int, `object`: Any) {
        mRegisteredFragments.remove(position)
        super.destroyItem(container, position, `object`)
    }

    /**
     * Returns the fragment for the position (if instantiated)
     */
    fun getRegisteredFragment(position: Int): Fragment {
        return mRegisteredFragments.get(position)
    }
}
