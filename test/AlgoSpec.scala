package tests

import org.scalatest._
import org.scalatestplus.play._
import play.api.test.Helpers._
import play.api.test._

import org.joda.time._
import algo._


class AlgoSpec1 extends PlaySpec {

  "The method datePlusSeconds" must {
    "be 2017-01-02T22:48:16.800 for date" in {
      val date = LocalDateTime.parse("2017-1-02T22:48:15.400")
      val time = TimeSeconds(1.4)
      datePlusSeconds(date, time) must equal (LocalDateTime.parse("2017-01-02T22:48:16.800"))
    }
  }

  "The method datePlusSeconds" must {
    "be 2017-01-02T22:48:17.100 for date" in {
      val date = LocalDateTime.parse("2017-1-02T22:48:15.700")
      val time = TimeSeconds(1.4)
      datePlusSeconds(date, time) must equal (LocalDateTime.parse("2017-01-02T22:48:17.100"))
    }
  }

  "The method dateMinusSeconds" must {
    "be 2017-01-02T22:48:14.400 for date" in {
      val date = LocalDateTime.parse("2017-1-02T22:48:15.800")
      val time = TimeSeconds(1.4)
      dateMinusSeconds(date, time) must equal (LocalDateTime.parse("2017-01-02T22:48:14.400"))
    }
  }

  "The method dateMinusSeconds" must {
    "be 2017-01-02T22:48:13.800 for date" in {
      val date = LocalDateTime.parse("2017-1-02T22:48:15.200")
      val time = TimeSeconds(1.4)
      dateMinusSeconds(date, time) must equal (LocalDateTime.parse("2017-01-02T22:48:13.800"))
    }
  }

}


/**
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
 */


class AlgoSpec2 extends PlaySpec {

  val date1 = LocalDateTime.parse("2017-1-02T22:48:15.427")
  "The method DayType.getDayType" must {
    "be D3 for date1" in {
      DayType.getDayType(date1) mustEqual D3
    }
  }
  "The method DayInterval.getDayIntervalType" must {
    "be T3 for date1" in {
       DayInterval.getDayIntervalType(date1) mustEqual T3
    }
  }
  "The method Tariff.getTariffType" must {
    "be TF3 for date1" in {
       Tariff.getTariffType(date1) mustEqual TF3
    }
  }
  "The method DayInterval.getTimeToStartNextIntervalType" must {
    "be (2017-01-03T05:00:00.000, 22304.572) for date1" in {
       val (nextDay, nextTime) = DayInterval.getTimeToStartNextIntervalType(date1)
       nextDay must equal (LocalDateTime.parse("2017-01-03T05:00:00.000"))
       nextTime.value mustBe 22304.572
    }
  }
  "The method DayType.getTimeToStartNextDay" must {
    "be (2017-01-03T00:00:00.000, 4304.573) for date1" in {
       val (nextDay, nextTime) = DayType.getTimeToStartNextDay(date1) 
       nextDay must equal (LocalDateTime.parse("2017-01-03T00:00:00.000"))
       nextTime.value mustBe 4304.573
    }
  }

  val date2 = LocalDateTime.parse("2017-1-01T21:48:15.427")
  "The method DayType.getDayType" must {
    "be D2 for date2" in {
      DayType.getDayType(date2) mustEqual D2
    }
  }
  "The method DayInterval.getDayIntervalType" must {
    "be T2 for date2" in {
       DayInterval.getDayIntervalType(date2) mustEqual T2
    }
  }
  "The method Tariff.getTariffType" must {
    "be TF2 for date2" in {
       Tariff.getTariffType(date2) mustEqual TF2
    }
  }
  "The method DayInterval.getTimeToStartNextIntervalType" must {
    "be (2017-01-01T22:00:00.000, 704.573) for date2" in {
       val (nextDay, nextTime) = DayInterval.getTimeToStartNextIntervalType(date2)
       nextDay must equal (LocalDateTime.parse("2017-01-01T22:00:00.000"))
       nextTime.value mustBe 704.573
    }
  }
  "The method DayType.getTimeToStartNextDay" must {
    "be (2017-01-02T00:00:00.000, 7904.573) for date2" in {
       val (nextDay, nextTime) = DayType.getTimeToStartNextDay(date2)
       nextDay must equal (LocalDateTime.parse("2017-01-02T00:00:00.000"))
       nextTime.value mustBe 7904.573
    }
  }

