package io.keepcoding.eh_ho

import org.junit.Test
import java.lang.Exception
import java.net.SocketImplFactory
import java.text.SimpleDateFormat
import java.util.*

class TopicModelTest {
    @Test
    fun getOffset_year_isCorrect() {
        val dateToCompare: Date = formatDate("01/01/2020 10:00:00")
    }

    private fun formatDate(date: String): Date {
        val formatter = SimpleDateFormat( "dd/MM/yyyy hh:mm:ss")
        var dateFormatted: Date?

        try {
            dateFormatted = formatter.parse(date)
        } catch (e:Exception){
            throw e
        }
        return dateFormatted
    }
}
