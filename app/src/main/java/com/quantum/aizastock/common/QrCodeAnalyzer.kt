package com.quantum.aizastock.common

import android.graphics.ImageFormat
import android.util.Log
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import com.google.zxing.*
import com.google.zxing.common.HybridBinarizer
import java.nio.ByteBuffer

private fun ByteBuffer.toByteArray(): ByteArray{
    rewind()
    val data = ByteArray(remaining())
    get(data)
    return data
}

class QrCodeAnalyzer(
    private val onQrCodesDetected: (qrCode: Result) -> Unit
): ImageAnalysis.Analyzer {

    private val yuvFormats = mutableListOf(ImageFormat.YUV_420_888)

    init {
        yuvFormats.addAll(listOf(ImageFormat.YUV_422_888, ImageFormat.YUV_444_888))
    }

    private val reader = MultiFormatReader().apply {
        val map = mapOf(
            DecodeHintType.POSSIBLE_FORMATS to arrayListOf(
                BarcodeFormat.QR_CODE,
                BarcodeFormat.CODABAR)
        )
        setHints(map)
    }

    override fun analyze(image: ImageProxy) {
        if (image.format !in yuvFormats){
            Log.e("QrCodeAnalyzer", "Expected YUV, now = ${image.format}")
            return
        }

        val data = image.planes[0].buffer.toByteArray()

        val source = PlanarYUVLuminanceSource(
            data,
            image.width,
            image.height,
            0,
            0,
            image.width,
            image.height,
            false
        )

        val binaryBitmap = BinaryBitmap(HybridBinarizer(source))
        try {
            val result = reader.decode(binaryBitmap)
            onQrCodesDetected(result)
        } catch (e: NotFoundException){
            e.printStackTrace()
        }
        image.close()
    }
}