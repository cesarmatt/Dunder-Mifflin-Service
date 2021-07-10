package getorder

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBScanExpression
import com.amazonaws.services.lambda.runtime.Context
import com.amazonaws.services.lambda.runtime.RequestHandler
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent
import com.fasterxml.jackson.databind.ObjectMapper
import data.Order
import java.io.IOException

class App : RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {

    private var dynamoDB: AmazonDynamoDB = AmazonDynamoDBClientBuilder
            .standard()
            .withRegion("sa-east-1")
            .build()
    private var mapper: DynamoDBMapper = DynamoDBMapper(dynamoDB)
    var objectMapper: ObjectMapper = ObjectMapper()

    override fun handleRequest(input: APIGatewayProxyRequestEvent, context: Context): APIGatewayProxyResponseEvent {
        val orderId = input.queryStringParameters?.let {
            if (it.isNullOrEmpty()) {
                ""
            } else {
                it["orderId"]
            }
        }

        val response = APIGatewayProxyResponseEvent()

        if (orderId.isNullOrEmpty()) {
            response.withBody(getAllOrders())
        } else {
            response.withBody(getOrderById(orderId))
        }

        return try {
            response.withStatusCode(200)
        } catch (exception: IOException) {
            response.withStatusCode(500)
        }
    }

    private fun getAllOrders(): String? {
        val scanExpression = DynamoDBScanExpression()
        val orders: List<Order> = mapper.scan(Order::class.java, scanExpression)
        return objectMapper.writeValueAsString(orders)
    }

    private fun getOrderById(orderId: String): String? {
        val order = mapper.load(Order::class.java, orderId)
        return objectMapper.writeValueAsString(order)
    }
}