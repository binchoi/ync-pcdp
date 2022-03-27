import akka.actor.ActorSystem
import com.typesafe.config.ConfigFactory

/**
  * @author Ilya Sergey
  */
package object actors {

  // This actor system takes care of managing the concurrency 
  // underlying the actor interaction

  lazy val ourSystem = ActorSystem("OurExampleSystem")

  // Declaring a custom remoting system

  def remotingConfig(port: Int) = ConfigFactory.parseString(
    s"""
      akka {
        actor.provider = "akka.remote.RemoteActorRefProvider"
        remote {
          enabled-transports = ["akka.remote.netty.tcp"]
          netty.tcp {
            hostname = "127.0.0.1"
            port = $port
          }
        }
      }
  """)

  def remotingSystem(name: String, port: Int) = ActorSystem(name, remotingConfig(port))


}
