package saveorder.template

import data.Customer
import data.Order
import java.time.LocalDate
import java.time.temporal.ChronoUnit

class EmailUtils {

    companion object {
        val subject = "Dunder Mifflin Paper Company receipt"
    }

    fun getEmailTemplate(customer: Customer, order: Order): String {
        val arrivingDate = LocalDate.now().plus(1, ChronoUnit.WEEKS)
        return "<h1> Hello ${customer.name}! </h1><br>" +
                "<p>Your purchase at Dunder Mifflin will arrive at ${arrivingDate}</p><br>" +
                "<p>The total value of your purchase is: ${order.value}</p>"
    }

}