package com.driver.eho.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.driver.eho.adapter.BookingHistoryListAdapter
import com.driver.eho.databinding.FragmentHistoryBinding
import com.driver.eho.model.BookingHistoryListModel


class HistoryFragment : Fragment() {
    private lateinit var binding: FragmentHistoryBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentHistoryBinding.inflate(layoutInflater, container, false)

        // bookingHistory adapter
        val bookingHistoryList = arrayOf(
            BookingHistoryListModel(
                "Title Here", "10/02/2019", "03:30 pm", "100.32",
                "Ganesh sernam jagatpur road,ahmedabad, gujrat 382470, india",
                "D Block Asarwa, Haripura, Office of the Medical Superintendent Civil Hospital, Asarwa, Ahmedabad, Gujarat 380016"
            ),
            BookingHistoryListModel(
                "Title Here", "10/02/2019", "03:30 pm", "100.32",
                "Ganesh sernam jagatpur road,ahmedabad, gujrat 382470, india",
                "D Block Asarwa, Haripura, Office of the Medical Superintendent Civil Hospital, Asarwa, Ahmedabad, Gujarat 380016"
            ),
            BookingHistoryListModel(
                "Title Here", "10/02/2019", "03:30 pm", "100.32",
                "Ganesh sernam jagatpur road,ahmedabad, gujrat 382470, india",
                "D Block Asarwa, Haripura, Office of the Medical Superintendent Civil Hospital, Asarwa, Ahmedabad, Gujarat 380016"
            ),
            BookingHistoryListModel(
                "Title Here", "10/02/2019", "03:30 pm", "100.32",
                "Ganesh sernam jagatpur road,ahmedabad, gujrat 382470, india",
                "D Block Asarwa, Haripura, Office of the Medical Superintendent Civil Hospital, Asarwa, Ahmedabad, Gujarat 380016"
            )
        )

        binding.rvBookingHy.hasFixedSize()
        binding.rvBookingHy.layoutManager =
            LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false)
        binding.rvBookingHy.adapter = BookingHistoryListAdapter(bookingHistoryList)

        return binding.root
    }


}