package com.poker.pokerhandbuddy.ui.main

import android.content.Context
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.poker.pokerhandbuddy.R
import com.poker.pokerhandbuddy.ui.main.deuceswild.DeucesWildFragment
import com.poker.pokerhandbuddy.ui.main.jacksorbetter.JacksOrBetterFragment
import com.poker.pokerhandbuddy.ui.main.texasholdem.TexasHoldemFragment
import com.poker.pokerhandbuddy.ui.main.todo.TodoFragment

private val TAB_TITLES = arrayOf(
        R.string.tab_texas_holdem,
        R.string.tab_jack_or_better,
        R.string.tab_deuces_wild,
        R.string.tab_todo,
)

/**
 * A [FragmentPagerAdapter] that returns a fragment corresponding to
 * one of the sections/tabs/pages.
 */
class SectionsPagerAdapter(private val context: Context, fm: FragmentManager)
    : FragmentPagerAdapter(fm) {

    override fun getItem(position: Int): Fragment {
        return when(position){
            0 -> TexasHoldemFragment.newInstance()
            1 -> JacksOrBetterFragment.newInstance()
            2 -> DeucesWildFragment.newInstance()
            3 -> TodoFragment.newInstance()
            else -> TexasHoldemFragment.newInstance()
        }
    }

    override fun getPageTitle(position: Int): CharSequence? {
        return context.resources.getString(TAB_TITLES[position])
    }

    override fun getCount(): Int {
        return TAB_TITLES.size
    }

}