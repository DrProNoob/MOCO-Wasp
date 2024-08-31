package camera.Util
import kotlinx.cinterop.BetaInteropApi
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.addressOf
import kotlinx.cinterop.allocArrayOf
import kotlinx.cinterop.memScoped
import kotlinx.cinterop.usePinned
import platform.Foundation.NSData
import platform.Foundation.create
import platform.UIKit.UIImage
import platform.UIKit.UIImageJPEGRepresentation
import platform.posix.memcpy

@OptIn(ExperimentalForeignApi::class, BetaInteropApi::class)
actual fun compressImage(image: ByteArray, qualtity: Int): ByteArray {
    val data = memScoped {
        NSData.create(bytes = allocArrayOf(image), length = image.size.toULong())
    }
    val picture = UIImage(data = data)
    val compressedPicture = UIImageJPEGRepresentation(image = picture, compressionQuality = qualtity.toDouble())
    if (compressedPicture != null) {
        return compressedPicture.toByteArray()
    }
    return image
}

private typealias ImageBytes = NSData

@OptIn(ExperimentalForeignApi::class)
private fun ImageBytes.toByteArray():ByteArray = ByteArray(this@toByteArray.length.toInt()).apply {
    usePinned {
        memcpy(it.addressOf(0),this@toByteArray.bytes, this@toByteArray.length())
    }
}