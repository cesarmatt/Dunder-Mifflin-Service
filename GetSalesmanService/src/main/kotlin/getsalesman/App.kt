package getsalesman

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBScanExpression
import com.amazonaws.services.lambda.runtime.Context
import com.amazonaws.services.lambda.runtime.RequestHandler
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent
import com.fasterxml.jackson.databind.ObjectMapper
import data.Salesman
import java.io.IOException

class App : RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {

    private var dynamoDB: AmazonDynamoDB = AmazonDynamoDBClientBuilder
            .standard()
            .withRegion("sa-east-1")
            .build()
    private var mapper: DynamoDBMapper = DynamoDBMapper(dynamoDB)
    var objectMapper: ObjectMapper = ObjectMapper()

    override fun handleRequest(input: APIGatewayProxyRequestEvent, context: Context): APIGatewayProxyResponseEvent {
        val salesmanId = input.queryStringParameters?.let {
            if (it.isNullOrEmpty()) {
                ""
            } else {
                it["salesmanId"]
            }
        }

        val response = APIGatewayProxyResponseEvent()

        if (salesmanId.isNullOrEmpty()) {
            response.withBody(getAllSalesman())
        } else {
            response.withBody(getSalesmanById(salesmanId))
        }

        return try {
            response.withStatusCode(200)
        } catch (exception: IOException) {
            response.withStatusCode(500)
        }
    }

    private fun getAllSalesman(): String? {
        val scanExpression = DynamoDBScanExpression()
        val salesman: List<Salesman> = mapper.scan(Salesman::class.java, scanExpression)
        return objectMapper.writeValueAsString(salesman)
    }

    private fun getSalesmanById(salesmanId: String): String? {
        val salesman = mapper.load(Salesman::class.java, salesmanId)
        return objectMapper.writeValueAsString(salesman)
    }
}