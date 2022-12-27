package edu.bluejack22_1.Jejewegs.Fragment

import android.content.Context
import android.icu.text.CaseMap
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import androidx.viewbinding.ViewBindings
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import edu.bluejack22_1.Jejewegs.Adapter.ProfileSettingAdapter
import edu.bluejack22_1.Jejewegs.R
import edu.bluejack22_1.Jejewegs.databinding.FragmentCreateReviewBinding
import edu.bluejack22_1.Jejewegs.databinding.FragmentProfileSettingBinding


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [ProfileSettingFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class ProfileSettingFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private var _binding : FragmentProfileSettingBinding?=null
    private val binding get() = _binding!!
//    private lateinit var tabTitle: ArrayList<String>;
//        getActivity()?.let { getActivity()?.let { it1 -> arrayOf(it1.getString(R.string.profile_tab), it.getString(R.string.setting_tab)) } }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }

//        tabTitle = context?.let { arrayOf(it.getString(R.string.setting_tab), it.getString(R.string.profile_tab)) }

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentProfileSettingBinding.inflate(inflater, container, false)
//        var pager = binding.findViewById<ViewPager2>(R.id.viewPager2)
        binding.viewPager2.adapter = ProfileSettingAdapter(parentFragmentManager, lifecycle)

        TabLayoutMediator(binding.tabLayout, binding.viewPager2){
            tab, position ->
                tab.text = when(position) {
                    0 -> getString(R.string.profile_tab)
                    else -> getString(R.string.setting_tab)
                }
        }.attach()
//        var tl = view?.findViewById<TabLayout>(R.id.tabLayout)
//        if (pager != null) {
//            pager.adapter = ProfileSettingAdapter(parentFragmentManager, lifecycle)
//        }
//        if (tl != null) {
//            if (pager != null) {
//                TabLayoutMediator(tl,pager){
//                    tab, position ->
//                        tab.text = tabTitle[position]
//                }.attach()
//            }
//        }

        return binding.root
    }



    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment ProfileSettingFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            ProfileSettingFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}