  val date3 = LocalDateTime.parse("2017-1-03T19:48:15.427")
  "The method DayType.getDayType" must {
    "be D1 for date3" in {
      DayType.getDayType(date3) mustEqual D1
    }
  }
  "The method DayInterval.getDayIntervalType" must {
    "be T1 for date3" in {
       DayInterval.getDayIntervalType(date3) mustEqual T1
    }
  }
  "The method Tariff.getTariffType" must {
    "be TF1 for date3" in {
       Tariff.getTariffType(date3) mustEqual TF1
    }
  }
  "The method DayInterval.getTimeToStartNextIntervalType" must {
    "be (2017-01-03T20:00:00.000, 704.573) for date3" in {
       val (nextDay, nextTime) = DayInterval.getTimeToStartNextIntervalType(date3)
       nextDay must equal (LocalDateTime.parse("2017-01-03T20:00:00.000"))
       nextTime.value mustBe 704.573
    }
  }
  "The method DayType.getTimeToStartNextDay" must {
    "be (2017-01-04T00:00:00.000, 15104.573) for date3" in {
       val (nextDay, nextTime) = DayType.getTimeToStartNextDay(date3)
       nextDay must equal (LocalDateTime.parse("2017-01-04T00:00:00.000"))
       nextTime.value mustBe 15104.573
    }
  }

}


/**
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
 */


class AlgoSpec3 extends PlaySpec {

  "The method TF1.getTariffValue" must {
    "be 260p for TF1, TS1" in {
       TF1.getTariffValue(TS1).value mustEqual 260
    }
  }

  "The method TF1.getTariffValue" must {
    "be 20p for TF1, TS2" in {
       TF1.getTariffValue(TS2).value mustEqual 20
    }
  }

  "The method TF2.getTariffValue" must {
    "be 260p for TF2, TS1" in {
       TF2.getTariffValue(TS1).value mustEqual 260
    }
  }

  "The method TF2.getTariffValue" must {
    "be 20p for TF2, TS2" in {
       TF2.getTariffValue(TS2).value mustEqual 20
    }
  }

  "The method TF3.getTariffValue" must {
    "be 260p for TF3, TS1" in {
       TF3.getTariffValue(TS1).value mustEqual 260
    }
  }

  "The method TF3.getTariffValue" must {
    "be 20p for TF3, TS2" in {
       TF3.getTariffValue(TS2).value mustEqual 20
    }
  }

  "The method TF1.getTime" must {
    "be 50.4 secs for TF1, TS1" in {
       TF1.getTime(TS1).value mustEqual 50.4
    }
  }

  "The method TF1.getTime" must {
    "be 25.2 secs for TF1, TS2" in {
       TF1.getTime(TS2).value mustEqual 25.2
    }
  }

  "The method TF2.getTime" must {
    "be 41.0  secs for TF2, TS1" in {
       TF2.getTime(TS1).value mustEqual 41.0
    }
  }

  "The method TF2.getTime" must {
    "be 20.5 secs for TF2, TS2" in {
       TF2.getTime(TS2).value mustEqual 20.5
    }
  }

  "The method TF3.getTime" must {
    "be 35.0 secs for TF3, TS1" in {
       TF3.getTime(TS1).value mustEqual 35.0
    }
  }

  "The method TF3.getTime" must {
    "be 17.5 secs for TF3, TS2" in {
       TF3.getTime(TS2).value mustEqual 17.5
    }
  }

  "The method TF1.getDistance" must {
    "be 234.8 meters for TF1, TS1" in {
       TF1.getDistance(TS1).value mustEqual 234.8
    }
  }

