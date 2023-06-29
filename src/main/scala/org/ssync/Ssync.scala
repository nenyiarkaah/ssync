package org.ssync

import com.softwaremill.macwire.wire
import com.typesafe.scalalogging.LazyLogging
import org.ssync.info.BuildInfo.toJson
import org.ssync.configs.AppConfig._
import org.ssync.services.{ConfigConversions, IoCapabilities, SsyncItemProcessor}
object Ssync extends App with LazyLogging {

  logger.info(
    """
      |      ___           ___                       ___           ___
      |     /\__\         /\__\                     /\  \         /\__\
      |    /:/ _/_       /:/ _/_         ___        \:\  \       /:/  /
      |   /:/ /\  \     /:/ /\  \       /|  |        \:\  \     /:/  /
      |  /:/ /::\  \   /:/ /::\  \     |:|  |    _____\:\  \   /:/  /  ___
      | /:/_/:/\:\__\ /:/_/:/\:\__\    |:|  |   /::::::::\__\ /:/__/  /\__\
      | \:\/:/ /:/  / \:\/:/ /:/  /  __|:|__|   \:\~~\~~\/__/ \:\  \ /:/  /
      |  \::/ /:/  /   \::/ /:/  /  /::::\  \    \:\  \        \:\  /:/  /
      |   \/_/:/  /     \/_/:/  /   ~~~~\:\  \    \:\  \        \:\/:/  /
      |     /:/  /        /:/  /         \:\__\    \:\__\        \::/  /
      |     \/__/         \/__/           \/__/     \/__/         \/__/
      |
      |
      |""".stripMargin)
  logger.info(toJson)

  val (settings) =
    load.fold(e => sys.error(s"Failed to load configuration:\n${e.toList.mkString("\n")}"), identity)

  logger.info(s"Source 🥫:- ${settings.Source}")
  logger.info(s"Destination 🏛️:- ${settings.Archive}")
  logger.info(s"Extensions ፝:- ${settings.Extensions}")
  logger.info(s"Items 🧾:- ${settings.Items.mkString("\n")}")

  val ioCapabilities = wire[IoCapabilities]
  val configConversions = wire[ConfigConversions]
  val ssyncItemProcessor = wire[SsyncItemProcessor]

  val ssyncItems = configConversions.convertSettingItemsToSyncItems(settings)
  ssyncItems.map { i =>
    ssyncItemProcessor.processSsyncItem(i)
  }

}
