package camera.view

sealed class CameraMode {
    /**
     * Represents the front-facing camera mode.
     * Use this mode to utilize the device's front camera in the PeekabooCamera composable.
     */
    data object Front : CameraMode()

    /**
     * Represents the back-facing camera mode.
     * Use this mode to utilize the device's rear camera in the PeekabooCamera composable.
     */
    data object Back : CameraMode()
}

internal fun CameraMode.inverse(): CameraMode {
    return when (this) {
        CameraMode.Back -> CameraMode.Front
        CameraMode.Front -> CameraMode.Back
    }
}

internal fun CameraMode.id(): Int {
    return when (this) {
        CameraMode.Back -> 0
        CameraMode.Front -> 1
    }
}

internal fun cameraModeFromId(id: Int): CameraMode {
    return when (id) {
        0 -> CameraMode.Back
        1 -> CameraMode.Front
        else -> throw IllegalArgumentException("CameraMode with id=$id does not exists")
    }
}