package com.SIMATS.nutriai

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.FlashlightOff
import androidx.compose.material.icons.filled.FlashlightOn
import androidx.compose.material.icons.outlined.Cameraswitch
import androidx.compose.material.icons.outlined.PhotoLibrary
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.ui.viewinterop.AndroidView
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.SIMATS.nutriai.ui.theme.PrimaryGreen
import com.google.mlkit.vision.barcode.BarcodeScannerOptions
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.common.InputImage
import java.util.concurrent.Executors
import android.util.Size
import androidx.camera.core.FocusMeteringAction
import androidx.camera.core.SurfaceOrientedMeteringPointFactory
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.ui.input.pointer.pointerInput

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun ScanScreen(viewModel: NutriViewModel, onCloseClick: () -> Unit, onScanComplete: () -> Unit) {
    var isFlashOn by remember { mutableStateOf(false) }
    var scanMode by remember { mutableStateOf("BARCODE") } // Default to Barcode for direct scanning
    var isScanning by remember { mutableStateOf(false) }
    var showScanFeedback by remember { mutableStateOf(false) }
    var zoomRatio by remember { mutableStateOf(1f) }
    val context = androidx.compose.ui.platform.LocalContext.current

    val cameraPermissionState = rememberPermissionState(
        android.Manifest.permission.CAMERA
    )

    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
        onResult = { uri ->
            if (uri != null) {
                isScanning = true
                try {
                    val image = InputImage.fromFilePath(context, uri)
                    val scanner = BarcodeScanning.getClient()
                    scanner.process(image)
                        .addOnSuccessListener { barcodes: List<Barcode> ->
                            if (barcodes.isNotEmpty()) {
                                val barcode = barcodes[0].rawValue ?: ""
                                viewModel.fetchProductData(barcode) { success ->
                                    if (success) onScanComplete()
                                    else {
                                        isScanning = false
                                        viewModel.errorMessage = "No product found in this image."
                                    }
                                }
                            } else {
                                isScanning = false
                                viewModel.errorMessage = "No barcode detected in the selected image."
                            }
                        }
                        .addOnFailureListener {
                            isScanning = false
                            viewModel.errorMessage = "Failed to process image."
                        }
                } catch (e: Exception) {
                    isScanning = false
                    viewModel.errorMessage = "Error loading image."
                }
            }
        }
    )

    LaunchedEffect(Unit) {
        if (!cameraPermissionState.status.isGranted) {
            cameraPermissionState.launchPermissionRequest()
        }
    }

    val infiniteTransition = rememberInfiniteTransition(label = "scan_line")
    val lineOffset by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "scan_line_offset"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF1E293B))
    ) {
        if (cameraPermissionState.status.isGranted) {
            CameraPreview(
                modifier = Modifier.fillMaxSize(),
                zoomRatio = zoomRatio,
                isFlashOn = isFlashOn,
                onBarcodeDetected = { barcode ->
                    if (!isScanning) {
                        isScanning = true
                        showScanFeedback = true
                        viewModel.fetchProductData(barcode) { success ->
                            if (success) {
                                onScanComplete()
                            } else {
                                isScanning = false
                                showScanFeedback = false
                            }
                        }
                    }
                }
            )
        }

        // --- Detection Feedback Overlay ---
        if (showScanFeedback) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(PrimaryGreen.copy(alpha = 0.3f)),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = "Detected",
                        tint = Color.White,
                        modifier = Modifier.size(80.dp)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "BARCODE DETECTED",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    )
                }
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 48.dp, start = 24.dp, end = 24.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(Color.Black.copy(alpha = 0.5f))
                    .clickable(onClick = onCloseClick),
                contentAlignment = Alignment.Center
            ) {
                Icon(imageVector = Icons.Default.Close, contentDescription = "Close", tint = Color.White)
            }

            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(text = "SCAN BARCODE", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp, letterSpacing = 1.sp)
                Spacer(modifier = Modifier.height(4.dp))
                Box(modifier = Modifier.width(24.dp).height(3.dp).clip(RoundedCornerShape(1.5.dp)).background(PrimaryGreen))
            }

            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(Color.Black.copy(alpha = 0.5f))
                    .clickable { isFlashOn = !isFlashOn },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = if (isFlashOn) Icons.Default.FlashlightOn else Icons.Default.FlashlightOff,
                    contentDescription = "Flash",
                    tint = Color.White
                )
            }
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 120.dp, start = 24.dp, end = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = "Align the nutrition label within the frame", color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold, textAlign = androidx.compose.ui.text.style.TextAlign.Center)
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = "Hold steady for automatic detection", color = Color(0xFF9CA3AF), fontSize = 14.sp)
        }

        Box(
            modifier = Modifier
                .align(Alignment.Center)
                .padding(top = 40.dp)
                .fillMaxWidth(0.85f)
                .aspectRatio(0.8f) 
        ) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val cornerLength = 40.dp.toPx()
                val strokeWidth = 4.dp.toPx()
                val cornerRadius = 16.dp.toPx()

                drawPath(
                    path = androidx.compose.ui.graphics.Path().apply {
                        moveTo(0f, cornerLength); lineTo(0f, cornerRadius); quadraticBezierTo(0f, 0f, cornerRadius, 0f); lineTo(cornerLength, 0f)
                    },
                    color = PrimaryGreen,
                    style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
                )
                drawPath(
                    path = androidx.compose.ui.graphics.Path().apply {
                        moveTo(size.width - cornerLength, 0f); lineTo(size.width - cornerRadius, 0f); quadraticBezierTo(size.width, 0f, size.width, cornerRadius); lineTo(size.width, cornerLength)
                    },
                    color = PrimaryGreen,
                    style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
                )
                drawPath(
                    path = androidx.compose.ui.graphics.Path().apply {
                        moveTo(0f, size.height - cornerLength); lineTo(0f, size.height - cornerRadius); quadraticBezierTo(0f, size.height, cornerRadius, size.height); lineTo(cornerLength, size.height)
                    },
                    color = PrimaryGreen,
                    style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
                )
                drawPath(
                    path = androidx.compose.ui.graphics.Path().apply {
                        moveTo(size.width - cornerLength, size.height); lineTo(size.width - cornerRadius, size.height); quadraticBezierTo(size.width, size.height, size.width, size.height - cornerRadius); lineTo(size.width, size.height - cornerLength)
                    },
                    color = PrimaryGreen,
                    style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
                )

                val lineY = size.height * lineOffset
                drawLine(color = PrimaryGreen.copy(alpha = 0.6f), start = Offset(0f, lineY), end = Offset(size.width, lineY), strokeWidth = 2.dp.toPx())
            }
        }

        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(32.dp))
                    .background(Color(0xFF064E3B).copy(alpha = 0.6f))
                    .border(1.dp, PrimaryGreen.copy(alpha = 0.3f), RoundedCornerShape(32.dp))
                    .padding(horizontal = 24.dp, vertical = 12.dp)
            ) {
                Text(text = "DETECTING BARCODE...", color = PrimaryGreen, fontSize = 12.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
            }

            Spacer(modifier = Modifier.height(32.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 32.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Box(
                        modifier = Modifier.size(56.dp).clip(CircleShape).background(Color.Black.copy(alpha = 0.5f)).border(1.dp, Color(0xFF4B5563), CircleShape)
                            .clickable { photoPickerLauncher.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)) },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(imageVector = Icons.Outlined.PhotoLibrary, contentDescription = "Gallery", tint = Color.White, modifier = Modifier.size(24.dp))
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("GALLERY", color = Color.White, fontSize = 10.sp, letterSpacing = 1.sp)
                }

                Box(
                    modifier = Modifier.size(80.dp).clip(CircleShape).border(3.dp, Color.White.copy(alpha = 0.5f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Box(modifier = Modifier.size(64.dp).clip(CircleShape).background(if (isScanning) PrimaryGreen else Color.White.copy(alpha = 0.3f)))
                }

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Box(
                        modifier = Modifier.size(56.dp).clip(CircleShape).background(Color.Black.copy(alpha = 0.5f)).border(1.dp, Color(0xFF4B5563), CircleShape)
                            .clickable { 
                                zoomRatio = if (zoomRatio == 1f) 2f else 1f
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "${zoomRatio.toInt()}x",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("ZOOM", color = Color.White, fontSize = 10.sp, letterSpacing = 1.sp)
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            Row(
                modifier = Modifier.fillMaxWidth().background(Color.Black.copy(alpha = 0.7f)).padding(vertical = 20.dp),
                horizontalArrangement = Arrangement.Center
            ) {
                Text(text = "BARCODE", color = PrimaryGreen, fontSize = 12.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
fun CameraPreview(
    modifier: Modifier = Modifier, 
    zoomRatio: Float = 1f,
    isFlashOn: Boolean = false,
    onBarcodeDetected: (String) -> Unit
) {
    val lifecycleOwner = LocalLifecycleOwner.current
    var camera: Camera? by remember { mutableStateOf(null) }

    LaunchedEffect(zoomRatio) {
        camera?.cameraControl?.setZoomRatio(zoomRatio)
    }

    LaunchedEffect(isFlashOn) {
        camera?.cameraControl?.enableTorch(isFlashOn)
    }

    AndroidView(
        modifier = modifier.pointerInput(Unit) {
            detectTapGestures { offset ->
                val factory = SurfaceOrientedMeteringPointFactory(size.width.toFloat(), size.height.toFloat())
                val point = factory.createPoint(offset.x, offset.y)
                val action = FocusMeteringAction.Builder(point).build()
                camera?.cameraControl?.startFocusAndMetering(action)
            }
        },
        factory = { context ->
            val previewView = PreviewView(context).apply { 
                implementationMode = PreviewView.ImplementationMode.COMPATIBLE 
            }
            val cameraProviderFuture = ProcessCameraProvider.getInstance(context)
            val analyzer = BarcodeAnalyzer { onBarcodeDetected(it) }
            
            // Higher resolution for better barcode detection on small/curved products
            val imageAnalysis = ImageAnalysis.Builder()
                .setTargetResolution(Size(1280, 720))
                .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                .build().also {
                    it.setAnalyzer(Executors.newSingleThreadExecutor(), analyzer)
                }

            cameraProviderFuture.addListener({
                val cameraProvider = cameraProviderFuture.get()
                val preview = Preview.Builder().build().also { 
                    it.setSurfaceProvider(previewView.surfaceProvider) 
                }
                try {
                    cameraProvider.unbindAll()
                    camera = cameraProvider.bindToLifecycle(
                        lifecycleOwner, 
                        CameraSelector.DEFAULT_BACK_CAMERA, 
                        preview, 
                        imageAnalysis
                    )
                    // Enable auto-focus by default
                    camera?.cameraControl?.enableTorch(false)
                } catch (exc: Exception) { }
            }, ContextCompat.getMainExecutor(context))
            previewView
        }
    )
}

class BarcodeAnalyzer(private val onBarcodeDetected: (String) -> Unit) : ImageAnalysis.Analyzer {
    private val options = BarcodeScannerOptions.Builder().setBarcodeFormats(Barcode.FORMAT_ALL_FORMATS).build()
    private val scanner = BarcodeScanning.getClient(options)

    @androidx.annotation.OptIn(androidx.camera.core.ExperimentalGetImage::class)
    override fun analyze(imageProxy: ImageProxy) {
        val mediaImage = imageProxy.image
        if (mediaImage != null) {
            val image = InputImage.fromMediaImage(mediaImage, imageProxy.imageInfo.rotationDegrees)
            scanner.process(image)
                .addOnSuccessListener { barcodes ->
                    for (barcode in barcodes) {
                        barcode.rawValue?.let { onBarcodeDetected(it) }
                    }
                }
                .addOnCompleteListener { imageProxy.close() }
        } else {
            imageProxy.close()
        }
    }
}
