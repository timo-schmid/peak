package ch.timo_schmid.cmf.db

import com.comcast.ip4s.*

trait DatabaseConfig {

  def numThreads: Int
  def driver: String = "org.postgresql.Driver"
  def host: Host
  def port: Port
  def username: String
  def password: String
  def database: String

  lazy val jdbcUrl: String = s"jdbc:postgresql://$host:$port/$database"

}
