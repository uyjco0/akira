package algo

import scala.annotation.tailrec
import org.joda.time._
import com.typesafe.config.{ConfigFactory, Config}
import scala.collection.JavaConverters._

/**
 ***************************************
 *** AUXILIAR CLASSES FOR TYPE SAFETY ***
 ***************************************
 */


/**
 * Currency in pence
 * @param value
 */
final case class CurrencyPence(value: Int)

/**
 * Distance in meters
 * @param value
 */
final case class DistanceMeters(value: Double)

/**
 * Time in fractional seconds.
 * It is a more accurate version of the class 
 * that gives the Joda Time library 'Seconds'
 * @param value
 */
final case class TimeSeconds(value: Double)

/**
 * Velocity in Meters/Seconds
 * @param value
 */
final case class VelocityMetersSecs(value: Double)

/**
 ***************************************************
 *** ALGEBRAIC DATA TYPES FOR THE PROBLEM DOMAIN ***
 ***************************************************
 */


/**
 * Algebraic Data Type for time intervals in a day
 */
sealed trait DayInterval

/** Time interval between 05:00 and 20:00 */
case object T1 extends DayInterval

/** Time interval between 20:00 and 22:00 */
case object T2 extends DayInterval

/** Time interval between 22:00 and 05:00 */
case object T3 extends DayInterval

/** Companion object with several utilities to manipulate the ADT 'DayInterval' */
object DayInterval {

  val startRangeT1Str = "05:00"
  val startRangeT1 = LocalTime.parse(startRangeT1Str)
  val startRangeT2Str = "20:00"
  val startRangeT2 = LocalTime.parse(startRangeT2Str)
  val startRangeT3Str = "22:00"
  val startRangeT3 = LocalTime.parse(startRangeT3Str)
  val endRangeT3FirstHalfStr = "23:59:59.999"
  val startRangeT3SecondHalfStr = "00:00"
  val startRangeT3SecondHalf = LocalTime.parse(startRangeT3SecondHalfStr)
  val rangeT1 = new Interval(localToNormalizedDate(startRangeT1), localToNormalizedDate(startRangeT2))
  val rangeT2 = new Interval(localToNormalizedDate(startRangeT2), localToNormalizedDate(startRangeT3))
  val rangeT3SecondHalf = new Interval(localToNormalizedDate(startRangeT3SecondHalf), localToNormalizedDate(startRangeT1))

  /**
   * Given a date returns the DayInterval for this date
   * @param dateToCheck
   */
  def getDayIntervalType(dateToCheck: LocalDateTime): DayInterval = {
    val dateToCheckNormalized = normalizeDate(dateToCheck)
    // Check to range interval for 'timeToCheck'   
    if (rangeT3SecondHalf.contains(dateToCheckNormalized)) {
      T3
    } else if (rangeT1.contains(dateToCheckNormalized)) {
      T1
    } else if (rangeT2.contains(dateToCheckNormalized)) {
      T2
    } else {
      // It is in T3FirstHalf - from 22:00 to 23:59:59.999
      T3
    }
  }

  /** 
   * Given an interval type returns the next interval type
   * @param intervalType
   */
  def getNextIntervalType(intervalType: DayInterval): DayInterval = {
    intervalType match {
      case T1 => T2
      //-----------
      case T2 => T3
      //-----------
      case T3 => T1
    }
  }

  /** 
   * Given an interval type returns its local time start
   * @param intervalType
   */
  def getStartIntervalType(intervalType: DayInterval): LocalTime = {
    intervalType match {
      case T1 => startRangeT1
      //---------------------
      case T2 => startRangeT2
      //---------------------
      case T3 => startRangeT3
    }
  }

  /** 
   * Given a date returns the date and the time in seconds for the start of the next interval type
   * @param dateToCheck
   */
  def getTimeToStartNextIntervalType(dateToCheck: LocalDateTime): Tuple2[LocalDateTime, TimeSeconds] = {
    val intervalType = getDayIntervalType(dateToCheck)
    val nextIntervalType = getNextIntervalType(intervalType)
    val startNextIntervalType = getStartIntervalType(nextIntervalType)
    val totalSeconds = nextIntervalType match {
      case T1 => {
        // We loses 1 millisecond of accuracy
        val timeAux1 = LocalTime.parse(endRangeT3FirstHalfStr)
        val timeAux2 = LocalTime.parse(startRangeT3SecondHalfStr)
        val firstSeconds = secondsInBetweenLocalTime(dateToCheck.toLocalTime(), timeAux1)
        val lastSeconds = secondsInBetweenLocalTime(timeAux2, startNextIntervalType)
        TimeSeconds(firstSeconds.value + lastSeconds.value)
      }
      //------------------------------
      case _ => secondsInBetweenLocalTime(dateToCheck.toLocalTime(), startNextIntervalType)
    }
    val nextIntervalDateAux = datePlusSeconds(dateToCheck, totalSeconds)
    // It is needed to build the date manually
    val nextTime = nextIntervalType match {
      case T1 => startRangeT1Str
      //------------------------
      case T2 => startRangeT2Str
      //------------------------
      case T3 => startRangeT3Str
    }
    val nextIntervalDate = changeTime(nextIntervalDateAux, nextTime)
    (nextIntervalDate, totalSeconds)
  } 

}