  "The method TF1.getDistance" must {
    "be 117.4 meters for TF1, TS2" in {
       TF1.getDistance(TS2).value mustEqual 117.4
    }
  }

  "The method TF2.getDistance" must {
    "be 191.0 meters for TF2, TS1" in {
       TF2.getDistance(TS1).value mustEqual 191.0
    }
  }

  "The method TF2.getDistance" must {
    "be 95.5 meters for TF2, TS2" in {
       TF2.getDistance(TS2).value mustEqual 95.5
    }
  }

  "The method TF3.getDistance" must {
    "be 162.4 meters for TF3, TS1" in {
       TF3.getDistance(TS1).value mustEqual 162.4
    }
  }

  "The method TF3.getDistance" must {
    "be 81.2 meters for TF3, TS2" in {
       TF3.getDistance(TS2).value mustEqual 81.2
    }
  }

}


/**
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
 */


class AlgoSpec4 extends PlaySpec {

  "The method State.getNewState" must {
    "Give a Left because state is None and update.endDate is not None" in {
      val date = LocalDateTime.parse("2017-1-02T22:48:15.000")
      val date1 = LocalDateTime.parse("2017-1-02T22:48:20.000")
      val update = Update(date, Some(date1), DistanceMeters(0), false)
      val left = State.getNewState(None, update).left.toOption.get
      left must equal ("To work with a trip both state and update must be consistent")
    }
  }

  "The method State.getNewState" must {
    "Give a Left because update.startDate has a smaller date than state.startTime" in {
      val date = LocalDateTime.parse("2017-1-02T22:48:15.000")
      val date1 = LocalDateTime.parse("2017-1-02T22:48:20.000")
      val update = Update(date, Some(date1), DistanceMeters(0), false)
      val left = State.getNewState(Some(State(LocalDateTime.parse("2017-2-02T22:48:15.000"), TimeSeconds(10), DistanceMeters(10), CurrencyPence(20), Some(TF2), Some(LocalDateTime.parse("2017-2-02T22:50:15.000")), true)), update).left.toOption.get
      left must equal ("To work with a trip both state and update must be consistent")
    }
  }


  "The method State.getNewState" must {
    "start a new trip with a fresh state for date and update" in {
      val date = LocalDateTime.parse("2017-1-02T22:48:15.000")
      val update = Update(date, None, DistanceMeters(0), false)
      val (state, remain) = State.getNewState(None, update).right.toOption.get
      state must equal (State(date, TimeSeconds(0), DistanceMeters(0), CurrencyPence(0), None, None, true))
      remain must be (None)
    }
  }

  "The method State.getNewState" must {
    "Resolve the update recursively with a tariff in meters for the Tariff TF3 and return a Remain" in {
      // The date is TF3 tariff
      val date = LocalDateTime.parse("2017-1-02T22:48:15.000")
      val update = Update(date, None, DistanceMeters(0), false)
      val (state, remain) = State.getNewState(None, update).right.toOption.get
      // 30 seconds update
      val date2 = LocalDateTime.parse("2017-1-02T22:48:45.000")
      /** Update for the 30 seconds and 420 mts (i.e. 14 mts/sec):
       * It is starting with state TS1:
       *   (1) TF3/TS1 is 260p for Min(162mts, 35sec):
       *         - Here the min is 162 mts
       *   (2) For the remaining distance is TS2:
       *         - TF3/TS2 is 20p for Min(81.2mts, 17.5sec):
       *             - Because it the velocity is 14mts/sec, it is doing the 81.2mts
       *               in 5.8 secs, so the charge is for each 81.2mts:
       *                 - There are 3 charges, and a Remain of 14mts
       */
      val update2 = Update(date, Some(date2), DistanceMeters(420), false)
      val (state2, remain2) = State.getNewState(Some(state), update2).right.toOption.get

      state2.startTime must === (date)
      state2.lastTimeCovered must === (TimeSeconds(28.93053179677668))
      state2.lastDistanceCovered must === (DistanceMeters(406.0))
      state2.lastTariffValue must === (CurrencyPence(320))
      state2.lastTariffType.get must === (TF3)
      state2.lastTariffTypeDate.get must === (LocalDateTime.parse("2017-01-02T22:48:44.008"))
      state2.isOpen must be (true)
      remain2 must === (Some(Remain(Update(LocalDateTime.parse("2017-01-02T22:48:44.033"), Some(LocalDateTime.parse("2017-01-02T22:48:45.000")), DistanceMeters(14.000000000000028),false),CurrencyPence(4))))
    }
  }

