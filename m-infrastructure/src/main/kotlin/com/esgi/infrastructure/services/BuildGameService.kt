package com.esgi.infrastructure.services
import com.amazonaws.services.ecs.model.*
import com.esgi.applicationservices.services.GameBuilder
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service

@Service
class BuildGameService(
    private val _taskService: TaskService,
): GameBuilder {
    @Value("\${games-bucket.name}")
    private val bucketName: String? = null

    override fun buildGame(gameId: String) {
        val networkConfiguration = _taskService.getNetworkConfigFromService()

        val subnetId = networkConfiguration.awsvpcConfiguration.subnets[0]
        val securityGroupId = networkConfiguration.awsvpcConfiguration.securityGroups[0]

        val containerEnv = listOf(
            KeyValuePair().withName("GAME_ID").withValue(gameId),
            KeyValuePair().withName("S3_BUCKET").withValue(bucketName),
            KeyValuePair().withName("ECR_GAMES_REPOSITORY").withValue("fpr-games-repository"),
            KeyValuePair().withName("ECR_EXECUTOR_REPOSITORY").withValue("fpr-executor-repository"),
            KeyValuePair().withName("AWS_ACCOUNT_ID").withValue("075626265631"),
            KeyValuePair().withName("AWS_REGION").withValue("eu-west-3")
        )

        val taskDefinition = _taskService.createTaskDefinitionFromAnother(
            "fpr-games-builder-task:${gameId}",
            "fpr-games-builder-task",
            "fpr-games-builder",
            "075626265631.dkr.ecr.eu-west-3.amazonaws.com/fpr-executor-repository:builder-latest",
            containerEnv
        )

        val task = _taskService.runTask(taskDefinition, subnetId, securityGroupId)

        _taskService.waitForTaskCompletion(task.taskArn)

        val taskDetails = _taskService.getTaskDetails(task.taskArn)

        val exitCode = taskDetails.tasks?.get(0)?.containers?.get(0)?.exitCode

        if (exitCode != null && exitCode != 0) {
            println("Exit Code: $exitCode")
        } else {
            println("Game built successfully")
        }
    }
}