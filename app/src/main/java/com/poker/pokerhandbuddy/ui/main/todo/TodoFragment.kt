package com.poker.pokerhandbuddy.ui.main.todo

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import com.poker.pokerhandbuddy.R

class TodoFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_todo, container, false)

        val emailButton = view.findViewById<Button>(R.id.emailButton)
        emailButton.setOnClickListener {
            sendEmail()
        }

        return view
    }

    private fun sendEmail() {
        val intent = Intent(
            Intent.ACTION_SENDTO, Uri.fromParts(
            "mailto", "trentrobison@gmail.com", null)
        )
        intent.putExtra(Intent.EXTRA_SUBJECT, "Wild Idea")
        intent.putExtra(Intent.EXTRA_TEXT, "Hi Trent, I have a fun feature idea.")
        startActivity(Intent.createChooser(intent, "Choose an Email client :"))
    }


    companion object {

        @JvmStatic
        fun newInstance() = TodoFragment()
    }
}