  "The method State.getNewState" must {
    "Resolve the update recursively with a tariff in meters for the Tariff TF3 and closes the trip" in {
      // The date is TF3 tariff
      val date = LocalDateTime.parse("2017-1-02T22:48:15.000")
      val update = Update(date, None, DistanceMeters(0), false)
      val (state, remain) = State.getNewState(None, update).right.toOption.get
      // 30 seconds update
      val date2 = LocalDateTime.parse("2017-1-02T22:48:45.000")
      /** Update for the 30 seconds and 420 mts (i.e. 14 mts/sec):
       * It is starting with state TS1:
       *   (1) TF3/TS1 is 260p for Min(162mts, 35sec):
       *         - Here the min is 162 mts
       *   (2) For the remaining distance is TS2:
       *         - TF3/TS2 is 20p for Min(81.2mts, 17.5sec):
       *             - Because it the velocity is 14mts/sec, it is doing the 81.2mts
       *               in 5.8 secs, so the charge is for each 81.2mts:
       *                 - There are 3 charges, and the Remain of 14mts is prorated
       */
      val update2 = Update(date, Some(date2), DistanceMeters(420), true)
      val (state2, remain2) = State.getNewState(Some(state), update2).right.toOption.get

      state2.startTime must === (date)
      state2.lastTimeCovered must === (TimeSeconds(29.89753179677668))
      state2.lastDistanceCovered must === (DistanceMeters(420.0))
      state2.lastTariffValue must === (CurrencyPence(324))
      state2.lastTariffType.get must === (TF3)
      state2.lastTariffTypeDate.get must === (LocalDateTime.parse("2017-01-02T22:48:45.000"))
      state2.isOpen must be (false)
      remain2.isDefined must be (false)
    }
  }

  "The method State.getNewState" must {
    "Resolve the update recursively with a tariff in seconds for the Tariff TF3 and return a Remain" in {
      // The date is TF3 tariff
      val date = LocalDateTime.parse("2017-1-02T22:48:15.000")
      val update = Update(date, None, DistanceMeters(0), false)
      val (state, remain) = State.getNewState(None, update).right.toOption.get
      // 45 seconds update
      val date2 = LocalDateTime.parse("2017-1-02T22:49:00.000")
      /** Update for the 45 seconds and 200 mts (i.e. 4.44 mts/sec):
       * It is starting with state TS1:
       *   (1) TF3/TS1 is 260p for Min(162mts, 35sec):
       *         - Here the min is 35 sec
       *   (2) For the remaining distance is TS2:
       *         - TF3/TS2 is 20p for Min(81.2mts, 17.5sec):
       *             - Because it the velocity is 4.44 mts/sec, in 17.5 secs it is
       *               doing 77.77 mts, so the charge is for each 17.5 secs:
       *                 - There are 1 charge, and a Remain of 44.444 mts
       */
      val update2 = Update(date, Some(date2), DistanceMeters(200), false)
      val (state2, remain2) = State.getNewState(Some(state), update2).right.toOption.get

      state2.startTime must === (date)
      state2.lastTimeCovered must === (TimeSeconds(35.0))
      state2.lastDistanceCovered must === (DistanceMeters(155.55555555555557))
      state2.lastTariffValue must === (CurrencyPence(260))
      state2.lastTariffType.get must === (TF3)
      state2.lastTariffTypeDate.get must === (LocalDateTime.parse("2017-01-02T22:48:50.000"))
      state2.isOpen must be (true)
      remain2 must === (Some(Remain(Update(LocalDateTime.parse("2017-01-02T22:48:50.025"),Some(LocalDateTime.parse("2017-01-02T22:49:00.000")),DistanceMeters(44.44444444444443),false),CurrencyPence(12))))
       
    }
  }

