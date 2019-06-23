package fcm

interface FCMTokenInterface {
    fun onTokenReceived(token: String)
    fun onFailure()
}