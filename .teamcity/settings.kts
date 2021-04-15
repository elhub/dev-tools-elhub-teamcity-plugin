import jetbrains.buildServer.configs.kotlin.v2019_2.DslContext
import jetbrains.buildServer.configs.kotlin.v2019_2.project
import jetbrains.buildServer.configs.kotlin.v2019_2.sequential
import jetbrains.buildServer.configs.kotlin.v2019_2.triggers.VcsTrigger
import jetbrains.buildServer.configs.kotlin.v2019_2.triggers.vcs
import jetbrains.buildServer.configs.kotlin.v2019_2.version
import no.elhub.common.build.configuration.Assemble
import no.elhub.common.build.configuration.AutoRelease
import no.elhub.common.build.configuration.CodeReview
import no.elhub.common.build.configuration.ProjectType
import no.elhub.common.build.configuration.SonarScan
import no.elhub.common.build.configuration.UnitTest

version = "2020.2"

project {

    val projectId = "no.elhub.tools:dev-tools-elhub-teamcity-plugin"

    params {
        param("teamcity.ui.settings.readOnly", "true")
    }

    val buildChain = sequential {

        buildType(
            UnitTest(
                UnitTest.Config(
                    vcsRoot = DslContext.settingsRoot,
                    type = ProjectType.GRADLE
                )
            )
        )

        buildType(
            SonarScan(
                SonarScan.Config(
                    vcsRoot = DslContext.settingsRoot,
                    type = ProjectType.GRADLE,
                    sonarId = projectId,
                    sonarProjectSources = "elhub-teamcity-agent/src,elhub-teamcity-common/src,elhub-teamcity-server/src"
                )
            )
        )

        buildType(
            Assemble(
                Assemble.Config(
                    vcsRoot = DslContext.settingsRoot,
                    type = ProjectType.GRADLE
                )
            )
        )

        buildType(
            AutoRelease(
                AutoRelease.Config(
                    vcsRoot = DslContext.settingsRoot,
                    type = ProjectType.GRADLE
                )
            ) {
                triggers {
                    vcs {
                        branchFilter = "+:<default>"
                        quietPeriodMode = VcsTrigger.QuietPeriodMode.USE_DEFAULT
                    }
                }
            }
        )

    }

    buildChain.buildTypes().forEach { buildType(it) }

    buildType(
        CodeReview(
            CodeReview.Config(
                vcsRoot = DslContext.settingsRoot,
                type = ProjectType.GRADLE,
                sonarId = projectId,
                sonarProjectSources = "elhub-teamcity-agent/src,elhub-teamcity-common/src,elhub-teamcity-server/src"
            )
        )
    )

}