  "The method State.getNewState" must {
    "Resolve the update recursively with a tariff in seconds for the Tariff TF3 and closes the trip" in {
      // The date is TF3 tariff
      val date = LocalDateTime.parse("2017-1-02T22:48:15.000")
      val update = Update(date, None, DistanceMeters(0), false)
      val (state, remain) = State.getNewState(None, update).right.toOption.get
      // 45 seconds update
      val date2 = LocalDateTime.parse("2017-1-02T22:49:00.000")
      /** Update for the 45 seconds and 200 mts (i.e. 4.44 mts/sec):
       * It is starting with state TS1:
       *   (1) TF3/TS1 is 260p for Min(162mts, 35sec):
       *         - Here the min is 35 sec
       *   (2) For the remaining distance is TS2:
       *         - TF3/TS2 is 20p for Min(81.2mts, 17.5sec):
       *             - Because it the velocity is 4.44 mts/sec, in 17.5 secs it is
       *               doing 77.77 mts, so the charge is for each 17.5 secs:
       *                 - There are 1 charge, and the Remain of 44.444 mts is prorated
       */
      val update2 = Update(date, Some(date2), DistanceMeters(200), true)
      val (state2, remain2) = State.getNewState(Some(state), update2).right.toOption.get

      state2.startTime must === (date)
      state2.lastTimeCovered must === (TimeSeconds(44.975))
      state2.lastDistanceCovered must === (DistanceMeters(200.0))
      state2.lastTariffValue must === (CurrencyPence(272))
      state2.lastTariffType.get must === (TF3)
      state2.lastTariffTypeDate.get must === (LocalDateTime.parse("2017-01-02T22:49:00.000"))
      state2.isOpen must be (false)
      remain2.isDefined must be (false)

    }
  }

  "The method State.getNewState" must {
    "Resolve the update recursively with a tariff in seconds for the Tariff TF2 and return a Remain that belongs to TF3" in {
      // The date is TF2 tariff close to TF3
      val date = LocalDateTime.parse("2017-1-01T21:59:20.00")
      val update = Update(date, None, DistanceMeters(0), false)
      val (state, remain) = State.getNewState(None, update).right.toOption.get
      // 45 seconds update
      val date2 = LocalDateTime.parse("2017-1-01T22:00:05.00")
      /** Update for the 45 seconds and 450 mts (i.e. 10 mts/sec):
       * It is starting with state TS1:
       *   (1) TF2/TS1 is 260p for Min(191mts, 41sec):
       *         - Here the min is 191mts
       *   (2) For the remaining distance is TS2:
       *         - TF3/TS2 is 20p for Min(95.5mts, 20.5sec):
       *             - Because it the velocity is 10 mts/sec it is reaching the 95.5 mts
       *               before the 20.5 secs, so the charge is 20p for each 95.5 mts:
       *                 - It is enough for two whole updates before the change of tariff, 
       *                   and there is a little remain of 1.83 secs (i.e. around 18.31 mts)
       *                   that need to be prorated, because at 22:00 is changing the tariff.
       *                   There is also a little Remain for the new Tariff TF3 from 22:00 to 
       *                   20:00:05
       */
      val update2 = Update(date, Some(date2), DistanceMeters(450), false)
      val (state2, remain2) = State.getNewState(Some(state), update2).right.toOption.get

      state2.startTime must === (date)
      state2.lastTimeCovered must === (TimeSeconds(39.89788101732138))
      state2.lastDistanceCovered must === (DistanceMeters(399.43022051206157))
      state2.lastTariffValue must === (CurrencyPence(304))
      state2.lastTariffType.get must === (TF2)
      state2.lastTariffTypeDate.get must === (LocalDateTime.parse("2017-01-01T21:59:59.975"))
      state2.isOpen must be (true)
      remain2 must === (Some(Remain(Update(LocalDateTime.parse("2017-01-01T22:00:00.000"),Some(LocalDateTime.parse("2017-01-01T22:00:05.000")),DistanceMeters(50.56977948793843),false),CurrencyPence(81))))
    }
  }

