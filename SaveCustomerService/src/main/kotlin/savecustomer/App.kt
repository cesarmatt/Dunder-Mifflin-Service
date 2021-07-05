package savecustomer;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.fasterxml.jackson.databind.ObjectMapper
import data.Customer
import java.io.IOException
import java.util.*

class App : RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {

    private var dynamoDB: AmazonDynamoDB = AmazonDynamoDBClientBuilder
            .standard()
            .withRegion("sa-east-1")
            .build()
    private var mapper: DynamoDBMapper = DynamoDBMapper(dynamoDB)
    var objectMapper: ObjectMapper = ObjectMapper()

    override fun handleRequest(input: APIGatewayProxyRequestEvent, context: Context): APIGatewayProxyResponseEvent {
        val customer = objectMapper.readValue(input.body, Customer::class.java)
        customer.customerId = UUID.randomUUID().toString()
        mapper.save(customer)

        val response = APIGatewayProxyResponseEvent()

        return try {
            response.withStatusCode(200)
        } catch (exception: IOException) {
            response.withStatusCode(500)
        }
    }
}
