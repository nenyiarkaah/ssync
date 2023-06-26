package org.ssync.configs

case class Settings(
                     Source: String,
                     Archive: String,
                     Extensions: Seq[String],
                     IgnoredExtensions: Seq[String]
                 )
