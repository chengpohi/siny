package org.siny.validator

import com.secer.elastic.model.User
/**
 * BookMark model
 * Created by chengpohi on 7/29/15.
 */
trait UserValidator {
  def valid(user: User): Boolean
}
