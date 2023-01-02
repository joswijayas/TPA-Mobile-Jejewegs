package edu.bluejack22_1.Jejewegs.Adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import edu.bluejack22_1.Jejewegs.Fragment.ReviewCommentedFragment
import edu.bluejack22_1.Jejewegs.Fragment.ReviewLikedFragment


class ReviewLikedCommentedAdapter (fragmentManager: FragmentManager, lifecycle: Lifecycle) : FragmentStateAdapter(fragmentManager, lifecycle) {
    override fun getItemCount(): Int {
        return 2
    }

    override fun createFragment(position: Int): Fragment {
        when(position){
            0 -> return ReviewLikedFragment()
            1 -> return ReviewCommentedFragment()
            else -> return ReviewLikedFragment()
        }

    }

}