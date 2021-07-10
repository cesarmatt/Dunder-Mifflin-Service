package saveorder

import com.amazonaws.regions.Regions
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper
import com.amazonaws.services.lambda.runtime.Context
import com.amazonaws.services.lambda.runtime.RequestHandler
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent
import com.amazonaws.services.simpleemail.AmazonSimpleEmailServiceClientBuilder
import com.amazonaws.services.simpleemail.model.*
import com.fasterxml.jackson.databind.ObjectMapper
import data.Customer
import data.Order
import saveorder.template.EmailUtils
import java.io.IOException
import java.time.LocalDate
import java.time.temporal.ChronoUnit
import java.util.*

class App : RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {

    companion object {
        const val UTF8 = "UTF-8"
    }

    private var dynamoDB: AmazonDynamoDB = AmazonDynamoDBClientBuilder
            .standard()
            .withRegion("sa-east-1")
            .build()

    private var sesClient = AmazonSimpleEmailServiceClientBuilder
            .standard()
            .withRegion(Regions.SA_EAST_1)
            .build()

    private var mapper: DynamoDBMapper = DynamoDBMapper(dynamoDB)
    var objectMapper: ObjectMapper = ObjectMapper()

    override fun handleRequest(input: APIGatewayProxyRequestEvent, context: Context): APIGatewayProxyResponseEvent {
        val order = objectMapper.readValue(input.body, Order::class.java)
        order.orderId = UUID.randomUUID().toString()
        order.purchaseDate = LocalDate.now().toString()
        order.deliveryDate = LocalDate.now().plus(1, ChronoUnit.WEEKS).toString()
        val customerId = order.customerId

        val response = APIGatewayProxyResponseEvent()

        if (!customerId.isNullOrEmpty()) {
            val customer = getCustomer(customerId)
            if (customer != null) {
                mapper.save(order)
                sendEmail(customer, order)
            }
        } else {
            response.withStatusCode(500)
                    .withBody("You created a order without a proper customer")
        }

        return try {
            response.withStatusCode(200)
                    .withBody("Your order was saved successfuly and the client was already notified!")
        } catch (exception: IOException) {
            response.withStatusCode(500)
                    .withBody("Something went wrong trying to send your email, please contact the support")
        }
    }

    private fun sendEmail(customer: Customer, order: Order) {
        if (customer.email != null) {
            val template = EmailUtils().getEmailTemplate(customer, order)
            val subject = EmailUtils.subject
            val emailRequest = SendEmailRequest()
                    .withDestination(Destination().withToAddresses(customer.email))
                    .withMessage(
                            Message()
                                    .withSubject(Content().withCharset(UTF8).withData(subject))
                                    .withBody(Body().withHtml(Content()
                                            .withCharset(UTF8)
                                            .withData(template)))
                    )
                    .withSource("matheusilvacesar@gmail.com")

            sesClient.sendEmail(emailRequest)

            println("Email send with success!")
        }
    }

    private fun getCustomer(customerId: String): Customer? {
        return mapper.load(Customer::class.java, customerId)
    }

}
