package com.flipperdevices.changelog.impl.api

import com.flipperdevices.changelog.api.ChangelogFormatterApi
import com.flipperdevices.core.di.AppGraph
import com.squareup.anvil.annotations.ContributesBinding
import dagger.Reusable
import javax.inject.Inject

private const val REGEX_GITHUB_PR = "(^| )(https://github.com/\\S+/pull/([0-9]+))"
private const val REGEX_GITHUB_COMPARE = "(^| )(https://github.com/\\S+/compare/(\\S+))"
private const val REGEX_GITHUB_NICKNAME = "(^| )@(\\S+)"

private const val NUMBER_MATCH_FIRST = 2
private const val NUMBER_MATCH_SECOND = 3

@Reusable
@ContributesBinding(AppGraph::class, ChangelogFormatterApi::class)
class ChangelogFormatterImpl @Inject constructor() : ChangelogFormatterApi {
    override fun format(changelog: String): String {
        return changelog
            .removeLineBreakChangelog()
            .replaceGithubPR()
            .replaceGithubCompare()
            .replaceGithubNickname()
    }

    private fun String.removeLineBreakChangelog(): String {
        return this
            .replace("\r\n\r\n", "\n\n")
            .replace("\r", "")
    }

    private fun String.replaceGithubPR(): String {
        return Regex(REGEX_GITHUB_PR).replace(this) { result ->
            val url = result.groupValues[NUMBER_MATCH_FIRST]
            val prNumber = result.groupValues[NUMBER_MATCH_SECOND]
            " [#$prNumber]($url) "
        }
    }

    private fun String.replaceGithubCompare(): String {
        return Regex(REGEX_GITHUB_COMPARE).replace(this) { result ->
            val url = result.groupValues[NUMBER_MATCH_FIRST]
            val tags = result.groupValues[NUMBER_MATCH_SECOND]
            " [$tags]($url) "
        }
    }

    private fun String.replaceGithubNickname(): String {
        return Regex(REGEX_GITHUB_NICKNAME).replace(this) { result ->
            val nickname = result.groupValues[NUMBER_MATCH_FIRST]
            " **@$nickname** "
        }
    }
}
