import org.joda.time._

package object algo {

  // Maximum distance in meters (i.e. 6 miles)
  val MAX_DIST_COVERED = DistanceMeters(9656.1)
 
  // Instant to be used in the normalization for interval comparison
  val absRef = new Instant(0)  

  /**
   * Given a date returns the year, month and day
   * @param date
   */
  def getDate(date: LocalDateTime): Tuple3[Int, Int, Int] = {
    (date.getYear(), date.getMonthOfYear(), date.getDayOfMonth())
  }

  /**
   * Given a date returns the hour, minutes, seconds and milliseconds
   * @param date
   */
  def getTime(date: LocalDateTime): Tuple4[Int, Int, Int, Int] = {
    (date.getHourOfDay(), date.getMinuteOfHour(), date.getSecondOfMinute(), date.getMillisOfSecond())
  }

  /**
   * Given a local date time and a time, returns a new local date time that is the
   * given local date time with the time changed by the given time
   * @param oldDate
   * @param newTime
   */
  def changeTime(oldDate: LocalDateTime, newTime: String): LocalDateTime = {
    val (year, month, day) = getDate(oldDate)
    LocalDateTime.parse(year.toString.concat("-").concat(month.toString).concat("-").concat(day.toString).concat("T").concat(newTime))
  }

  /**
   * Given a millisecons as an Int returns its String representation
   * @param milliInt
   */
  def createStringMillis(milliInt: Int): String = {
    milliInt match {
      case x if (x/100.0 >= 1) => x.toString
      case x if (x/100.0 >= 0.1) => "0".concat(x.toString)
      case x => "00".concat(x.toString)
    }
  }

  /**
   * Given a date and seconds returns a new date that is the sum of the given date to the given seconds.
   * This version is more accurate that the function 'LocalDateTime.plusSeconds' that gives the Joda Time library
   * @param date
   * @param totalSeconds
   */
  def datePlusSeconds(date: LocalDateTime, totalSeconds: TimeSeconds): LocalDateTime = {
    val secsDiff = totalSeconds.value - totalSeconds.value.toInt
    val datePlus = date.plusSeconds(totalSeconds.value.toInt)
    val (hour, min, sec, millis) = getTime(datePlus)
    val totalMillis = math.ceil(millis + 1000*secsDiff).toInt
    val (addSecs, newMillis) = (1000 - totalMillis) match {
      case x if (x <= 0) => (1, Math.abs(x))
      //-----------------------------------
      case x => (0, totalMillis)
    }
    val newTime = hour.toString.concat(":").concat(min.toString).concat(":").concat(sec.toString).concat(".").concat(createStringMillis(newMillis))
    val newDate = changeTime(datePlus, newTime)
    newDate.plusSeconds(addSecs)
  }

  /**
   * Given a date and seconds returns a new date that is the substraction between the given date and the given seconds.
   * This version is more accurate that the function 'LocalDateTime.minusSeconds' that gives the Joda Time library
   * @param date
   * @param totalSeconds
   */
  def dateMinusSeconds(date: LocalDateTime, totalSeconds: TimeSeconds): LocalDateTime = {
    val secsDiff = totalSeconds.value - totalSeconds.value.toInt
    val dateMinus = date.minusSeconds(totalSeconds.value.toInt)
    val (hour, min, sec, millis) = getTime(dateMinus)
    val (minusSecs, newMillis) = math.floor(millis - 1000*secsDiff).toInt match {
      case x if (x >= 0) => (0, x)
      //-------------------------
      case x => (1, 1000 + x)
    }
    val newTime = hour.toString.concat(":").concat(min.toString).concat(":").concat(sec.toString).concat(".").concat(createStringMillis(newMillis))
    val newDate = changeTime(dateMinus, newTime)
    newDate.minusSeconds(minusSecs)
  }

  /**
   * Given two local times returns the difference in seconds between both times.
   * It is a more accurate version that the function 'Seconds.secondsBetween' that gives
   * the Joda Time library
   * @param startTime
   * @param endTime
   */
  def secondsInBetweenLocalTime(startTime: LocalTime, endTime: LocalTime): TimeSeconds = {
    TimeSeconds(Math.abs(startTime.getMillisOfDay() - endTime.getMillisOfDay()) / 1000.0)
  }

  /**
   * Given to local date times returns the difference between them in seconds.
   * It is a more accurate version that the function 'Seconds.secondsBetween' that gives
   * the Joda Time library 
   * @param startDate
   * @param endDate
   */
  def secondsInBetweenLocalDateTime(startDate: LocalDateTime, endDate: LocalDateTime): TimeSeconds = {
    TimeSeconds(Math.abs(startDate.toDateTime().getMillis() - endDate.toDateTime().getMillis()) / 1000.0)
  }

  /**
   * Converts a local time to the normalized date for interval comparison
   * @param localTime
   */
  def localToNormalizedDate(localTime: LocalTime): DateTime = {
    localTime.toDateTime(absRef)
  }

  /**
   * Normalize date for interval comparison
   * @param dateToNormalize
   */
  def normalizeDate(dateToNormalize: LocalDateTime): DateTime = {
    dateToNormalize.toLocalTime().toDateTime(absRef)
  }

}
