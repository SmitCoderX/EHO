package com.driver.eho.ui.viewModel.viewModelFactory

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.driver.eho.repository.EHORepository
import com.driver.eho.ui.viewModels.DriverSignInViewModel

class DriverSignInViewModelProviderFactory(
    val app: Application,
    val ehoRepository: EHORepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return DriverSignInViewModel(app, ehoRepository) as T
    }
}