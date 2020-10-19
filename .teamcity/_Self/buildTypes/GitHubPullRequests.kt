package _Self.buildTypes

import _Self.vcsRoots.HttpsGithubComJetBrainsIdeavimPullRequests
import jetbrains.buildServer.configs.kotlin.v2019_2.*
import jetbrains.buildServer.configs.kotlin.v2019_2.buildFeatures.PullRequests
import jetbrains.buildServer.configs.kotlin.v2019_2.buildFeatures.commitStatusPublisher
import jetbrains.buildServer.configs.kotlin.v2019_2.buildFeatures.pullRequests
import jetbrains.buildServer.configs.kotlin.v2019_2.buildSteps.gradle
import jetbrains.buildServer.configs.kotlin.v2019_2.triggers.VcsTrigger
import jetbrains.buildServer.configs.kotlin.v2019_2.triggers.vcs

object GitHubPullRequests : BuildType({
    name = "GitHub Pull Requests"
    description = "Test GitHub pull requests"

    params {
        param("env.ORG_GRADLE_PROJECT_downloadIdeaSources", "false")
        param("env.ORG_GRADLE_PROJECT_ideaVersion", "2020.1")
        param("env.ORG_GRADLE_PROJECT_instrumentPluginCode", "false")
    }

    vcs {
        root(_Self.vcsRoots.HttpsGithubComJetBrainsIdeavimPullRequests)

        checkoutMode = CheckoutMode.ON_SERVER
        branchFilter = """
            +:*
            -:<default>
        """.trimIndent()
    }

    steps {
        gradle {
            tasks = "clean test"
            buildFile = ""
            enableStacktrace = true
            param("org.jfrog.artifactory.selectedDeployableServer.defaultModuleVersionConfiguration", "GLOBAL")
        }
    }

    triggers {
        vcs {
            quietPeriodMode = VcsTrigger.QuietPeriodMode.USE_DEFAULT
            branchFilter = ""
        }
    }

    features {
        pullRequests {
            provider = github {
                authType = token {
                    token = "credentialsJSON:43afd6e5-6ad5-4d12-a218-cf1547717a7f"
                }
                filterTargetBranch = "refs/heads/master"
                filterAuthorRole = PullRequests.GitHubRoleFilter.EVERYBODY
            }
        }
        commitStatusPublisher {
            vcsRootExtId = "${HttpsGithubComJetBrainsIdeavimPullRequests.id}"
            publisher = github {
                githubUrl = "https://api.github.com"
                authType = personalToken {
                    token = "credentialsJSON:43afd6e5-6ad5-4d12-a218-cf1547717a7f"
                }
            }
            param("github_oauth_user", "AlexPl292")
        }
    }

    requirements {
        noLessThanVer("teamcity.agent.jvm.version", "1.8")
    }
})