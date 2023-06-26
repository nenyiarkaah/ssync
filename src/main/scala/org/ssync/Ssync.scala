package org.ssync

import com.typesafe.scalalogging.LazyLogging
import org.ssync.info.BuildInfo.toJson
import org.ssync.configs.AppConfig._
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

  val (settings, items) =
    load.fold(e => sys.error(s"Failed to load configuration:\n${e.toList.mkString("\n")}"), identity)

  logger.info(s"Source 🥫:- ${settings.Source}")
  logger.info(s"Destination 🏛️:- ${settings.Archive}")
  logger.info(s"Extensions ፝:- ${settings.Extensions}")
  logger.info(s"Items 🧾:- ${items.mkString("\n")}")
}