/**
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
 */


/**
 * Algebraic Data Type for day types
 */
sealed trait DayType

/** Monday to Friday */
case object D1 extends DayType

/** Saturday and Sunday */
case object D2 extends DayType

/** Public holidays */
case object D3 extends DayType

/** Companion object with several utilities to manipulate the ADT 'DayType' */
object DayType {

  // Load the configuration file (i.e. application.conf)
  val config:Config = ConfigFactory.load()
  // Define the key to access the configuration file
  val currentDate = new LocalDateTime()
  val currentYear = currentDate.getYear().toString
  val keyConfig = "holidays.".concat(currentYear)
  // Get the list of holiday days from the configuration
  val listHolidays:List[String] = config.getStringList(keyConfig).asScala.toList
  val listHolidaysNormalized = listHolidays.map(x => LocalDateTime.parse(currentYear.concat("-").concat(x))).map(y => y.getMonthOfYear().toString.concat("-").concat(y.getDayOfMonth().toString))
  // Convert the list to access for easy checking
  val mapHolidays:Map[String,Boolean] = listHolidaysNormalized.map(x => (x, true)).toMap

  /** 
   * Given a date returns the date type
   * @param dateToCheck
   */
  def getDayType(dateToCheck: LocalDateTime): DayType = {
    val dateMonth = dateToCheck.getMonthOfYear().toString
    val dateDay = dateToCheck.getDayOfMonth().toString
    if (mapHolidays.contains(dateMonth.concat("-").concat(dateDay))) {
      D3
    } else {
      dateToCheck.getDayOfWeek match {
        case DateTimeConstants.SATURDAY => D2
        //-----------------------------------
        case DateTimeConstants.SUNDAY => D2
        //-----------------------------------
        case _ => D1
      }
    }
  }

  /**
   * Given a date returns the date representing the start of the next day
   * @param dateToCheck
   */
  def getNextDay(dateToCheck: LocalDateTime): LocalDateTime = {
    dateToCheck.plusDays(1).withTime(0,0,0,0)
  }

  /**
   * Given a data returns the date and the time in seconds for the start of the next day
   * @param dateToCheck
   */
  def getTimeToStartNextDay(dateToCheck: LocalDateTime): Tuple2[LocalDateTime, TimeSeconds] = {
    val nextDayStart = getNextDay(dateToCheck)
    (nextDayStart, secondsInBetweenLocalDateTime(dateToCheck, nextDayStart))
  }

}


/**
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
 */


/**
 * Algebraic Data Type for the states corresponding to the tariff types
 * TF1, TF2 and TF3
 */
sealed trait TariffState

/** Tariff start state */
case object TS1 extends TariffState

/** After the start and while the distance covered < MAX_DIST_COVERED */
case object TS2 extends TariffState


/**
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
 */


/** 
 * Algebraic Data Type for tariff types
 */
sealed trait Tariff

/** Tariff for D1/T1 */
case object TF1 extends Tariff {

  /**
   * Given a tariff state returns the tariff value to be
   * applied to the tariff
   * @param tariffState
   */
  def getTariffValue(tariffState: TariffState): CurrencyPence = {
    tariffState match {
      case TS1 => CurrencyPence(260)
      // --------------------------
      case TS2 => CurrencyPence(20)
    }
  }

  /**
   * Given a tariff state returns the time used to calculate
   * the tariff value for the tariff
   * @param tariff
   */
  def getTime(tariffState: TariffState): TimeSeconds = {
    tariffState match {
      case TS1 => TimeSeconds(50.4)
      // -------------------------
      case TS2 => TimeSeconds(25.2) 
    }
  } 

  /**
   * Given a tariffstate  returns the distance used to calculate
   * the tariff value for the tariff
   * @param tariff
   */
  def getDistance(tariffState: TariffState): DistanceMeters = {
    tariffState match {
      case TS1 => DistanceMeters(234.8)
      // ------------------------------
      case TS2 => DistanceMeters(117.4) 
    }
  } 

}

/** Tariff for D1/T2 or D2/(T1 or T2) */
case object TF2 extends Tariff {

  /**
   * Given a tariff state returns the tariff value to be
   * applied to the tariff
   * @param tariffState
   */
  def getTariffValue(tariffState: TariffState): CurrencyPence = {
    tariffState match {
      case TS1 => CurrencyPence(260)
      // --------------------------
      case TS2 => CurrencyPence(20)
    }
  }

  /**
   * Given a tariff state returns the time used to calculate
   * the tariff value for the tariff
   * @param tariff
   */
  def getTime(tariffState: TariffState): TimeSeconds = {
    tariffState match {
      case TS1 => TimeSeconds(41)
      // -------------------------
      case TS2 => TimeSeconds(20.5) 
    }
  }

  /**
   * Given a tariffstate  returns the distance used to calculate
   * the tariff value for the tariff
   * @param tariff
   */
  def getDistance(tariffState: TariffState): DistanceMeters = {
    tariffState match {
      case TS1 => DistanceMeters(191)
      // ------------------------------
      case TS2 => DistanceMeters(95.5)
    }
  }

}

