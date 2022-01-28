package com.driver.eho.ui.user

import android.util.Patterns

class DriverSignUpUser(
    UserName:String,
    HospitalName:String,
    MobileNumber:String,
    Email: String,
    DriverName:String,
    DriverExperience: Int,
    LicenceNumber:Int,
    AmbulanceVehicleNumber:String,
    HospitalAddress:String,
    State:String,
    City:String,
    Country:String,
    Password:String,

    ) {

    internal var strUserName: String = UserName
    internal var strHospitalName: String = HospitalName
    internal var strMobileNumber: String = MobileNumber
    internal var strEmail: String = Email
    internal var strDriverName: String = DriverName
    internal var strDriverExperience: Int = DriverExperience
    internal var strLicenceNumber: Int = LicenceNumber
    internal var strAmbulanceVehicleNumber: String = AmbulanceVehicleNumber
    internal var strHospitalAddress: String = HospitalAddress
    internal var strState: String = State
    internal var strCity: String = City
    internal var strCountry: String = Country
    internal var strPassword: String = Password

    fun getUserName(): String{
        return strUserName
    }
    fun getHospitalName(): String{
        return strHospitalName
    }
    fun getMobileNumber(): String{
        return strMobileNumber
    }
    fun getEmail(): String{
        return strEmail
    }
    fun getDriverName(): String{
        return strDriverName
    }
    fun getDriverExperience(): Int{
        return strDriverExperience
    }
    fun getLicenceNumber(): Int{
        return strLicenceNumber
    }
    fun getAmbulanceVehicleNumber(): String{
        return strAmbulanceVehicleNumber
    }
    fun getHospitalAddress(): String{
        return strHospitalAddress
    }
    fun getState(): String{
        return strState
    }
    fun getCity(): String{
        return strCity
    }
    fun getCountry(): String{
        return strCountry
    }
    fun getPassword(): String{
        return strPassword
    }


    fun isValidEmail(): Boolean{
        return  Patterns.EMAIL_ADDRESS.matcher(getEmail()).matches()
    }
    fun isPasswordLengthGreaterThan5(): Boolean{
        return getPassword().length > 5
    }
}