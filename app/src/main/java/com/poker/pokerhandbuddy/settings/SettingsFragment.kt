package com.poker.pokerhandbuddy.settings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import com.poker.pokerhandbuddy.R


class SettingsFragment : PreferenceFragmentCompat() {

    companion object {
        val NAME = SettingsFragment::class.java.simpleName
    }

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.root_preferences, rootKey)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val cardBack: Preference? = findPreference(SettingsUtils.Keys.CHOOSE_CARDBACK)
        cardBack?.onPreferenceClickListener = Preference.OnPreferenceClickListener {
            SettingsUtils.showChangeCardBackDialog(requireContext())
            Toast.makeText(requireContext(),"Cardback changed", Toast.LENGTH_LONG).show()
            true
        }

        return super.onCreateView(inflater, container, savedInstanceState)
    }
}