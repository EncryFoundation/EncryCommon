package org.encryfoundation.common.modifiers.mempool.transaction

import org.encryfoundation.common.serialization.BytesSerializable
import org.encryfoundation.prismlang.compiler.CompiledContract.ContractHash

trait Proposition extends BytesSerializable { val contractHash: ContractHash }
