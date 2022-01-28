package com.driver.eho.model


data class BookingHistoryListModel (val title: String,
                               val date: String,
                               var time: String,
                               var ammount: String,
                               var pickLocation: String,
                               val destinationLocation: String)
{
}