/**
 * Created by appcom interactive GmbH on 22.11.19.
 * Copyright © 2019 appcom interactive GmbH. All rights reserved.
 */
package eu.appcom.gradle

object Deps {

  private const val junit5 = "org.junit.jupiter:junit-jupiter-"

  const val junit5Api = "${junit5}api:${Versions.junit5}"
  const val junit5Params = "${junit5}params:${Versions.junit5}"
  const val junit5Engine = "${junit5}engine:${Versions.junit5}"
  const val junit5VintageEngine = "org.junit.jupiter:junit-jupiter-engine:${Versions.junit5}"
}