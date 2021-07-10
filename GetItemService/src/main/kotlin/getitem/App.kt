package getitem

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBScanExpression
import com.amazonaws.services.lambda.runtime.Context
import com.amazonaws.services.lambda.runtime.RequestHandler
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent
import com.fasterxml.jackson.databind.ObjectMapper
import data.OrderItem
import java.io.IOException

class App : RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {

    private var dynamoDB: AmazonDynamoDB = AmazonDynamoDBClientBuilder
            .standard()
            .withRegion("sa-east-1")
            .build()
    private var mapper: DynamoDBMapper = DynamoDBMapper(dynamoDB)
    var objectMapper: ObjectMapper = ObjectMapper()

    override fun handleRequest(input: APIGatewayProxyRequestEvent, context: Context): APIGatewayProxyResponseEvent {
        val itemId = input.queryStringParameters?.let {
            if (it.isNullOrEmpty()) {
                ""
            } else {
                it["itemId"]
            }
        }

        val response = APIGatewayProxyResponseEvent()

        if (itemId.isNullOrEmpty()) {
            response.withBody(getAllItems())
        } else {
            response.withBody(getItemById(itemId))
        }

        return try {
            response.withStatusCode(200)
        } catch (exception: IOException) {
            response.withStatusCode(500)
        }
    }

    private fun getAllItems(): String? {
        val scanExpression = DynamoDBScanExpression()
        val items: List<OrderItem> = mapper.scan(OrderItem::class.java, scanExpression)
        return objectMapper.writeValueAsString(items)
    }

    private fun getItemById(itemId: String): String? {
        val item = mapper.load(OrderItem::class.java, itemId)
        return objectMapper.writeValueAsString(item)
    }
}