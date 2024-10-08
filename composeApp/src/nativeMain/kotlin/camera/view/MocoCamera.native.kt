package camera.view

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.interop.UIKitView
import androidx.compose.ui.viewinterop.UIKitInteropProperties
import androidx.compose.ui.viewinterop.UIKitView
import kotlinx.cinterop.BetaInteropApi
import kotlinx.cinterop.CValue
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.ObjCAction
import kotlinx.cinterop.addressOf
import kotlinx.cinterop.sizeOf
import kotlinx.cinterop.useContents
import kotlinx.cinterop.usePinned
import platform.AVFoundation.AVCaptureConnection
import platform.AVFoundation.AVCaptureDevice
import platform.AVFoundation.AVCaptureDeviceDiscoverySession.Companion.discoverySessionWithDeviceTypes
import platform.AVFoundation.AVCaptureDeviceInput
import platform.AVFoundation.AVCaptureDeviceInput.Companion.deviceInputWithDevice
import platform.AVFoundation.AVCaptureDevicePositionBack
import platform.AVFoundation.AVCaptureDevicePositionFront
import platform.AVFoundation.AVCaptureDeviceTypeBuiltInDualCamera
import platform.AVFoundation.AVCaptureDeviceTypeBuiltInDualWideCamera
import platform.AVFoundation.AVCaptureDeviceTypeBuiltInDuoCamera
import platform.AVFoundation.AVCaptureDeviceTypeBuiltInUltraWideCamera
import platform.AVFoundation.AVCaptureDeviceTypeBuiltInWideAngleCamera
import platform.AVFoundation.AVCaptureFlashModeOff
import platform.AVFoundation.AVCaptureFlashModeOn
import platform.AVFoundation.AVCaptureInput
import platform.AVFoundation.AVCaptureOutput
import platform.AVFoundation.AVCapturePhoto
import platform.AVFoundation.AVCapturePhotoCaptureDelegateProtocol
import platform.AVFoundation.AVCapturePhotoOutput
import platform.AVFoundation.AVCapturePhotoSettings
import platform.AVFoundation.AVCaptureSession
import platform.AVFoundation.AVCaptureSessionPresetPhoto
import platform.AVFoundation.AVCaptureVideoDataOutput
import platform.AVFoundation.AVCaptureVideoDataOutputSampleBufferDelegateProtocol
import platform.AVFoundation.AVCaptureVideoOrientationLandscapeLeft
import platform.AVFoundation.AVCaptureVideoOrientationLandscapeRight
import platform.AVFoundation.AVCaptureVideoOrientationPortrait
import platform.AVFoundation.AVCaptureVideoPreviewLayer
import platform.AVFoundation.AVLayerVideoGravityResizeAspectFill
import platform.AVFoundation.AVMediaTypeVideo
import platform.AVFoundation.AVVideoCodecKey
import platform.AVFoundation.AVVideoCodecTypeJPEG
import platform.AVFoundation.fileDataRepresentation
import platform.AVFoundation.position
import platform.CoreGraphics.CGRect
import platform.CoreGraphics.CGRectMake
import platform.CoreMedia.CMSampleBufferGetImageBuffer
import platform.CoreMedia.CMSampleBufferRef
import platform.CoreMedia.kCMPixelFormat_32BGRA
import platform.CoreVideo.CVPixelBufferGetBaseAddress
import platform.CoreVideo.CVPixelBufferGetDataSize
import platform.CoreVideo.CVPixelBufferLockBaseAddress
import platform.CoreVideo.CVPixelBufferUnlockBaseAddress
import platform.CoreVideo.kCVPixelBufferPixelFormatTypeKey
import platform.Foundation.NSData
import platform.Foundation.NSError
import platform.Foundation.NSNotification
import platform.Foundation.NSNotificationCenter
import platform.Foundation.NSSelectorFromString
import platform.Foundation.dataWithBytes
import platform.QuartzCore.CATransaction
import platform.QuartzCore.kCATransactionDisableActions
import platform.UIKit.UIDevice
import platform.UIKit.UIDeviceOrientation
import platform.UIKit.UIGraphicsBeginImageContextWithOptions
import platform.UIKit.UIGraphicsEndImageContext
import platform.UIKit.UIGraphicsGetImageFromCurrentImageContext
import platform.UIKit.UIImage
import platform.UIKit.UIImageOrientation
import platform.UIKit.UIImagePNGRepresentation
import platform.UIKit.UIView
import platform.darwin.DISPATCH_QUEUE_PRIORITY_DEFAULT
import platform.darwin.NSObject
import platform.darwin.dispatch_async
import platform.darwin.dispatch_get_global_queue
import platform.darwin.dispatch_get_main_queue
import platform.darwin.dispatch_group_create
import platform.darwin.dispatch_group_enter
import platform.darwin.dispatch_group_leave
import platform.darwin.dispatch_group_notify
import platform.darwin.dispatch_queue_create
import platform.posix.memcpy

