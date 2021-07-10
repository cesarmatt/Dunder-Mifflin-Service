package data

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAttribute
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable

@DynamoDBTable(tableName = "orderitem")
data class OrderItem(
        @DynamoDBHashKey(attributeName = "orderItemId")
        var orderItemId: String? = "",
        @DynamoDBAttribute(attributeName = "name")
        var name: String? = null,
        @DynamoDBAttribute(attributeName = "value")
        var value: Float? = null
)