/** Tariff for D1/T3 or D2/T3 or D3 */
case object TF3 extends Tariff {

  /**
   * Given a tariff state returns the tariff value to be
   * applied to the tariff
   * @param tariffState
   */
  def getTariffValue(tariffState: TariffState): CurrencyPence = {
    tariffState match {
      case TS1 => CurrencyPence(260)
      // --------------------------
      case TS2 => CurrencyPence(20)
    }
  } 

  /**
   * Given a tariff state returns the time used to calculate
   * the tariff value for the tariff
   * @param tariff
   */
  def getTime(tariffState: TariffState): TimeSeconds = {
    tariffState match {
      case TS1 => TimeSeconds(35)
      // -------------------------
      case TS2 => TimeSeconds(17.5) 
    }
  }

  /**
   * Given a tariffstate  returns the distance used to calculate
   * the tariff value for the tariff
   * @param tariff
   */
  def getDistance(tariffState: TariffState): DistanceMeters = {
    tariffState match {
      case TS1 => DistanceMeters(162.4)
      // -------------------------
      case TS2 => DistanceMeters(81.2)
    }
  }

}

/** Tariff for distances >= MAX_DIST_COVERED */
case object TF4 extends Tariff {

   /**
    * It returns the corresponding tariff value
    * @param tariff
    */
  def getTariffValue(): CurrencyPence = {
    CurrencyPence(20)
  }

  /**
   * It returns the corresponding time used to calculate
   * the tariff value
   * @param tariff
   */
  def getTime(): TimeSeconds = {
    TimeSeconds(18.7)
  }

  /**
   * It returns the corresponding distance used to calculate
   * the tariff value
   * @param tariff
   */
  def getDistance(): DistanceMeters = {
    DistanceMeters(86.9)
  }

}

/** Companion object with several utilities to manipulate the ADT 'Tariff' */
object Tariff {

  /**
   * Given a date returns the corresponding tariff type
   * @param dateToCheck
   */
  def getTariffType(dateToCheck: LocalDateTime): Tariff = {
    val dayType = DayType.getDayType(dateToCheck)
    val dayIntervalType = DayInterval.getDayIntervalType(dateToCheck)
    (dayType, dayIntervalType) match {
      case (D1, T1) => TF1
      //------------------
      case (D1, T2) => TF2
      //------------------
      case (D2, T1) => TF2
      //------------------
      case (D2, T2) => TF2
      //------------------
      case (D1, T3) => TF3
      //------------------
      case (D2, T3) => TF3
      //------------------
      case (D3, _) => TF3
    }
  }

}


/**
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
 */


/**
 * Client trip update
 * @param startDate the start date of the update interval.
 * @param endDate the end date of the update interval. It is None for the first update from the client
 * @param distanceCovered distance covered on the update interval
 * @param isEnd whether it is the final client update or not
 */
final case class Update(startDate: LocalDateTime, endDate: Option[LocalDateTime], distanceCovered: DistanceMeters, isEnd: Boolean)

/**
 * The client trip update was not enough to change the trip state, so a Remain is created to accomodate the whole unprocessed update, 
   but to the client is shown a tariff update (prorated)
 * @param remain the update that was not possible to process
 * @param showProration the tariff update proration to be shown to the client
 */ 
final case class Remain(remain: Update, showProration: CurrencyPence)

/**
 * Trip current state
 * @param startTime trip start time
 * @param latTimeCovered time covered in the trip until last update
 * @param lastDistanceCovered distance covered in the trip until last update
 * @param lastTariffValue tariff value calculated until last update
 * @param lastTariffType tariff type being used in the last update
 * @param lastTariffTypeDate date that 'lastTariffType' was used
 * @param isOpen whether the trip is still going on, or is already over
 */
final case class State(startTime: LocalDateTime, lastTimeCovered: TimeSeconds, lastDistanceCovered: DistanceMeters, lastTariffValue: CurrencyPence, lastTariffType:Option[Tariff], lastTariffTypeDate: Option[LocalDateTime], isOpen: Boolean)

/** companion object with the main API for the problem domain */
object State {

  // The maximum difference allowed between client
  // updates before considering there is a gap
  val MAX_DIFF_SECONDS = TimeSeconds(1) 

