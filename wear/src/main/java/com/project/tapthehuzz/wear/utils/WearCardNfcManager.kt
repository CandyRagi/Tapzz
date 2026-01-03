package com.project.tapthehuzz.wear.utils

import android.app.Activity
import android.content.ComponentName
import android.content.Context
import android.nfc.NfcAdapter
import android.nfc.cardemulation.CardEmulation
import android.util.Log
import com.project.tapthehuzz.wear.services.WearHostApduService

object WearCardNfcManager {
    private const val TAG = "WearCardNfcManager"
    
    var currentCardUrl: String? = null
        set(value) {
            field = value
            Log.d(TAG, "Current card URL set to: $value")
        }
    
    /**
     * Enable HCE and set your service as preferred
     * Call this when the transmission screen is shown
     */
    fun enableCardEmulation(activity: Activity) {
        val nfcAdapter = NfcAdapter.getDefaultAdapter(activity)
        
        if (nfcAdapter == null) {
            Log.e(TAG, "NFC not available on this device")
            return
        }
        
        if (!nfcAdapter.isEnabled) {
            Log.w(TAG, "NFC is disabled. User needs to enable it in Settings.")
            return
        }
        
        val cardEmulation = CardEmulation.getInstance(nfcAdapter)
        val service = ComponentName(activity, WearHostApduService::class.java)
        
        // Check if HCE is supported
        if (!cardEmulation.isDefaultServiceForCategory(service, CardEmulation.CATEGORY_OTHER)) {
            Log.d(TAG, "Setting service as default for CATEGORY_OTHER")
            
            // Request to make this service the default
            // Note: User may need to approve this in Settings
            cardEmulation.setPreferredService(activity, service)
        }
        
        Log.d(TAG, "Card emulation enabled. Watch is now acting as NFC tag.")
    }
    
    /**
     * Disable preferred service when transmission screen is dismissed
     */
    fun disableCardEmulation(activity: Activity) {
        val nfcAdapter = NfcAdapter.getDefaultAdapter(activity) ?: return
        val cardEmulation = CardEmulation.getInstance(nfcAdapter)
        
        cardEmulation.unsetPreferredService(activity)
        currentCardUrl = null
        
        Log.d(TAG, "Card emulation disabled")
    }
    
    /**
     * Check if NFC is available and enabled
     */
    fun isNfcAvailable(context: Context): Boolean {
        val nfcAdapter = NfcAdapter.getDefaultAdapter(context)
        return nfcAdapter != null && nfcAdapter.isEnabled
    }
}