  "The method State.getNewState" must {
    "Resolve the update recursively with a tariff in seconds for the Tariff TF2 and return a Remain that belongs to TF3. This Remain is processed in a new update, and a new Remain is returned" in {
      // The date is TF2 tariff close to TF3
      val date = LocalDateTime.parse("2017-1-01T21:59:20.00")
      val update = Update(date, None, DistanceMeters(0), false)
      val (state, remain) = State.getNewState(None, update).right.toOption.get
      // 45 seconds update
      val date2 = LocalDateTime.parse("2017-1-01T22:00:05.00")
      /** Update for the 45 seconds and 450 mts (i.e. 10 mts/sec):
       * It is starting with state TS1:
       *   (1) TF2/TS1 is 260p for Min(191mts, 41sec):
       *         - Here the min is 191mts
       *   (2) For the remaining distance is TS2:
       *         - TF3/TS2 is 20p for Min(95.5mts, 20.5sec):
       *             - Because it the velocity is 10 mts/sec it is reaching the 95.5 mts
       *               before the 20.5 secs, so the charge is 20p for each 95.5 mts:
       *                 - It is enough for two whole updates before the change of tariff,
       *                   and there is a little remain of 1.83 secs (i.e. around 18.31 mts)
       *                   that need to be prorated, because at 22:00 is changing the tariff.
       *                   There is also a little Remain for the new Tariff TF3 from 22:00 to
       *                   20:00:05
       */
      val update2 = Update(date, Some(date2), DistanceMeters(450), false)
      val (state2, remain2) = State.getNewState(Some(state), update2).right.toOption.get
      
      /** New update starting with the Remain and adding 35 seconds more. So in total are 40 secs
        * The distance for the new update is the Remain distance, more the distance covered in these
        * extra 35 seconds, so the new distance is 50.57 mts more 350 mts (going at 10 mts/sec)
        */
      val date3 = LocalDateTime.parse("2017-01-01T22:00:40.000")
      /**
       * It is starting TS3 with TS1:
       *   (1) TF3/TS1 is 260p for Min(162mts, 35sec):
       *         - Here the min is 162 mts
       *   (2) For the remaining distance is TS2:
       *         - TF3/TS2 is 20p for Min(81.2mts, 17.5sec):
       *             - Because it the velocity is 10mts/sec, it is doing the 81.2mts
       *               in around 8.09 secs, so the charge is for each 81.2mts:
       *                 - There are 2 charges, and the Remain for TF3 of 75.77 mts is prorated
       */
      val update3 = Update(remain2.get.remain.startDate, Some(date3), DistanceMeters(400.57), false)
      val (state3, remain3) = State.getNewState(Some(state2), update3).right.toOption.get

      state3.startTime must === (date)
      state3.lastTimeCovered must === (TimeSeconds(72.30154994075532))
      state3.lastDistanceCovered must === (DistanceMeters(724.2302205120617))
      state3.lastTariffValue must === (CurrencyPence(604))
      state3.lastTariffType.get must === (TF3)
      state3.lastTariffTypeDate.get must === (LocalDateTime.parse("2017-01-01T22:00:32.454"))
      state3.isOpen must be (true)
      remain3 must === (Some(Remain(Update(LocalDateTime.parse("2017-01-01T22:00:32.479"),Some(LocalDateTime.parse("2017-01-01T22:00:40.000")),DistanceMeters(75.76999999999997),false),CurrencyPence(19))))
    }
  }

