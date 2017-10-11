package actors

import java.util.UUID

import akka.actor.{Actor, ActorRef, PoisonPill, Props, Status}
import play.api.libs.json._

import models.User

object WsActor {
  def props(user: User)(out: ActorRef) = Props(new WsActor(user, out))
}

class WsActor(user: User, out: ActorRef) extends Actor {

  // Isolated mutable state
  var internalState = 0

  override def receive: Receive = {
    case msg: String => {
        // Parse message from client
        val json: JsValue = Json.parse(msg)
        val session = (json \ "session").get
        val totalDist = (json \ "totalDist").get
        val updDist = (json \ "updDist").get
        val totalTime = (json \ "totalTime").get
        val updTime = (json \ "updTime").get
        // Generate response for client
        val resMsg = Json.obj(
          "rate" -> internalState,
          "session" -> session,
          "totalDist" -> totalDist,
          "updDist" -> updDist,
          "totalTime" -> totalTime,
          "updTime" -> updTime
        )
        internalState += 1
        // Sending message to the client
        out ! Json.stringify(resMsg)
    }
  }

}