private val deviceTypes =
    listOf(
        AVCaptureDeviceTypeBuiltInWideAngleCamera,
        AVCaptureDeviceTypeBuiltInDualWideCamera,
        AVCaptureDeviceTypeBuiltInDualCamera,
        AVCaptureDeviceTypeBuiltInUltraWideCamera,
        AVCaptureDeviceTypeBuiltInDuoCamera,
    )

@Composable
private fun AuthorizedCamera(
    state: MocoCameraState,
    modifier: Modifier = Modifier,
) {
    val camera: AVCaptureDevice? =
        remember {
            discoverySessionWithDeviceTypes(
                deviceTypes = deviceTypes,
                mediaType = AVMediaTypeVideo,
                position =
                when (state.cameraMode) {
                    CameraMode.Front -> AVCaptureDevicePositionFront
                    CameraMode.Back -> AVCaptureDevicePositionBack
                    else -> {
                        AVCaptureDevicePositionBack
                    }
                },
            ).devices.firstOrNull() as? AVCaptureDevice
        }

    if (camera != null) {
        RealDeviceCamera(
            state = state,
            camera = camera,
            modifier = modifier,
        )
    } else {
        Text(
            "Camera is not available on simulator. Please try to run on a real iOS device.",
            color = Color.White,
        )
    }

    if (!state.isCameraReady) {
        Box(
            modifier =
            Modifier
                .fillMaxSize()
                .background(Color.Black),
        )
    }
}


