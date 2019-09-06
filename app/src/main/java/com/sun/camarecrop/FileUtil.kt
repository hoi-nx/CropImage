import android.app.Activity
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.media.ExifInterface
import android.net.Uri
import android.os.Environment
import android.util.DisplayMetrics
import androidx.core.content.FileProvider
import com.sun.camarecrop.App
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

object FileUtil {
    fun createTempFile(context: Context, uri: Uri): File {
        val inputStream = context.contentResolver.openInputStream(uri)!!

        val file = File(genImagePath())
        val outputStream = FileOutputStream(file)

        inputStream.copyTo(outputStream)

        outputStream.close()
        inputStream.close()

        return file
    }

    fun genImagePath() = Environment.getExternalStorageDirectory().absolutePath
            .plus("/")
            .plus(System.currentTimeMillis())
            .plus(".jpg")

    fun getUriFromPath(context: Context, path: String): Uri {
        val authority = App.INSTANCE.packageName.plus(".provider")
        return FileProvider.getUriForFile(context, authority, File(path))
    }

    fun resizeImageFile(imagePath: String) {
        val file = File(imagePath)
        if (file.length() <= 600 * 1024) { // 600 KB
            return
        }

        val options = BitmapFactory.Options()

        options.inJustDecodeBounds = true
        BitmapFactory.decodeFile(imagePath, options)

        if (options.outWidth >= options.outHeight) {
            options.inSampleSize = calculateInSampleSize(options, 800, 600)
        } else {
            options.inSampleSize = calculateInSampleSize(options, 600, 800)
        }

        options.inJustDecodeBounds = false
        val bitmap = BitmapFactory.decodeFile(imagePath, options)

        val fos = FileOutputStream(file)
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos)
    }

    // ---------------------------------------------------------------------------------------------
    private fun calculateInSampleSize(options: BitmapFactory.Options, reqWith: Int, reqHeight: Int): Int {
        val width = options.outWidth
        val height = options.outHeight
        var inSampleSize = 1

        if (width > reqWith || height > reqHeight) {
            val halfWidth = width / 2
            val halfHeight = height / 2

            while ((halfWidth / inSampleSize) >= reqWith && (halfHeight / inSampleSize) >= reqHeight) {
                inSampleSize *= 2
            }
        }

        return inSampleSize
    }

    fun getMultipartBody(filePath: Uri, activity: Activity): MultipartBody {
        val file = File(createPhotoFileFromUri(filePath.toString(), "avata", activity))
        val requestFile = RequestBody.create(MediaType.parse(activity.contentResolver.getType(filePath)), file)
        val body = MultipartBody.Builder().addFormDataPart("file-type", "profile")
            .addFormDataPart("photo", file.name, requestFile)
            .build()
        return body
    }

    // private  val photoDir = Environment.getExternalStorageDirectory()
    private val photoDir = App.INSTANCE.filesDir

    @Throws(IOException::class)
    fun createPhotoFileFromUri(photoUri: String, fileName: String, activity: Activity): String {
        // Create origin file
        val inputStream = App.INSTANCE.contentResolver.openInputStream(Uri.parse(photoUri))
        val file = File("$photoDir/$fileName.jpg")
        org.apache.commons.io.FileUtils.copyInputStreamToFile(inputStream,file)

        val path = file.absolutePath
        if (file.length() <= 600 * 1024) { // 60 KB
            return path
        }

        val options = BitmapFactory.Options()
        options.inJustDecodeBounds = true
        BitmapFactory.decodeFile(path, options)
        val matrix = Matrix()
        val exifReader = ExifInterface(path)
        val orientation = exifReader.getAttributeInt(ExifInterface.TAG_ORIENTATION, -1)
        var rotate = 0
        when (orientation) {
            ExifInterface.ORIENTATION_NORMAL -> {
                // Do nothing. The original image is fine.
            }
            ExifInterface.ORIENTATION_ROTATE_90 -> rotate = 90
            ExifInterface.ORIENTATION_ROTATE_180 -> rotate = 180
            ExifInterface.ORIENTATION_ROTATE_270 -> rotate = 270
        }
        matrix.postRotate(rotate.toFloat())


//        if (options.outWidth >= options.outHeight) {
//            options.inSampleSize = calculateInSampleSize(options, 800, 600)
//        } else {
//            options.inSampleSize = calculateInSampleSize(options, 600, 800)
//        }
//        options.inJustDecodeBounds = false
        //val bitmap = BitmapFactory.decodeFile(path, options)
        val bitmap = loadBitmap(path, rotate, activity)
        val fos = FileOutputStream(file)
        bitmap?.compress(Bitmap.CompressFormat.JPEG, 80, fos)

        return path
    }

    /**
     * Calculate inSampleSize by dimensions
     */
    private fun calculateInSampleSizeBitmap(options: BitmapFactory.Options, reqWith: Int, reqHeight: Int): Int {
        val width = options.outWidth
        val height = options.outHeight
        var inSampleSize = 1

        if (width > reqWith || height > reqHeight) {
            val halfWidth = width / 2
            val halfHeight = height / 2

            while ((halfWidth / inSampleSize) >= reqWith
                && (halfHeight / inSampleSize) >= reqHeight
            ) {
                inSampleSize *= 2
            }
        }

        return inSampleSize
    }

    fun loadBitmap(path: String, orientation: Int, activity: Activity): Bitmap? {
        val displaymetrics = DisplayMetrics()
        activity.windowManager.defaultDisplay.getMetrics(displaymetrics)
        val screenWidth = displaymetrics.widthPixels
        val screenHeight = displaymetrics.heightPixels
        var bitmap: Bitmap? = null
        try {
            val options = BitmapFactory.Options()
            options.inJustDecodeBounds = true
            BitmapFactory.decodeFile(path, options)
            var sourceWidth: Int
            var sourceHeight: Int
            if (orientation == 90 || orientation == 270) {
                sourceWidth = options.outHeight
                sourceHeight = options.outWidth
            } else {
                sourceWidth = options.outWidth
                sourceHeight = options.outHeight
            }
            if (sourceWidth > screenWidth || sourceHeight > screenHeight) {
                val widthRatio = sourceWidth.toFloat() / screenWidth.toFloat()
                val heightRatio = sourceHeight.toFloat() / screenHeight.toFloat()
                val maxRatio = Math.max(widthRatio, heightRatio)
                options.inJustDecodeBounds = false
                options.inSampleSize = maxRatio.toInt()
                bitmap = BitmapFactory.decodeFile(path, options)
            } else {
                bitmap = BitmapFactory.decodeFile(path)
            }
            if (orientation > 0) {
                val matrix = Matrix()
                matrix.postRotate(orientation.toFloat())
                bitmap = Bitmap.createBitmap(bitmap!!, 0, 0, bitmap.width, bitmap.height, matrix, true)
            }
            sourceWidth = bitmap!!.width
            sourceHeight = bitmap.height
            if (sourceWidth != screenWidth || sourceHeight != screenHeight) {
                val widthRatio = sourceWidth.toFloat() / screenWidth.toFloat()
                val heightRatio = sourceHeight.toFloat() / screenHeight.toFloat()
                val maxRatio = Math.max(widthRatio, heightRatio)
                sourceWidth = (sourceWidth.toFloat() / maxRatio).toInt()
                sourceHeight = (sourceHeight.toFloat() / maxRatio).toInt()
                bitmap = Bitmap.createScaledBitmap(bitmap, sourceWidth, sourceHeight, true)
            }
        } catch (e: Exception) {
        }

        return bitmap
    }
}