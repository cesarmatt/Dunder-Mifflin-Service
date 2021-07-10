package data

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAttribute
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable

@DynamoDBTable(tableName = "customer")
data class Customer(
        @DynamoDBHashKey(attributeName = "customerId")
        var customerId: String = "",
        @DynamoDBAttribute(attributeName = "name")
        var name: String? = null,
        @DynamoDBAttribute(attributeName = "address")
        var address: String? = null,
        @DynamoDBAttribute(attributeName = "email")
        var email: String? = null,
)