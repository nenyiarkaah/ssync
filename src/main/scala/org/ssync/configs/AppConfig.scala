package org.ssync.configs

import com.typesafe.config.{ConfigFactory, ConfigParseOptions, ConfigRenderOptions}
import com.typesafe.scalalogging.StrictLogging
import pureconfig.error.ConfigReaderFailures
import pureconfig.generic.ProductHint
import pureconfig.generic.auto._
import pureconfig.{CamelCase, ConfigFieldMapping, loadConfig}

import java.io.File

object AppConfig extends StrictLogging {
  private val parseOptions = ConfigParseOptions.defaults().setAllowMissing(false)
  private val renderOptions = ConfigRenderOptions.defaults().setOriginComments(false)

  private val path = sys.env.getOrElse("APP_CONFIG_PATH", "src/main/resources/application.conf")

  implicit def hint[T]: ProductHint[T] = ProductHint[T](ConfigFieldMapping(CamelCase, CamelCase))

  def load: Either[ConfigReaderFailures,(Settings, List[Item])] = {
    val config = ConfigFactory.parseFile(new File(path), parseOptions).resolve()
    logger.debug("config content:\n {}", config.root().render(renderOptions))
    for {
      settings <- loadConfig[Settings](config, "settings")
      items <- loadConfig[List[Item]](config, "items")
    } yield (settings, items)
  }

}
