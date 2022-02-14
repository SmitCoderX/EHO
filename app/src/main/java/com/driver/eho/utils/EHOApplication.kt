package com.driver.eho.utils

import android.app.Application
import com.driver.eho.repository.EHORepository

class EHOApplication : Application() {

    val repository by lazy { EHORepository() }

}