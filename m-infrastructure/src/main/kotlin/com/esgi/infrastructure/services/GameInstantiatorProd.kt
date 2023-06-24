package com.esgi.infrastructure.services

import com.amazonaws.auth.DefaultAWSCredentialsProviderChain
import com.amazonaws.regions.Regions
import com.amazonaws.services.ec2.AmazonEC2ClientBuilder
import com.amazonaws.services.ec2.model.DescribeSecurityGroupsRequest
import com.amazonaws.services.ecs.model.*
import com.esgi.applicationservices.services.GameInstantiator
import org.springframework.context.annotation.Primary
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Service

@Profile("prod")
@Service
@Primary
class GameInstantiatorProd(
    private val taskService: TaskService
): GameInstantiator {
    private fun getGameSecurityGroup(): String {
        val name = "game-security-group"

        val ec2Client = AmazonEC2ClientBuilder.standard()
            .withCredentials(DefaultAWSCredentialsProviderChain.getInstance())
            .withRegion(Regions.EU_WEST_3)
            .build()

        val describeSecurityGroupsRequest = DescribeSecurityGroupsRequest()
            .withGroupNames(name)

        val describeSecurityGroupsResult = ec2Client.describeSecurityGroups(describeSecurityGroupsRequest)

        if (describeSecurityGroupsResult.securityGroups.isNotEmpty()) {
            return describeSecurityGroupsResult.securityGroups[0].groupId
        }

        throw Exception("GameSecurityGroup not found")
    }

    override fun instanciateGame(gameId: String) {
        val networkConfiguration = taskService.getNetworkConfigFromService()

        val subnetId = networkConfiguration.awsvpcConfiguration.subnets[0]
        val securityGroupId = getGameSecurityGroup()

        val taskDefinition = taskService.createTaskDefinitionFromAnother(
            "fpr-game-task-${gameId}",
            "fpr-game-task",
            "fpr-game-default-task",
            "075626265631.dkr.ecr.eu-west-3.amazonaws.com/fpr-games-repository:${gameId}"
        )

        val task = taskService.runTask(taskDefinition, subnetId, securityGroupId)

        val containerIpAddress = taskService.waitForTaskToBeRunning(task.taskArn)

        println("Container IP address: $containerIpAddress")
    }
}