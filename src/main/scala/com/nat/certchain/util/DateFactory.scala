package com.nat.certchain.util

import java.util.Date

class DateFactory {
  def getCurrentDate: Date = new Date()
  def getCurrentTimestamp: Long = getCurrentDate.getTime
}