  /**
   * Given an state and an update, returns the tariff type and tariff type state to be applied to the given update
   * @param state
   * @param update
   */
  def getTariffTypeState(state: State, update: Update): Either[String, Tuple2[Tariff, Option[TariffState]]] = {
    state.lastTariffType match {
      case Some(TF4) => Right((TF4, None))
      //---------------------------
      case _ => {
        val auxTariffType = Tariff.getTariffType(update.startDate)
        (state.lastTariffType, state.lastTariffTypeDate) match {
          case (None, None) => Right((auxTariffType, Some(TS1)))
          //-----------------------------------------------------------------------------------------------------------------------------------
          case (Some(x), Some(d)) =>   {
            if (d.isBefore(update.startDate)) {
              val secsDiff = secondsInBetweenLocalDateTime(d, update.startDate)
              if (secsDiff.value <= MAX_DIFF_SECONDS.value) {
                Right((auxTariffType, if (auxTariffType == x) Some(TS2) else Some(TS1)))
              } else {
                // There is a GAP between the current client update and the last client update
                Left(s"There is a gap greater than $MAX_DIFF_SECONDS between 'state.lastTariffTypeDate' and 'update.startDate'")
              }
            } else {
              Left("The date 'state.lastTariffTypeDate' must be before date 'update.startDate'")
            }
          }
          //-----------------------------------------------------------------------------------------------------------------------------------
          case (_, _) => Left("The fields 'state.lastTariffType' and 'state.lastTariffTypeDate' are either (None, None) or (Some(x), Some(d))")
        }
      }
    }
  }
 
  /**
   * Given a tariff type and tariff type state, returns the distance, time and tariff values to be used to calculate a tariff update
   * @param tariffType 
   * @param tariffState
   */
  def getUpdates(tariffType: Tariff, tariffState: Option[TariffState]): Option[Tuple3[DistanceMeters, TimeSeconds, CurrencyPence]] = {
    tariffState match {
      case Some(_) => {
        tariffType match {
          case TF1 => Some((TF1.getDistance(tariffState.get), TF1.getTime(tariffState.get), TF1.getTariffValue(tariffState.get)))
          //---------------------------------------------------------------------------------------------------------------
          case TF2 => Some((TF2.getDistance(tariffState.get), TF2.getTime(tariffState.get), TF2.getTariffValue(tariffState.get)))
          //---------------------------------------------------------------------------------------------------------------
          case TF3 => Some((TF3.getDistance(tariffState.get), TF3.getTime(tariffState.get), TF3.getTariffValue(tariffState.get)))
          //---------------------------------------------------------------------------------------------------------------
          case TF4 => Some((TF4.getDistance(), TF4.getTime(), TF4.getTariffValue()))
        }
      }
      // -----------
      case None => {
        tariffType match {
          case TF4 => Some((TF4.getDistance(), TF4.getTime(), TF4.getTariffValue()))
          //------------------------------------------------------------------------
          case _ => None
        }
      }
    }
  }

  /**
   * Given a client update returns the average velocity in this update
   * @param update
   */
  def getAverageVelocity(update: Update): VelocityMetersSecs = {
    update.endDate match {
      case Some(_) => {
        val updateSecs = secondsInBetweenLocalDateTime(update.startDate, update.endDate.get) 
        updateSecs.value match {
          case 0 => VelocityMetersSecs(0)
          //-----------------------------------------------------------
          // velocity = distance/time
          case _ => VelocityMetersSecs(update.distanceCovered.value/updateSecs.value)
        }
      }
      //--------------------------------
      case None => VelocityMetersSecs(0)
    }
  }

  /**
   * Given a distance to be covered and a velocity, returns the time to cover this distance
   * @param distance
   * @param velocity
   */
  def getTimeFromDistanceVel(distance: DistanceMeters, velocity: VelocityMetersSecs): TimeSeconds = {
    // time = distance/velocity
    velocity.value match {
      case 0 => TimeSeconds(0)
      //--------------------------------------------------
      // time = distance/velocity
      case _ => TimeSeconds(distance.value/velocity.value)
    }
  }

  /**
   * Given a time to be covered and a velocity, returns the distance to be covered
   * @param time
   * @param velocity
   */
  def getDistanceFromTimeVel(time: TimeSeconds, velocity: VelocityMetersSecs): DistanceMeters = {
    // distance = time*velocity
    DistanceMeters(time.value*velocity.value)
  }

  /**
   * Given the distance covered in an update, the seconds in the update, the distance to calculate the update and the time to calculate the update, returns the time in seconds to apply the
   * update, and the distance in meters for this update.
   * @param update update from the client
   * @param tariffUpdateDist the distance to calculate the update
   * @param tariffUpdateTime the tiem to calculate the update
   */
  def getDistanceTimeForUpdate(update: Update, tariffUpdateDist: DistanceMeters, tariffUpdateTime: TimeSeconds): Tuple2[TimeSeconds, DistanceMeters] = {
    val avgVelocity = getAverageVelocity(update)
    val timeToTariffUpdateDist = getTimeFromDistanceVel(tariffUpdateDist, avgVelocity)
    if (timeToTariffUpdateDist.value <= tariffUpdateTime.value) {
      (timeToTariffUpdateDist, tariffUpdateDist)
    } else {
      (tariffUpdateTime, getDistanceFromTimeVel(tariffUpdateTime, avgVelocity))
    }
  }

