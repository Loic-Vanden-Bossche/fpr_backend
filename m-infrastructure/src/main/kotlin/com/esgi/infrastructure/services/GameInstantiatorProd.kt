package com.esgi.infrastructure.services

import com.amazonaws.auth.DefaultAWSCredentialsProviderChain
import com.amazonaws.regions.Regions
import com.amazonaws.services.ecs.AmazonECS
import com.amazonaws.services.ecs.AmazonECSClientBuilder
import com.esgi.applicationservices.services.GameInstantiator

class GameInstantiatorProd : GameInstantiator {
    override fun instanciateGame() {
        // Create an instance of the Amazon ECS client
        val ecsClient: AmazonECS = AmazonECSClientBuilder.standard()
            .withCredentials(DefaultAWSCredentialsProviderChain.getInstance())
            .withRegion(Regions.EU_WEST_3)
            .build()

        // print all the clusters in the account
        val clusters = ecsClient.listClusters()
        for (cluster in clusters.clusterArns) {
            println(cluster)
        }

        // Specify the ECS task details
//        val clusterName = "your-cluster-name"
//        val taskDefinition = "your-task-definition"
//        val containerName = "your-container-name"
//        val subnetId = "your-subnet-id"
//        val securityGroupId = "your-security-group-id"

        // Create a task request
//        val request = RunTaskRequest()
//            .withCluster(clusterName)
//            .withTaskDefinition(taskDefinition)
//            .withLaunchType("FARGATE")
//            .withNetworkConfiguration(
//                NetworkConfiguration()
//                .withAwsvpcConfiguration(
//                    AwsVpcConfiguration()
//                    .withSubnets(subnetId)
//                    .withSecurityGroups(securityGroupId)
//                )
//            )
//            .withOverrides(
//                TaskOverride().withContainerOverrides(
//                ContainerOverride()
//                    .withName(containerName)
//                // Add any additional container configuration if required
//            ))
//
//        // Run the task
//        val runTaskResult: RunTaskResult = ecsClient.runTask(request)
//
//        // Retrieve the IP address of the created container
//        val ipAddress: String = runTaskResult.tasks[0].containers[0].networkInterfaces[0].privateIpv4Address
//        println("Container IP address: $ipAddress")
    }
}