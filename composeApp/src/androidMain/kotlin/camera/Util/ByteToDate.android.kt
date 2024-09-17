package camera.Util

import dev.gitlive.firebase.storage.Data

actual fun byteToData(image: ByteArray):Data {
    return Data(image)
}