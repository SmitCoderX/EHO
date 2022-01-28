package com.driver.eho.ui.activity

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.driver.eho.adapter.BookingHistoryListAdapter
import com.driver.eho.databinding.ActivityBookingHistoryBinding
import com.driver.eho.model.BookingHistoryListModel

class BookingHistoryActivity : AppCompatActivity() {
    private lateinit var binding: ActivityBookingHistoryBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBookingHistoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.ivBack.setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }
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
            LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)

        binding.rvBookingHy.adapter = BookingHistoryListAdapter(bookingHistoryList)
    }

    override fun onBackPressed() {
        super.onBackPressed()
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }
}