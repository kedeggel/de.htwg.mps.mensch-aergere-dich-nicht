package de.htwg.mps.menschAergereDichNicht

import akka.actor.{Actor, ActorSystem, Props}
import akka.testkit.{ImplicitSender, TestActors, TestKit, TestProbe}

import scala.concurrent.duration._
import de.htwg.mps.menschAergereDichNicht.actor._
import de.htwg.mps.menschAergereDichNicht.model.diceBaseImpl
import org.scalatest.{BeforeAndAfterAll, Matchers, WordSpecLike}

class GameSpec extends TestKit(ActorSystem("View")) with ImplicitSender
  with WordSpecLike with Matchers with BeforeAndAfterAll {
  override def afterAll: Unit = {
    TestKit.shutdownActorSystem(system)
  }

  val probe = TestProbe()
  val gameRef = system.actorOf(Props(classOf[Game], new diceBaseImpl.Dice))
  val view = system.actorOf(Props(new TestViewActor() {
    override def forward(msg: Any) = {
      probe.ref ! msg
    }
  }), "View")

  "A game" should {
    "instruct the view to ask for 2-4 players" in {
      within(500 millis) {
        gameRef ! NewGame
        probe.expectMsg(RequestHumanCount(2, 4))
      }
    }
  }

}

class TestViewActor extends Actor {
  override def receive = {
    case RequestHumanCount(min, max) ⇒ {
      sender ! HumanCount(min)
      forward(RequestHumanCount(min, max))
    }
    case ShowBoard => {
      forward(ShowBoard)
    }
  }

  def forward(msg: Any) =
    {}
}
