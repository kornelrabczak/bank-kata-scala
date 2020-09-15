package com.thecookiezen

import java.time.LocalDate

trait Clock {
  def now(): LocalDate
}
