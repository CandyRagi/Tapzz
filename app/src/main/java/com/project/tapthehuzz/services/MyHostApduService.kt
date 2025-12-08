package com.project.tapthehuzz.services

import android.nfc.cardemulation.HostApduService
import android.os.Bundle
import android.util.Log
import com.project.tapthehuzz.utils.CardNfcManager
import java.util.Arrays

class MyHostApduService : HostApduService() {

    // Standard APDU Commands
    private val SELECT_APDU_HEADER = "00A40400"
    private val SELECT_OK_SW = hexStringToByteArray("9000")
    private val UNKNOWN_CMD_SW = hexStringToByteArray("0000")

    // The NDEF Application ID (D2760000850101)
    private val NDEF_ID = hexStringToByteArray("D2760000850101")
    
    // Capability Container (CC) file
    private val CC_FILE = hexStringToByteArray("000F20003B00340406E10404000000")
    
    // The actual NDEF file containing your URL
    private var ndefUriBytes = byteArrayOf()

    override fun onDeactivated(reason: Int) {
        Log.d("HCE", "Deactivated: $reason")
    }

    override fun processCommandApdu(commandApdu: ByteArray?, extras: Bundle?): ByteArray {
        if (commandApdu == null) return UNKNOWN_CMD_SW

        val hexCommand = toHex(commandApdu)
        
        // 1. SELECT NDEF Application
        if (hexCommand.startsWith(SELECT_APDU_HEADER) && Arrays.equals(commandApdu.copyOfRange(5, commandApdu.size - 1), NDEF_ID)) {
            // Prepare your URL here
            val url = CardNfcManager.currentCardUrl ?: "https://tapthehuzz.com" // Default or fallback
            ndefUriBytes = createNdefUri(url)
            return SELECT_OK_SW
        }

        // 2. SELECT Capability Container (CC)
        if (hexCommand == "00A4000C02E103") {
            return SELECT_OK_SW
        }

        // 3. SELECT NDEF File
        if (hexCommand == "00A4000C02E104") {
            return SELECT_OK_SW
        }

        // 4. READ BINARY
        if (hexCommand.startsWith("00B0")) {
            val length = commandApdu[4].toInt()
            
            if (length == 15) { 
                return CC_FILE 
            } else {
                return ndefUriBytes 
            }
        }

        return UNKNOWN_CMD_SW
    }

    // --- Helper Functions ---

    private fun createNdefUri(url: String): ByteArray {
        val urlBytes = url.toByteArray(Charsets.UTF_8)
        val recordHeader = byteArrayOf(0xD1.toByte(), 0x01.toByte(), (urlBytes.size + 1).toByte(), 0x55.toByte())
        val uriIdentifier = byteArrayOf(0x00.toByte()) // 0x00 = No prefix
        
        val fullPayload = recordHeader + uriIdentifier + urlBytes
        
        val len = fullPayload.size
        val lenBytes = byteArrayOf((len shr 8).toByte(), (len and 0xFF).toByte())
        
        return lenBytes + fullPayload + SELECT_OK_SW
    }

    private fun hexStringToByteArray(s: String): ByteArray {
        val len = s.length
        val data = ByteArray(len / 2)
        var i = 0
        while (i < len) {
            data[i / 2] = ((Character.digit(s[i], 16) shl 4) + Character.digit(s[i + 1], 16)).toByte()
            i += 2
        }
        return data
    }

    private fun toHex(bytes: ByteArray): String {
        val sb = StringBuilder()
        for (b in bytes) sb.append(String.format("%02X", b))
        return sb.toString()
    }
}
