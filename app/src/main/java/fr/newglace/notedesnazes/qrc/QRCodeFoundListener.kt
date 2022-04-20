package fr.newglace.notedesnazes.qrc

interface QRCodeFoundListener {
    fun onQRCodeFound(qrCodeString: String?)
    fun qrCodeNotFound()
}