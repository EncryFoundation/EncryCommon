package org.encryfoundation.common.modifiers.state.box

import org.encryfoundation.common.modifiers.state.box.Box.Amount

trait MonetaryBox extends EncryBaseBox {

  val amount: Amount
}