  /**
   * Given a date returns the closest next date to start a new update 
   * from the client
   * @param date
   */
  def getDateStartNextUpdate(date: LocalDateTime): LocalDateTime = {
    datePlusSeconds(date, TimeSeconds(MAX_DIFF_SECONDS.value/40.0))
  }

  /**
   * Given a date returns the closest previous date to finish an update 
   * from the client, and the time between the given date and the closest
   * previous
   * @param date
   */
  def getDateFinishPreviousUpdate(date: LocalDateTime): Tuple2[LocalDateTime, TimeSeconds] = {
    val minusTime = TimeSeconds(MAX_DIFF_SECONDS.value/40.0)
    (dateMinusSeconds(date, minusTime), minusTime) 
  }

  /**
   * Given a state and an update from the client checks the consistency of both, returning false if there is not consistency
   * @param state
   * @param update
   */
  def checkStateUpdate(state: Option[State], update: Update): Boolean = {
   val flag1 = ((!state.isDefined && update.endDate.isDefined) || (state.isDefined && !update.endDate.isDefined))
   val flag2 = (update.endDate.isDefined && update.endDate.get.isBefore(update.startDate))
   val flag3 = (state.isDefined && update.startDate.isBefore(state.get.startTime))
   val flag4 = (update.endDate.isDefined && (update.distanceCovered.value <= 0))
   val flag5 = (state.isDefined && ((state.get.lastTimeCovered.value < 0) || (state.get.lastDistanceCovered.value < 0) || (state.get.lastTariffValue.value < 0)))
   val flag6 = (state.isDefined && state.get.lastTariffTypeDate.isDefined && update.startDate.isBefore(state.get.lastTariffTypeDate.get))
   val flag7 = (state.isDefined && ((state.get.lastTariffType.isDefined && !state.get.lastTariffTypeDate.isDefined) || (!state.get.lastTariffType.isDefined && state.get.lastTariffTypeDate.isDefined)))
   !(flag1 || flag2 || flag3 || flag4 || flag5 || flag6 || flag7)
  }

  /**
   * Given an update from the customer, and a tariff type state, returns the time in seconds until the next tariff update, the distance in meters until the next tariff update,
   * the tariff value for the next tariff update, the date when the tariff update will be applied, the time covered in the client update report, the tariff being applied to the 
   * update, the difference between the distance reported in the, and the date when the next client update should start
   * client update and the distance needed for the next tariff update
   * @param update the update from the client
   * @param tariffState if it is None then the tariff to be used is TF4, otherwise the tariff to be used is calculated from update.startDate, and the state for this tariff is 'tariffState'
   */
  def getDataForUpdateTariffValue(state: State, update: Update): Either[String, Tuple8[TimeSeconds, DistanceMeters, CurrencyPence, LocalDateTime, TimeSeconds, Tariff, Double, LocalDateTime]] = {
    val updateSecs = update.endDate match {
      case Some(_) => secondsInBetweenLocalDateTime(update.startDate, update.endDate.get)
      //---------------------------------------------------------------------------
      case None => TimeSeconds(0)
    }
    if (checkStateUpdate(Some(state), update) && (updateSecs.value != 0) && (update.distanceCovered.value > 0)) {
      getTariffTypeState(state, update) match {
        case Left(error) => Left(error)
        //------------------------------------------------------
        case Right((currentTariffType, currentTariffState)) => {
          getUpdates(currentTariffType, currentTariffState) match {
            case Some((tariffUpdateDist, tariffUpdateTime, tariffUpdateValue)) => {
              val (timeTariff, distanceTariff) = getDistanceTimeForUpdate(update, tariffUpdateDist, tariffUpdateTime)
              // The difference between the distance reported in the client update and the distance needed for the next tariff update
              val diffDist = update.distanceCovered.value - distanceTariff.value
              // The date when the tariff update will be applied
              val updateDate = datePlusSeconds(update.startDate, timeTariff)
              // The date when the next client update should start
              val dateStartNextUpdate = getDateStartNextUpdate(updateDate)
              Right((timeTariff, distanceTariff, tariffUpdateValue, updateDate, updateSecs, currentTariffType, diffDist, dateStartNextUpdate))
            }
            //---------------------------------------------------------------------------------------------
            case None => Left(s"Inconsistent state $currentTariffState for tariff type $currentTariffType")
          }
        }
      }
    } else {
      // It is a bad state
      Left("In order to get data to update the tariff value, the state and the update must be consistent")
    }
  }

