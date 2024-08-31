package camera.Util

import android.graphics.Bitmap
import androidx.compose.ui.graphics.asAndroidBitmap
import java.io.ByteArrayOutputStream

actual fun compressImage(image: ByteArray, qualtity: Int): ByteArray {
    val imagebitmap = image.toImageBitmap()
    val bitmap = imagebitmap.asAndroidBitmap()
    val output = ByteArrayOutputStream()
    bitmap.compress(Bitmap.CompressFormat.JPEG, qualtity, output)
    return output.toByteArray()
}