@OptIn(ExperimentalForeignApi::class)
@Composable
private fun RealDeviceCamera(
    state: MocoCameraState,
    camera: AVCaptureDevice,
    modifier: Modifier,
) {
    val queue =
        remember {
            dispatch_get_global_queue(DISPATCH_QUEUE_PRIORITY_DEFAULT.toLong(), 0UL)
        }
    val capturePhotoOutput = remember { AVCapturePhotoOutput() }
    val videoOutput = remember { AVCaptureVideoDataOutput() }

    val photoCaptureDelegate =
        remember(state) { PhotoCaptureDelegate(state::stopCapturing, state::onCapture) }

    val frameAnalyzerDelegate =
        remember {
            CameraFrameAnalyzerDelegate(state.onFrame)
        }

    val triggerCapture: () -> Unit = {
        val photoSettings =
            AVCapturePhotoSettings.photoSettingsWithFormat(
                format = mapOf(pair = AVVideoCodecKey to AVVideoCodecTypeJPEG),
            ).apply {
                flashMode = if (state.isTorchOn) AVCaptureFlashModeOn else AVCaptureFlashModeOff
            }
        if (camera.position == AVCaptureDevicePositionFront) {
            capturePhotoOutput.connectionWithMediaType(AVMediaTypeVideo)
                ?.automaticallyAdjustsVideoMirroring = false
            capturePhotoOutput.connectionWithMediaType(AVMediaTypeVideo)
                ?.videoMirrored = true
        }
        capturePhotoOutput.capturePhotoWithSettings(
            settings = photoSettings,
            delegate = photoCaptureDelegate,
        )
    }

    SideEffect {
        state.triggerCaptureAnchor = triggerCapture
    }

    val captureSession: AVCaptureSession =
        remember {
            AVCaptureSession().also { captureSession ->
                captureSession.sessionPreset = AVCaptureSessionPresetPhoto
                val captureDeviceInput: AVCaptureDeviceInput =
                    deviceInputWithDevice(device = camera, error = null)!!
                captureSession.addInput(captureDeviceInput)
                captureSession.addOutput(capturePhotoOutput)

                if (captureSession.canAddOutput(videoOutput)) {
                    val captureQueue = dispatch_queue_create("sampleBufferQueue", attr = null)
                    videoOutput.setSampleBufferDelegate(frameAnalyzerDelegate, captureQueue)
                    videoOutput.alwaysDiscardsLateVideoFrames = true
                    videoOutput.videoSettings =
                        mapOf(
                            kCVPixelBufferPixelFormatTypeKey to kCMPixelFormat_32BGRA,
                        )
                    captureSession.addOutput(videoOutput)
                }
            }
        }

    val cameraPreviewLayer =
        remember {
            AVCaptureVideoPreviewLayer(session = captureSession)
        }


    // Update captureSession with new camera configuration whenever isFrontCamera changed.
    LaunchedEffect(state.cameraMode) {
        val dispatchGroup = dispatch_group_create()
        captureSession.beginConfiguration()
        captureSession.inputs.forEach { captureSession.removeInput(it as AVCaptureInput) }

        val newCamera =
            discoverySessionWithDeviceTypes(
                deviceTypes,
                AVMediaTypeVideo,
                if (state.cameraMode == CameraMode.Front) AVCaptureDevicePositionFront else AVCaptureDevicePositionBack,
            ).devices.firstOrNull() as? AVCaptureDevice

        newCamera?.let {
            val newInput =
                AVCaptureDeviceInput.deviceInputWithDevice(it, error = null) as AVCaptureDeviceInput
            if (captureSession.canAddInput(newInput)) {
                captureSession.addInput(newInput)
            }
        }



        captureSession.commitConfiguration()

        dispatch_group_enter(dispatchGroup)
        dispatch_async(queue) {
            captureSession.startRunning()
            dispatch_group_leave(dispatchGroup)
        }

        dispatch_group_notify(dispatchGroup, dispatch_get_main_queue()) {
            state.onCameraReady()
        }
    }

    DisposableEffect(cameraPreviewLayer, capturePhotoOutput, videoOutput, state) {
        val listener = OrientationListener(cameraPreviewLayer, capturePhotoOutput, videoOutput)
        val notificationName = platform.UIKit.UIDeviceOrientationDidChangeNotification
        NSNotificationCenter.defaultCenter.addObserver(
            observer = listener,
            selector =
            NSSelectorFromString(
                OrientationListener::orientationDidChange.name + ":",
            ),
            name = notificationName,
            `object` = null,
        )
        onDispose {
            state.triggerCaptureAnchor = null
            NSNotificationCenter.defaultCenter.removeObserver(
                observer = listener,
                name = notificationName,
                `object` = null,
            )
        }
    }

/*    UIKitView(
        modifier = modifier,
        background = Color.Black,
        factory = {
            val dispatchGroup = dispatch_group_create()
            val cameraContainer = UIView()
            cameraContainer.layer.addSublayer(cameraPreviewLayer)
            cameraPreviewLayer.videoGravity = AVLayerVideoGravityResizeAspectFill
            dispatch_group_enter(dispatchGroup)
            dispatch_async(queue) {
                captureSession.startRunning()
                dispatch_group_leave(dispatchGroup)
            }
            dispatch_group_notify(dispatchGroup, dispatch_get_main_queue()) {
                state.onCameraReady()
            }
            cameraContainer
        },
        onResize = { view: UIView, rect: CValue<CGRect> ->
            CATransaction.begin()
            CATransaction.setValue(true, kCATransactionDisableActions)
            view.layer.setFrame(rect)
            cameraPreviewLayer.setFrame(rect)
            CATransaction.commit()
        },
    )*/

    UIKitView<CameraContainerView>(
        factory = {
            val dispatchGroup = dispatch_group_create()
            val cameraContainer = CameraContainerView(cameraPreviewLayer)
            cameraContainer.layer.addSublayer(cameraPreviewLayer)
            cameraPreviewLayer.videoGravity = AVLayerVideoGravityResizeAspectFill
            dispatch_group_enter(dispatchGroup)
            dispatch_async(queue) {
                captureSession.startRunning()
                dispatch_group_leave(dispatchGroup)
            }
            dispatch_group_notify(dispatchGroup, dispatch_get_main_queue()) {
                state.onCameraReady()
            }
            cameraContainer
        },
        modifier = modifier,
        properties = UIKitInteropProperties(
            isInteractive = true,
            isNativeAccessibilityEnabled = true
        )
    )
}