  "The method State.getNewState" must {
    "Resolve the update recursively with a tariff in seconds for the Tariff TF2 and return a Remain that belongs to TF3. This Remain is processed in a new update, and a new Remain is returned. This Remain is proccesed in a new update that reaches the maximum distance, so TF4 is started" in {
      // The date is TF2 tariff close to TF3
      val date = LocalDateTime.parse("2017-1-01T21:59:20.00")
      val update = Update(date, None, DistanceMeters(0), false)
      val (state, remain) = State.getNewState(None, update).right.toOption.get
      // 45 seconds update
      val date2 = LocalDateTime.parse("2017-1-01T22:00:05.00")
      /** Update for the 45 seconds and 450 mts (i.e. 10 mts/sec):
       * It is starting with state TS1:
       *   (1) TF2/TS1 is 260p for Min(191mts, 41sec):
       *         - Here the min is 191mts
       *   (2) For the remaining distance is TS2:
       *         - TF3/TS2 is 20p for Min(95.5mts, 20.5sec):
       *             - Because it the velocity is 10 mts/sec it is reaching the 95.5 mts
       *               before the 20.5 secs, so the charge is 20p for each 95.5 mts:
       *                 - It is enough for two whole updates before the change of tariff,
       *                   and there is a little remain of 1.83 secs (i.e. around 18.31 mts)
       *                   that need to be prorated, because at 22:00 is changing the tariff.
       *                   There is also a little Remain for the new Tariff TF3 from 22:00 to
       *                   20:00:05
       */
      val update2 = Update(date, Some(date2), DistanceMeters(450), false)
      val (state2, remain2) = State.getNewState(Some(state), update2).right.toOption.get

      /** New update starting with the Remain and adding 35 seconds more. So in total are 40 secs
        * The distance for the new update is the Remain distance, more the distance covered in these
        * extra 35 seconds, so the new distance is 50.57 mts more 350 mts (going at 10 mts/sec)
        */
      val date3 = LocalDateTime.parse("2017-01-01T22:00:40.000")
      /**
       * It is starting TS3 with TS1:
       *   (1) TF3/TS1 is 260p for Min(162mts, 35sec):
       *         - Here the min is 162 mts
       *   (2) For the remaining distance is TS2:
       *         - TF3/TS2 is 20p for Min(81.2mts, 17.5sec):
       *             - Because it the velocity is 10mts/sec, it is doing the 81.2mts
       *               in around 8.09 secs, so the charge is for each 81.2mts:
       *                 - There are 2 charges, and the Remain for TF3 of 75.77 mts is prorated
       */
      val update3 = Update(remain2.get.remain.startDate, Some(date3), DistanceMeters(400.57), false)
      val (state3, remain3) = State.getNewState(Some(state2), update3).right.toOption.get

      /** New update starting with the Remain and adding more than 15 and 20 secs.
        * The distance for the new update is the Remain distance, more the distance covered in these
        * extra 15 mins and 20 secs, so the new distance is 9200 mts more 75.77 mts (going at 10 mts/sec)
        */
      val date4 = LocalDateTime.parse("2017-01-01T22:16:00.000")
      val update4 = Update(remain3.get.remain.startDate, Some(date4), DistanceMeters(9275.77), false)

      val (state4, remain4) = State.getNewState(Some(state3), update4).right.toOption.get

      state4.startTime must === (date)
      state4.lastTimeCovered must === (TimeSeconds(988.7308498167895))
      state4.lastDistanceCovered must === (DistanceMeters(9916.547462562394))
      state4.lastTariffValue must === (CurrencyPence(2864))
      state4.lastTariffType.get must === (TF4)
      state4.lastTariffTypeDate.get must === (LocalDateTime.parse("2017-01-01T22:15:51.766"))
      state4.isOpen must be (true)
      remain4 must === (Some(Remain(Update(LocalDateTime.parse("2017-01-01T22:15:51.791"),Some(LocalDateTime.parse("2017-01-01T22:16:00.000")),DistanceMeters(83.45275794967091),false),CurrencyPence(20))))
    }
  }

}