  /**
   * In the case that there is not enough distance to cover a tariff update value, it returnsa new update value
   * calculated for the remaining distance
   * @param state
   * @param update
   * @param timeForUpdate the time to apply the update for the current tariff type and state
   */ 
  def prorateTariffValueUpdate(state: State, update: Update, timeForUpdate: TimeSeconds): Either[String, Tuple3[CurrencyPence, DistanceMeters, Tariff]] = {
    getDataForUpdateTariffValue(state, update) match {
      case Left(error) => Left(error)
      //----------------------------------------------------------------------------------------------------------------------------------------
      case Right((timeTariff, distanceTariff, tariffUpdateValue, updateDate, updateSecs, currentTariffType, diffDist, dateStartNextUpdate)) => {
       if (timeForUpdate.value <= timeTariff.value) {
         if (timeTariff.value > 0) {
           val newTariffValue = Math.ceil((timeForUpdate.value*tariffUpdateValue.value)/timeTariff.value).toInt
           val avgVelocity = getAverageVelocity(update)
           val distToUpdate = getDistanceFromTimeVel(timeForUpdate, avgVelocity)
           Right((CurrencyPence(newTariffValue), distToUpdate, currentTariffType))
         } else {
           Left("The real time to apply a tariff update is no valid")
         }
       } else {
         Left("When prorating the time for updating the tariff value  must be equal or smaller than the real time to apply a tariff update")
       }
      }
    
    }
  }

  /**
   * Given the current state and an update from the client, returns the date that is starting to be applied the TF4 tariff,
   * the time to start to apply TF4, the date to finish the previous update, and the time between the date that is staring TF4
   * and the date to finish the previous update
   * @param state
   * @param update
   */
  def getTimeDateForTF4(state: State, update: Update): Tuple4[Option[LocalDateTime], TimeSeconds, Option[LocalDateTime], Option[TimeSeconds]] = {
    val diffMaxDist = MAX_DIST_COVERED.value - (state.lastDistanceCovered.value + update.distanceCovered.value)
    if (diffMaxDist <= 0) {
      val distToMax = DistanceMeters(update.distanceCovered.value + diffMaxDist)
      val avgVelocity = getAverageVelocity(update)
      val timeForTF4 = getTimeFromDistanceVel(distToMax, avgVelocity)
      val updateDate = datePlusSeconds(update.startDate, timeForTF4)
      val (previousDate, previousTime) = getDateFinishPreviousUpdate(updateDate)
      (Some(updateDate), timeForTF4, Some(previousDate), Some(previousTime))
    } else {
      (None, TimeSeconds(Double.MaxValue), None, None)
    }
  }

  /**
   * It returns a 'Partial Function' that is only defined for open trips.
   * This 'Partial Function' is receiving the current state and an update from the client, and it returns a sequence of tuples with the following format:
   */ 
  def getDateTimesForNewState(): PartialFunction[Tuple2[State, Update], Either[String, Seq[(Int, Option[LocalDateTime], TimeSeconds, Option[LocalDateTime], Option[CurrencyPence], Option[DistanceMeters], Option[Tariff], Option[TimeSeconds])]]] = {
    // 'Partial Function' definition with a 'case' expression
    case (state, update) if (state.isOpen) => {
      getDataForUpdateTariffValue(state, update) match {
        case Left(error) => Left(error)
        //----------------------------------------------------------------------------------------------------------------------------------------
        case Right((timeTariff, distanceTariff, tariffUpdateValue, updateDate, updateSecs, currentTariffType, diffDist, dateStartNextUpdate)) => {
          // **************
          // *** TIME-1 ***
          // **************
          /**
           * Date and time until the end of the client update
           */
          val items = Seq((-1, update.endDate, updateSecs, Some(dateStartNextUpdate), Some(tariffUpdateValue), Some(distanceTariff), Some(currentTariffType), None))
          state.lastTariffType match {
            case Some(TF4) => {
              // *************
              // *** TIME0 ***
              // *************
              /**
               * It is the steady tariff, i.e. once in TF4 there are not more tariff changes.
               * Date and time for tariff TF4 value update
               */
               Right(items ++ Seq((0,  Some(updateDate), timeTariff, Some(dateStartNextUpdate), Some(tariffUpdateValue), Some(distanceTariff), Some(currentTariffType), None)))
            }
            //----------------
            case _ => {
              // *************
              // *** TIME1 ***
              // *************
              /**
               * Date and time for tariff change that is not to TF4
               * The tariff can change because there is an interval change, or a day change. But with the current tariff
               * types, the change of a day ocurrs while is active T3, and it is the same for all the days types until
               * 05:00am. So it is only needed to check for interval changes.
               */
              val (date1, time1) = DayInterval.getTimeToStartNextIntervalType(update.startDate)
              val tariffDate1 = Tariff.getTariffType(date1)
              val item1 = if (!state.lastTariffType.isDefined || (state.lastTariffType.get != tariffDate1)) Some((1, Some(date1), time1, None, None, None, None, None)) else None
              // *************
              // *** TIME2 ***
              // *************
              /**
               * Date and time for tariff value update for the current tariff (i.e. 'currentTatiffType')
               */
              val item2 = (2, Some(updateDate), timeTariff, Some(dateStartNextUpdate), Some(tariffUpdateValue), Some(distanceTariff), Some(currentTariffType), None)
              // *************
              // *** TIME3 ***
              // *************
              /**
               * Date and time for starting to be applied TF4 tariff
               */
              val (date3, time3, nextStart3, nextTime3_aux) = getTimeDateForTF4(state, update)
              val item3 = (3, date3, time3, nextStart3, None, None, None, nextTime3_aux)
              // Sequence with the times for the different events. It is happening the one with
              // the minimum time 
              Right(items ++ Seq(item2, item3) ++ item1)
            }
          }
        }
      }
    }
  }

