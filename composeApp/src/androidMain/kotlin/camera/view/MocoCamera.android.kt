package camera.view

import android.graphics.Bitmap
import android.graphics.Matrix
import android.view.ViewGroup
import androidx.camera.core.AspectRatio
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageProxy
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.preat.peekaboo.ui.camera.loadCameraProvider
import java.io.ByteArrayOutputStream
import java.util.concurrent.Executors


private val executor = Executors.newSingleThreadExecutor()



@Composable
private fun CameraWithGrantedPermission(
    state: MocoCameraState,
    modifier: Modifier,
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val cameraProvider: ProcessCameraProvider? by loadCameraProvider(context)

    val preview = Preview.Builder().build()
    val previewView = remember { PreviewView(context) }
    val imageCapture: ImageCapture = remember {
        ImageCapture.Builder()
            .setFlashMode(if (state.isTorchOn) ImageCapture.FLASH_MODE_ON else ImageCapture.FLASH_MODE_OFF)
            .build() }
    val backgroundExecutor = remember { Executors.newSingleThreadExecutor() }
    val imageAnalyzer =
        remember(state.onFrame) {
            state.onFrame?.let { onFrame ->
                val analyzer =
                    ImageAnalysis.Builder()
                        .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                        .setOutputImageFormat(ImageAnalysis.OUTPUT_IMAGE_FORMAT_RGBA_8888)
                        .build()

                analyzer.apply {
                    setAnalyzer(backgroundExecutor) { imageProxy ->
                        val imageBytes = imageProxy.toByteArray()
                        onFrame(imageBytes)
                    }
                }
            }
        }

    val cameraSelector =
        remember(state.cameraMode) {
            val lensFacing =
                when (state.cameraMode) {
                    CameraMode.Front -> {
                        CameraSelector.LENS_FACING_FRONT
                    }
                    CameraMode.Back -> {
                        CameraSelector.LENS_FACING_BACK
                    }

                    else -> {
                        CameraSelector.LENS_FACING_BACK
                    }
                }
            CameraSelector.Builder().requireLensFacing(lensFacing).build()
        }

    DisposableEffect(Unit) {
        onDispose {
            cameraProvider?.unbindAll()
        }
    }

    LaunchedEffect(state.cameraMode, cameraProvider, imageAnalyzer) {
        if (cameraProvider != null) {
            state.onCameraReady()
            cameraProvider?.unbindAll()
            cameraProvider?.bindToLifecycle(
                lifecycleOwner,
                cameraSelector,
                *listOfNotNull(
                    preview,
                    imageCapture,
                    imageAnalyzer,
                ).toTypedArray(),
            )
            preview.setSurfaceProvider(previewView.surfaceProvider)
            previewView.layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT,
            )
        }
    }

    SideEffect {
        val triggerCapture = {
            imageCapture.takePicture(
                executor,
                ImageCaptureCallback(state::onCapture, state::stopCapturing),
            )
        }
        state.triggerCaptureAnchor = triggerCapture
    }

    DisposableEffect(state) {
        onDispose {
            state.triggerCaptureAnchor = null
        }
    }
    AndroidView(
        factory = { previewView },
        modifier = modifier,
    )
}

class ImageCaptureCallback(
    private val onCapture: (byteArray: ByteArray?) -> Unit,
    private val stopCapturing: () -> Unit,
) : ImageCapture.OnImageCapturedCallback() {
    override fun onCaptureSuccess(image: ImageProxy) {
        val imageBytes = image.toByteArray()
        onCapture(imageBytes)
        stopCapturing()
    }
}

private fun ImageProxy.toByteArray(): ByteArray {
    val rotationDegrees = imageInfo.rotationDegrees
    val bitmap = toBitmap()

    // Rotate the image if necessary
    val rotatedData =
        if (rotationDegrees != 0) {
            bitmap.rotate(rotationDegrees)
        } else {
            bitmap.toByteArray()
        }
    close()

    return rotatedData
}

private fun Bitmap.toByteArray(): ByteArray {
    val stream = ByteArrayOutputStream()
    this.compress(Bitmap.CompressFormat.JPEG, 100, stream)
    return stream.toByteArray()
}

private fun Bitmap.rotate(degrees: Int): ByteArray {
    val matrix = Matrix().apply { postRotate(degrees.toFloat()) }
    val rotatedBitmap = Bitmap.createBitmap(this, 0, 0, this.width, this.height, matrix, true)
    return rotatedBitmap.toByteArray()
}

@Composable
actual fun MocoCamera(state: MocoCameraState, modifier: Modifier) {
    CameraWithGrantedPermission(state = state, modifier = modifier)
}