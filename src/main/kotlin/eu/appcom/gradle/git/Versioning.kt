package eu.appcom.gradle.git

import eu.appcom.gradle.utils.get
import eu.appcom.gradle.utils.getInteger

/**
 * Created by appcom interactive GmbH on 20.03.19.
 * Copyright © 2019 appcom interactive GmbH. All rights reserved.
 */

object Versioning {

    fun getCommitCount(): Int = "git rev-list --count HEAD".getInteger()

    fun getSha1(): String = "git rev-parse --short HEAD".get()

    fun getVersionCode(): Int = getCommitCount()

    fun getBranch(): String = "git rev-parse --abbrev-ref HEAD".get()

    fun getVersionName(): String {
        val branch = getBranch()
        if (branch == "master" || branch.contains("release/") || branch.contains("hotfix/")) {
            if (branch.contains("release/")) {
                val tag = branch.split("/")[0]
                return "$tag-rc_${getCommitCountOnBranch()}"
            }
            return getTag()
        } else {
            return getTag()
        }
    }

    private fun getTag(): String {
        val revList = "git rev-list --tags --max-count=1".get()
        return "git describe --tags $revList".get()
    }

    private fun getCommitCountOnBranch(): Int = "git rev-list --count develop..HEAD".getInteger()
}