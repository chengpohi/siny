package org.siny.validator

import com.secer.elastic.model.User

/**
 * BookMark model
 * Created by chengpohi on 7/29/15.
 */
class LoginValidator extends UserValidator{
  override def valid(user: User): Boolean = {
    false
  }
}
