package com.esgi.infrastructure.services

import com.amazonaws.auth.DefaultAWSCredentialsProviderChain
import com.amazonaws.regions.Regions
import com.amazonaws.services.ecs.AmazonECS
import com.amazonaws.services.ecs.AmazonECSClientBuilder
import com.amazonaws.services.ecs.model.*
import com.esgi.applicationservices.services.GameBuilder
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Primary
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Service

@Profile("prod")
@Service
@Primary
class BuildGameServiceProd: GameBuilder {
    @Value("\${games-bucket.name}")
    private val bucketName: String? = null

    override fun buildGame(gameId: String) {
        val ecsClient: AmazonECS = AmazonECSClientBuilder.standard()
            .withCredentials(DefaultAWSCredentialsProviderChain.getInstance())
            .withRegion(Regions.EU_WEST_3)
            .build()

        val clusterName = "fpr-backend-cluster"

        val describeServicesRequest = DescribeServicesRequest()
            .withCluster(clusterName)
            .withServices("fpr-backend-service")
        val describeServicesResult = ecsClient.describeServices(describeServicesRequest)

        val networkConfiguration = describeServicesResult.services[0].networkConfiguration

        val subnetId = networkConfiguration.awsvpcConfiguration.subnets[0]
        val securityGroupId = networkConfiguration.awsvpcConfiguration.securityGroups[0]

        val describeTaskDefinitionRequest = DescribeTaskDefinitionRequest()
            .withTaskDefinition("fpr-games-builder-task")

        val describeTaskDefinitionResult = ecsClient.describeTaskDefinition(describeTaskDefinitionRequest)
        val taskDefinition = describeTaskDefinitionResult.taskDefinition

        val containerName = "fpr-games-builder"

        val containerDefinition = taskDefinition.containerDefinitions.find { it.name == containerName }

        val containerImage = "075626265631.dkr.ecr.eu-west-3.amazonaws.com/fpr-executor-repository:builder-latest"

        if (containerDefinition != null) {
            containerDefinition.image = containerImage

            containerDefinition.setEnvironment(
                listOf(
                    KeyValuePair().withName("GAME_ID").withValue(gameId),
                    KeyValuePair().withName("S3_BUCKET").withValue(bucketName),
                    KeyValuePair().withName("ECR_GAMES_REPOSITORY").withValue("fpr-games-repository"),
                    KeyValuePair().withName("ECR_EXECUTOR_REPOSITORY").withValue("fpr-executor-repository"),
                    KeyValuePair().withName("AWS_ACCOUNT_ID").withValue("075626265631"),
                    KeyValuePair().withName("AWS_REGION").withValue("eu-west-3")
                )
            )

            val registerTaskDefinitionRequest = RegisterTaskDefinitionRequest()
                .withFamily("fpr-games-builder-task-test")
                .withContainerDefinitions(containerDefinition)
                .withCpu(taskDefinition.cpu)
                .withMemory(taskDefinition.memory)
                .withRequiresCompatibilities(taskDefinition.requiresCompatibilities)
                .withExecutionRoleArn(taskDefinition.executionRoleArn)
                .withTaskRoleArn(taskDefinition.taskRoleArn)
                .withNetworkMode(taskDefinition.networkMode)
                .withVolumes(taskDefinition.volumes)

            val registerTaskDefinitionResult = ecsClient.registerTaskDefinition(registerTaskDefinitionRequest)
            val updatedTaskDefinitionArn = registerTaskDefinitionResult.taskDefinition.taskDefinitionArn

            val request = RunTaskRequest()
                .withCluster(clusterName)
                .withTaskDefinition(updatedTaskDefinitionArn)
                .withLaunchType("FARGATE")
                .withNetworkConfiguration(
                    NetworkConfiguration()
                        .withAwsvpcConfiguration(
                            AwsVpcConfiguration()
                                .withSubnets(subnetId)
                                .withAssignPublicIp("ENABLED")
                                .withSecurityGroups(securityGroupId)
                        )
                )

            val runTaskResult: RunTaskResult = ecsClient.runTask(request)

            val taskArn = runTaskResult.tasks[0].taskArn

            waitForTaskCompletion(ecsClient, clusterName, taskArn)

            val taskDetails = getTaskDetails(ecsClient, clusterName, taskArn)

            val exitCode = taskDetails?.tasks?.get(0)?.containers?.get(0)?.exitCode

            if (exitCode != null && exitCode != 0) {
                println("Exit Code: $exitCode")
            }

            println(runTaskResult)
            println(exitCode)
        } else {
            throw Exception("Container definition not found")
        }
    }

    fun waitForTaskCompletion(ecsClient: AmazonECS, clusterName: String, taskArn: String) {
        var taskState = ""
        while (taskState != "STOPPED") {
            val describeTasksRequest = DescribeTasksRequest()
                .withCluster(clusterName)
                .withTasks(taskArn)


            val describeTasksResponse: DescribeTasksResult = ecsClient.describeTasks(describeTasksRequest)
            taskState = describeTasksResponse.tasks[0].lastStatus.toString()

            Thread.sleep(2000)
        }
    }

    fun getTaskDetails(ecsClient: AmazonECS, clusterName: String, taskArn: String): DescribeTasksResult? {
        val describeTasksRequest = DescribeTasksRequest()
            .withCluster(clusterName)
            .withTasks(taskArn)
            .withInclude(TaskField.TAGS)

        return ecsClient.describeTasks(describeTasksRequest)
    }
}