class OrientationListener(
    private val cameraPreviewLayer: AVCaptureVideoPreviewLayer,
    private val capturePhotoOutput: AVCapturePhotoOutput,
    private val videoOutput: AVCaptureVideoDataOutput,
) : NSObject() {
    @OptIn(BetaInteropApi::class)
    @Suppress("UNUSED_PARAMETER")
    @ObjCAction
    fun orientationDidChange(arg: NSNotification) {
        val cameraConnection = cameraPreviewLayer.connection
        val actualOrientation =
            when (UIDevice.currentDevice.orientation) {
                UIDeviceOrientation.UIDeviceOrientationPortrait ->
                    AVCaptureVideoOrientationPortrait

                UIDeviceOrientation.UIDeviceOrientationLandscapeLeft ->
                    AVCaptureVideoOrientationLandscapeRight

                UIDeviceOrientation.UIDeviceOrientationLandscapeRight ->
                    AVCaptureVideoOrientationLandscapeLeft

                UIDeviceOrientation.UIDeviceOrientationPortraitUpsideDown ->
                    AVCaptureVideoOrientationPortrait

                else -> cameraConnection?.videoOrientation ?: AVCaptureVideoOrientationPortrait
            }
        if (cameraConnection != null) {
            cameraConnection.videoOrientation = actualOrientation
        }
        capturePhotoOutput.connectionWithMediaType(AVMediaTypeVideo)
            ?.videoOrientation = actualOrientation
        videoOutput.connectionWithMediaType(AVMediaTypeVideo)
            ?.videoOrientation = actualOrientation
    }
}

class CameraFrameAnalyzerDelegate(
    private val onFrame: ((frame: ByteArray) -> Unit)?,
) : NSObject(), AVCaptureVideoDataOutputSampleBufferDelegateProtocol {
    @OptIn(ExperimentalForeignApi::class)
    override fun captureOutput(
        output: AVCaptureOutput,
        @Suppress("PARAMETER_NAME_CHANGED_ON_OVERRIDE")
        didOutputSampleBuffer: CMSampleBufferRef?,
        fromConnection: AVCaptureConnection,
    ) {
        if (onFrame == null) return

        val imageBuffer = CMSampleBufferGetImageBuffer(didOutputSampleBuffer) ?: return
        CVPixelBufferLockBaseAddress(imageBuffer, 0uL)
        val baseAddress = CVPixelBufferGetBaseAddress(imageBuffer)
        val bufferSize = CVPixelBufferGetDataSize(imageBuffer)
        val data = NSData.dataWithBytes(bytes = baseAddress, length = bufferSize)
        CVPixelBufferUnlockBaseAddress(imageBuffer, 0uL)

        val bytes = data.toByteArray()
        onFrame.invoke(bytes)
    }
}

class PhotoCaptureDelegate(
    private val onCaptureEnd: () -> Unit,
    private val onCapture: (byteArray: ByteArray?) -> Unit,
) : NSObject(), AVCapturePhotoCaptureDelegateProtocol {
    @OptIn(ExperimentalForeignApi::class)
    override fun captureOutput(
        output: AVCapturePhotoOutput,
        didFinishProcessingPhoto: AVCapturePhoto,
        error: NSError?,
    ) {
        val photoData = didFinishProcessingPhoto.fileDataRepresentation()
        if (photoData != null) {
            var uiImage = UIImage(photoData)
            if (uiImage.imageOrientation != UIImageOrientation.UIImageOrientationUp) {
                UIGraphicsBeginImageContextWithOptions(
                    uiImage.size,
                    false,
                    uiImage.scale,
                )
                uiImage.drawInRect(
                    CGRectMake(
                        x = 0.0,
                        y = 0.0,
                        width = uiImage.size.useContents { width },
                        height = uiImage.size.useContents { height },
                    ),
                )
                val normalizedImage = UIGraphicsGetImageFromCurrentImageContext()
                UIGraphicsEndImageContext()
                uiImage = normalizedImage!!
            }
            val imageData = UIImagePNGRepresentation(uiImage)
            val byteArray: ByteArray? = imageData?.toByteArray()
            onCapture(byteArray)
        }
        onCaptureEnd()
    }
}

@OptIn(ExperimentalForeignApi::class)
private inline fun NSData.toByteArray(): ByteArray {
    val size = length.toInt()
    val byteArray = ByteArray(size)
    if (size > 0) {
        byteArray.usePinned { pinned ->
            memcpy(pinned.addressOf(0), this.bytes, this.length)
        }
    }
    return byteArray
}

@Composable
actual fun MocoCamera(state: MocoCameraState, modifier: Modifier) {
    Box(
        modifier = modifier.fillMaxSize().background(Color.Black),
    ) {
        AuthorizedCamera(state,Modifier.fillMaxSize())
    }
}

@OptIn(ExperimentalForeignApi::class)
class CameraContainerView(
    private val cameraPreviewLayer: AVCaptureVideoPreviewLayer
) : UIView(frame = CGRectMake(0.0, 0.0, sizeOf<CGRect>().toDouble(), sizeOf<CGRect>().toDouble())) {
    override fun layoutSubviews() {
        super.layoutSubviews()
        cameraPreviewLayer.frame = this.bounds
    }
}