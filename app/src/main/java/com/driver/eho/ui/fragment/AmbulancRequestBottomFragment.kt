package com.driver.eho.ui.fragment

import android.content.Context
import android.media.MediaPlayer
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentManager
import com.driver.eho.R
import com.driver.eho.databinding.FragmentAmbulancRequestBottomBinding
import com.driver.eho.ui.History.HistoryFragment
import com.google.android.material.bottomsheet.BottomSheetDialogFragment


class AmbulancRequestBottomFragment : BottomSheetDialogFragment() {

    private lateinit var binding: FragmentAmbulancRequestBottomBinding
    private lateinit var mediaPlayer: MediaPlayer
    private lateinit var listener: OnBottomListener
    private val handler = Handler(Looper.myLooper()!!)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_ambulanc_request_bottom, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentAmbulancRequestBottomBinding.bind(view)

        mediaPlayer = MediaPlayer.create(requireContext(), R.raw.siren)
        mediaPlayer.start()

        binding.btnAccept.setOnClickListener {
            performButtonAccept()
            mediaPlayer.stop()
            handler.removeCallbacksAndMessages(null)
        }

        binding.btnDropOff.setOnClickListener {
            val fM: FragmentManager = requireActivity().supportFragmentManager
            val fragment = HistoryFragment()
            fM.beginTransaction().replace(R.id.fragment_container_view_tag, fragment).commit()
//            startActivity(Intent(activity, BookingHistoryActivity::class.java))
            mediaPlayer.stop()
            dismiss()
            listener.onClosed()
        }

        binding.btnDecline.setOnClickListener {
            dismiss()
            mediaPlayer.stop()
            listener.onClosed()
        }

        binding.btnCancel.setOnClickListener {
            dismiss()
            mediaPlayer.stop()
            listener.onClosed()
        }

    }

    // click on Accept button onclick listener fun
    private fun performButtonAccept() {
        binding.llDrop.visibility = View.VISIBLE
        binding.call.visibility = View.VISIBLE
        binding.btnDropOff.visibility = View.VISIBLE
        binding.btnAccept.visibility = View.GONE
        binding.btnDecline.visibility = View.GONE
    }


    override fun onResume() {
        super.onResume()
        handler.postDelayed({
            dismiss()
            mediaPlayer.stop()
            listener.onClosed()
        }, 10000)
    }

    interface OnBottomListener {
        fun onClosed()
    }

    override fun onPause() {
        super.onPause()
        mediaPlayer.stop()
        listener.onClosed()
    }

    override fun onDestroy() {
        super.onDestroy()
        mediaPlayer.stop()
        listener.onClosed()
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        try {
            listener = targetFragment as OnBottomListener
        } catch (e: ClassCastException) {
            throw ClassCastException("$context must implement OnBottomListener")
        }
    }

}