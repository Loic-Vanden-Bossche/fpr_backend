package com.esgi.infrastructure.services

import com.amazonaws.auth.DefaultAWSCredentialsProviderChain
import com.amazonaws.regions.Regions
import com.amazonaws.services.ecs.AmazonECS
import com.amazonaws.services.ecs.AmazonECSClientBuilder
import com.amazonaws.services.ecs.model.*
import org.springframework.stereotype.Service

@Service
class TaskService {
    private final val _ecsClient: AmazonECS = AmazonECSClientBuilder.standard()
        .withCredentials(DefaultAWSCredentialsProviderChain.getInstance())
        .withRegion(Regions.EU_WEST_3)
        .build()

    private val _clusterName = "fpr-backend-cluster"
    private val _serviceName = "fpr-backend-service"

    fun getNetworkConfigFromService(): NetworkConfiguration {
        val describeServicesRequest = DescribeServicesRequest()
            .withCluster(_clusterName)
            .withServices(_serviceName)
        val describeServicesResult = _ecsClient.describeServices(describeServicesRequest)

        return describeServicesResult.services[0].networkConfiguration
    }

    fun waitForTaskToBeRunning(taskArn: String): String {
        val timeout = 60 * 1000

        val startTime = System.currentTimeMillis()

        while (System.currentTimeMillis() - startTime < timeout) {
            val describeTasksRequest = DescribeTasksRequest()
                .withCluster(_clusterName)
                .withTasks(taskArn)
            val describeTasksResult = _ecsClient.describeTasks(describeTasksRequest)
            val task = describeTasksResult.tasks[0]
            if (task.lastStatus == "RUNNING") {
                return task.containers[0].networkInterfaces[0].privateIpv4Address
            }
            Thread.sleep(1000)
        }

        throw Exception("Timeout while waiting for task to be running")
    }

    fun waitForTaskCompletion(taskArn: String) {
        var taskState = ""
        while (taskState != "STOPPED") {
            val describeTasksRequest = DescribeTasksRequest()
                .withCluster(_clusterName)
                .withTasks(taskArn)


            val describeTasksResponse: DescribeTasksResult = _ecsClient.describeTasks(describeTasksRequest)
            taskState = describeTasksResponse.tasks[0].lastStatus.toString()

            Thread.sleep(2000)
        }
    }

    fun createTaskDefinitionFromAnother(
        taskName: String,
        baseTaskName: String,
        containerName: String,
        containerImage: String,
        containerEnv: List<KeyValuePair>? = null
    ): TaskDefinition {
        val describeTaskDefinitionRequest = DescribeTaskDefinitionRequest()
            .withTaskDefinition(baseTaskName)

        val describeTaskDefinitionResult = _ecsClient.describeTaskDefinition(describeTaskDefinitionRequest)
        val taskDefinition = describeTaskDefinitionResult.taskDefinition

        val containerDefinition = taskDefinition.containerDefinitions.find { it.name == containerName }
        if (containerDefinition != null) {
            // Update the container image
            containerDefinition.image = containerImage

            if (containerEnv != null) {
                containerDefinition.setEnvironment(containerEnv)
            }

            // Register the updated task definition
            val registerTaskDefinitionRequest = RegisterTaskDefinitionRequest()
                .withFamily(baseTaskName)
                .withContainerDefinitions(containerDefinition)
                .withCpu(taskDefinition.cpu)
                .withMemory(taskDefinition.memory)
                .withRequiresCompatibilities(taskDefinition.requiresCompatibilities)
                .withExecutionRoleArn(taskDefinition.executionRoleArn)
                .withTaskRoleArn(taskDefinition.taskRoleArn)
                .withNetworkMode(taskDefinition.networkMode)
                .withVolumes(taskDefinition.volumes)

            val registerTaskDefinitionResult = _ecsClient.registerTaskDefinition(registerTaskDefinitionRequest)

            return registerTaskDefinitionResult.taskDefinition
        } else {
            throw Exception("Container definition not found")
        }
    }

    fun runTask(taskDefinition: TaskDefinition, subnetId: String, securityGroupId: String): Task {
        val runTaskRequest = RunTaskRequest()
            .withCluster(_clusterName)
            .withTaskDefinition(taskDefinition.taskDefinitionArn)
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

        val runTaskResult = _ecsClient.runTask(runTaskRequest)

        return runTaskResult.tasks[0]
    }

    fun getTaskDetails(taskArn: String): DescribeTasksResult {
        val describeTasksRequest = DescribeTasksRequest()
            .withCluster(_clusterName)
            .withTasks(taskArn)
            .withInclude(TaskField.TAGS)

        return _ecsClient.describeTasks(describeTasksRequest)
    }
}