  /**
   * Using the current state and update, it is creating a new state and a possible new update for the client
   * @param currentState
   * @param update
   * @param timeForUpdate the time until the tariff value update that is creating the new state
   * @param distForUpdate the distance until the tariff value update that is creating the new state
   * @param tariffValue the tariff value update that is creating the new state
   * @param endUpdateDate the tariff value update date that is creating the new state
   * @param startNewUpdateDate if a new update from the client is created, then it is the start date for this update
   * @param tariffForUpdate the tariff being applied in the tariff value update that is creating the new value
   */
  def getStateUpdate(currentState: State, update: Update, timeForUpdate: TimeSeconds, distForUpdate: DistanceMeters, tariffValue: CurrencyPence, endUpdateDate: LocalDateTime, startNewUpdateDate: LocalDateTime, tariffForUpdate: Tariff): Option[Tuple3[Boolean, Option[State], Option[Update]]] = {
    val newLastTimeCovered = TimeSeconds(currentState.lastTimeCovered.value + timeForUpdate.value)
    val newLastDistanceCovered = DistanceMeters(currentState.lastDistanceCovered.value + distForUpdate.value)
    val newLastTariffValue = CurrencyPence(currentState.lastTariffValue.value + tariffValue.value)
    val newState = currentState.copy(lastTimeCovered = newLastTimeCovered, lastDistanceCovered = newLastDistanceCovered, lastTariffValue = newLastTariffValue, lastTariffType = Some(tariffForUpdate), lastTariffTypeDate = Some(endUpdateDate))
    val newUpdateTime = update.endDate match {
      case Some(_) => secondsInBetweenLocalDateTime(startNewUpdateDate, update.endDate.get)
      //----------------------------------------------------------------------------------
      case None => TimeSeconds(0)
    }
    newUpdateTime.value match {
      case 0 => None
      //------------
      case _ => {
        if (newUpdateTime.value > MAX_DIFF_SECONDS.value/40.0) {
          val newDistanceCovered = DistanceMeters(update.distanceCovered.value - distForUpdate.value)
          val newUpdate = update.copy(startDate = startNewUpdateDate, endDate = update.endDate, distanceCovered = newDistanceCovered)
          Some((true, Some(newState), Some(newUpdate)))
        } else {
          Some((false, Some(newState.copy(isOpen = !update.isEnd)), None))
        }
      }
    }
  }

