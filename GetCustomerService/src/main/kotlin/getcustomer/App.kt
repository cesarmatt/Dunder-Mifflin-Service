package getcustomer

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBScanExpression
import com.amazonaws.services.lambda.runtime.Context
import com.amazonaws.services.lambda.runtime.RequestHandler
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent
import com.fasterxml.jackson.databind.ObjectMapper
import data.Customer
import java.io.IOException

class App : RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {

    private var dynamoDB: AmazonDynamoDB = AmazonDynamoDBClientBuilder
            .standard()
            .withRegion("sa-east-1")
            .build()
    private var mapper: DynamoDBMapper = DynamoDBMapper(dynamoDB)
    var objectMapper: ObjectMapper = ObjectMapper()

    override fun handleRequest(input: APIGatewayProxyRequestEvent, context: Context): APIGatewayProxyResponseEvent {
        val customerId = input.queryStringParameters?.let {
            if (it.isNullOrEmpty()) {
                ""
            } else {
                it["customerId"]
            }
        }
        val response = APIGatewayProxyResponseEvent()

        if (customerId.isNullOrEmpty()) {
            response.withBody(getAllCustomers())
        } else {
            response.withBody(getCustomerById(customerId))
        }

        return try {
            response.withStatusCode(200)
        } catch (exception: IOException) {
            response.withStatusCode(500)
        }
    }

    private fun getAllCustomers(): String? {
        val scanExpression = DynamoDBScanExpression()
        val customers: List<Customer> = mapper.scan(Customer::class.java, scanExpression)
        return objectMapper.writeValueAsString(customers)
    }

    private fun getCustomerById(customerId: String): String? {
        val customer = mapper.load(Customer::class.java, customerId)
        return objectMapper.writeValueAsString(customer)
    }
}
