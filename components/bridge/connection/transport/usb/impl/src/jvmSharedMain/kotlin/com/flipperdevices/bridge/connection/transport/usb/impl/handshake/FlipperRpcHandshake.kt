package com.flipperdevices.bridge.connection.transport.usb.impl.handshake

import com.flipperdevices.bridge.connection.transport.usb.impl.model.USBPlatformDevice
import com.flipperdevices.core.log.LogTagProvider
import com.flipperdevices.core.log.info

private val CLI_PROMPT = "\r\n\r\n>: ".toByteArray()
private val START_RPC_SESSION_COMMAND = "start_rpc_session\r".toByteArray()
private val COMMAND_ECHO_TERMINATOR = "\n".toByteArray()

/**
 * Switches a Flipper CLI session into RPC mode: waits for the CLI prompt
 * after the welcome banner, sends `start_rpc_session` and waits for the
 * command echo to finish.
 */
class FlipperRpcHandshake : LogTagProvider {
    override val TAG = "FlipperRpcHandshake"

    /**
     * Reads from [device] until [terminator] is found, starting with the
     * already-read [initialChunk]. Returns the bytes that arrived in the
     * same chunk right after the terminator - they belong to the next
     * protocol phase and must not be dropped.
     */
    private suspend fun skipUntilTerminator(
        device: USBPlatformDevice,
        terminator: ByteArray,
        initialChunk: ByteArray
    ): Result<ByteArray> {
        val matcher = ByteSequenceMatcher(terminator)
        var chunk = initialChunk
        while (true) {
            if (chunk.isNotEmpty()) {
                val indexAfterMatch = matcher.indexAfterMatch(chunk)
                if (indexAfterMatch >= 0) {
                    return Result.success(chunk.copyOfRange(indexAfterMatch, chunk.size))
                }
            }
            chunk = device.read()
                .getOrElse { readError -> return Result.failure(readError) }
        }
    }

    /**
     * On success returns the bytes that arrived right after the handshake
     * finished - the first bytes of the RPC stream.
     */
    suspend fun enterRpcMode(device: USBPlatformDevice): Result<ByteArray> {
        info { "Waiting for CLI prompt" }
        val afterPrompt = skipUntilTerminator(device, CLI_PROMPT, ByteArray(size = 0))
            .getOrElse { promptError -> return Result.failure(promptError) }
        info { "CLI prompt received, requesting RPC session" }
        device.write(START_RPC_SESSION_COMMAND)
            .getOrElse { writeError -> return Result.failure(writeError) }
        return skipUntilTerminator(device, COMMAND_ECHO_TERMINATOR, afterPrompt)
            .onSuccess { info { "RPC session started" } }
    }
}