  /**
   * Given a trip state and an update from a client, returns a new state calculated as a function of the given state and the update from the client.
   * If it is not possible to fully process the update from the client, then a 'Remain' is created and returned to the client. This remain should be
   * used by the client to show a prorated tariff value (but it is not a real tariff value update, because the state is the same), and add the 
   * 'Remain.remain' update to a new update
   * @param state
   * @param update
   */
  @tailrec
  def getNewState(state: Option[State], update: Update): Either[String, Tuple2[State, Option[Remain]]] = {
    if (checkStateUpdate(state, update)) {
      (state, update.endDate) match {
        // The trip is just starting
        case (None, None) => Right((State(update.startDate, TimeSeconds(0), DistanceMeters(0), CurrencyPence(0), None, None, true), None))
        //---------------------------------------------------------------------------------------------------------------------------------------------
        // The trip is going on
        case (Some(currentState), Some(_)) => {
          // Apply the 'Partial Function' converting its result to Option
          getDateTimesForNewState().lift(currentState, update) match {
            case None => Left("It is possible to work only with open trips")
            //-------------------------------------------------------------
            case Some(x) => {
              x match {
                case Left(error) => Left(error)
                //-----------------------------
                case Right(s) => {
                  // Select the minimum time
                  s.minBy(x => x._3.value) match {
                    // It is the case when the update from the client does not has enough information to update the tariff value
                    case (-1, Some(endDate), updateSecs, Some(dateStartNextUpdate), Some(tariffUpdateValue), Some(distanceTariff), Some(currentTariffType), None) => {
                      prorateTariffValueUpdate(currentState, update, updateSecs) match {
                        case Left(error) => Left(error)
                        //----------------------------------------------------------------------
                        case Right((proratedTariffVal, distForUpdate, tariffTypeForUpdate)) => { 
                          if (update.isEnd) {
                            // It is the case where the client is finishing the trip, so it is needed to force an update value
                            val newLastTimeCovered = TimeSeconds(currentState.lastTimeCovered.value + updateSecs.value)
                            val newLastDistanceCovered = DistanceMeters(currentState.lastDistanceCovered.value + distForUpdate.value)
                            val newLastTariffValue = CurrencyPence(currentState.lastTariffValue.value + proratedTariffVal.value)
                            val newState = currentState.copy(lastTimeCovered = newLastTimeCovered, lastDistanceCovered = newLastDistanceCovered, lastTariffValue = newLastTariffValue, lastTariffType = Some(currentTariffType), lastTariffTypeDate = update.endDate, isOpen = !update.isEnd)
                            Right((newState, None))
                          } else {
                            // As there is not enough information for an update of the tariff value, then there is not a new state, and a 'Remain' is created
                            Right((currentState, Some(Remain(update, proratedTariffVal))))
                          }
                        }
                      }
                   }
                    //-----------------------------------------------------------------------------------------------------------------------------
                    case (0, Some(updateDate), timeTariff, Some(dateStartNextUpdate), Some(tariffUpdateValue), Some(distanceTariff), Some(currentTariffType), None) => {
                      getStateUpdate(currentState, update, timeTariff, distanceTariff, tariffUpdateValue, updateDate, dateStartNextUpdate, currentTariffType) match {
                        // Recursive call
                        case Some((true, Some(x), Some(y))) => getNewState(Some(x), y)
                        //---------------------------------------------------------------------------------------------
                        case Some((false, Some(x), None)) => Right(x, None)
                        //---------------------------------------------------------------------------------------------
                        case _ => Left("Inconsistent update from the client because has undefined the field 'endDate'")
                      }
                    }
                    //-----------------------------------------------------------------------------------------------------------------------------
                    case (1, Some(date1), time1, None, None, None, None, None) => {
                      val (date2, nextTime1) = getDateFinishPreviousUpdate(date1)
                      val tariffDate1 = Tariff.getTariffType(date1)
                      val time2 = TimeSeconds(time1.value - nextTime1.value)
                      prorateTariffValueUpdate(currentState, update, time2) match {
                        case Left(error) => Left(error)
                        //------------------------------------------------------------------------------------------------
                        case Right((tariffValue, distForUpdate, tariffForUpdate)) => {
                          getStateUpdate(currentState, update, time2, distForUpdate, tariffValue, date2, date1, tariffForUpdate) match {
                            // Recursive call
                            case Some((true, Some(x), Some(y))) => getNewState(Some(x), y)
                            //---------------------------------------------------------------------------------------------
                            case Some((false, Some(x), None)) => Right(x, None)
                            //---------------------------------------------------------------------------------------------
                            case _ => Left("Inconsistent update from the client because has undefined the field 'endDate'")
                          }
                        }
                      }
                    }
                    //-----------------------------------------------------------------------------------------------------------------------------
                    case (2, Some(updateDate), timeTariff, Some(dateStartNextUpdate), Some(tariffUpdateValue), Some(distanceTariff), Some(currentTariffType), None) => {
                      getStateUpdate(currentState, update, timeTariff, distanceTariff, tariffUpdateValue, updateDate, dateStartNextUpdate, currentTariffType) match {
                        // Recursive call
                        case Some((true, Some(x), Some(y))) => getNewState(Some(x), y)
                        //---------------------------------------------------------------------------------------------
                        case Some((false, Some(x), None)) => Right(x, None)
                        //---------------------------------------------------------------------------------------------
                        case _ => Left("Inconsistent update from the client because has undefined the field 'endDate'")
                      }
                    }
                    //-----------------------------------------------------------------------------------------------------------------------------
                    case (3, d3, t3, ns3, None, None, None, nt3)  => {
                      (d3, t3, ns3, nt3) match {
                        case (Some(date3), time3, Some(nextStart3), Some(nextTime3_aux)) => {
                          val timeForUpdate = TimeSeconds(time3.value - nextTime3_aux.value)
                          prorateTariffValueUpdate(currentState, update, timeForUpdate) match {
                             case Left(error) => Left(error)
                            //----------------------------
                            case Right((tariffValue, distForUpdate, tariffTypeForUpdate)) => {
                              getStateUpdate(currentState, update, timeForUpdate, distForUpdate, tariffValue, nextStart3, date3, TF4) match {
                                // Recursive call
                                case Some((true, Some(x), Some(y))) => getNewState(Some(x), y)
                                //---------------------------------------------------------------------------------------------
                                case Some((false, Some(x), None)) => Right(x, None)
                                //---------------------------------------------------------------------------------------------
                                case _ => Left("Inconsistent update from the client because has undefined the field 'endDate'")
                              }
                            }
                          }
                        }
                        //--------------------------------------------------------------------------------------------------------------------
                        case (_, _, _, _) => Left("There is something wrong with the case '3', it was selected when it was not possible") 
                      }
                    }
                    //-----------------------------------------------------------------------------------------------------------------------------
                    case (_, _, _, _, _, _, _, _) => Left("Unrecognized type")
                  }
                }
              }
            }
          }
        }
        //-------------------------------------------------------------------------------------------------------------------------
        // It is a bad case
        case (_, _) => Left("The only accepted None combination is both 'state' and 'update.endDate' with None")
      }
    } else {
      Left("To work with a trip both state and update must be consistent")
    }
  } 

}
