package com.driver.eho.ui.viewModel.viewModelFactory

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.driver.eho.repository.EHORepository
import com.driver.eho.ui.viewModels.BookingHistoryViewModel

class BookingHistoryViewModelProviderFactory(
    val app: Application,
    val ehoRepository: EHORepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return BookingHistoryViewModel(app, ehoRepository) as T
    }
}