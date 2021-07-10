package data

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAttribute
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable

@DynamoDBTable(tableName = "salesman")
data class Salesman(
        @DynamoDBHashKey(attributeName = "salesmanId")
        var id: String? = null,
        @DynamoDBAttribute(attributeName = "name")
        var name: String? = null,
)