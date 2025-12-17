package com.project.tapthehuzz.wear.services

import android.nfc.cardemulation.HostApduService
import android.os.Bundle
import android.util.Log
import com.project.tapthehuzz.wear.utils.WearCardNfcManager
import java.util.Arrays

class WearHostApduService : HostApduService() {

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
        Log.d("WearHCE", "Deactivated: $reason")
    }

    override fun processCommandApdu(commandApdu: ByteArray?, extras: Bundle?): ByteArray {
        if (commandApdu == null) return UNKNOWN_CMD_SW

        val hexCommand = toHex(commandApdu)
        Log.e("WearHCE_DEBUG", "Received APDU: $hexCommand")
        
        // 1. SELECT NDEF Application
        // CLA INS P1 P2 Lc [Data] [Le]
        if (hexCommand.startsWith(SELECT_APDU_HEADER)) {
            val lc = commandApdu[4].toInt() and 0xFF
            // Ensure we have enough data
            if (commandApdu.size >= 5 + lc) {
                val aid = commandApdu.copyOfRange(5, 5 + lc)
                if (Arrays.equals(aid, NDEF_ID)) {
                    // Prepare your URL here
                    val url = WearCardNfcManager.currentCardUrl ?: "https://tapthehuzz.com"
                    Log.e("WearHCE_DEBUG", "Selected NDEF App. Serving URL: $url")
                    ndefUriBytes = createNdefUri(url)
                    selectedFile = FILE_NONE
                    return SELECT_OK_SW
                }
            }
        }

        // 2. SELECT Capability Container (CC)
        if (hexCommand == "00A4000C02E103") {
            selectedFile = FILE_CC
            return SELECT_OK_SW
        }

        // 3. SELECT NDEF File
        if (hexCommand == "00A4000C02E104") {
            selectedFile = FILE_NDEF
            return SELECT_OK_SW
        }

        // 4. READ BINARY
        if (hexCommand.startsWith("00B0")) {
            val p1 = commandApdu[2].toInt() and 0xFF
            val p2 = commandApdu[3].toInt() and 0xFF
            val offset = (p1 shl 8) + p2
            
            var le = commandApdu[4].toInt() and 0xFF
            if (le == 0) le = 256 // Le=0 means 256 bytes
            
            val dataToSend = if (selectedFile == FILE_CC) CC_FILE else ndefUriBytes
            
            if (offset >= dataToSend.size) {
                return hexStringToByteArray("6A83") // Wrong parameter(s) P1 P2
            }
            
            val len = Math.min(le, dataToSend.size - offset)
            val response = ByteArray(len + 2)
            System.arraycopy(dataToSend, offset, response, 0, len)
            System.arraycopy(SELECT_OK_SW, 0, response, len, 2)
            
            return response
        }

        return UNKNOWN_CMD_SW
    }
    
    // State to track which file is selected
    private var selectedFile = 0
    private val FILE_NONE = 0
    private val FILE_CC = 1
    private val FILE_NDEF = 2

    // --- Helper Functions ---

    private fun createNdefUri(url: String): ByteArray {
        val uriPrefix: Byte
        val uriData: ByteArray
        
        if (url.startsWith("https://www.")) {
            uriPrefix = 0x02.toByte()
            uriData = url.substring(12).toByteArray(Charsets.UTF_8)
        } else if (url.startsWith("https://")) {
            uriPrefix = 0x04.toByte()
            uriData = url.substring(8).toByteArray(Charsets.UTF_8)
        } else if (url.startsWith("http://www.")) {
            uriPrefix = 0x01.toByte()
            uriData = url.substring(11).toByteArray(Charsets.UTF_8)
        } else if (url.startsWith("http://")) {
            uriPrefix = 0x03.toByte()
            uriData = url.substring(7).toByteArray(Charsets.UTF_8)
        } else {
            uriPrefix = 0x00.toByte()
            uriData = url.toByteArray(Charsets.UTF_8)
        }

        val payloadLength = uriData.size + 1
        val recordHeader = byteArrayOf(0xD1.toByte(), 0x01.toByte(), payloadLength.toByte(), 0x55.toByte())
        
        val fullPayload = recordHeader + byteArrayOf(uriPrefix) + uriData
        
        val len = fullPayload.size
        val lenBytes = byteArrayOf((len shr 8).toByte(), (len and 0xFF).toByte())
        
        // Note: We do NOT append SELECT_OK_SW here because it's file content, not an APDU response.
        // The SW is appended in processCommandApdu when returning the data.
        return lenBytes + fullPayload
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
