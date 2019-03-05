package app.myCode.ir.ViewModel.Classes

import android.os.Handler
import android.os.Looper
import okhttp3.MediaType
import okhttp3.RequestBody
import okio.BufferedSink
import java.io.File
import java.io.FileInputStream
import java.io.IOException

class ProgressRequestBody(var file: File, var listener: UploadCallbacks) : RequestBody() {


    private val mPath: String? = null

    private val DEFAULT_BUFFER_SIZE = 2048

    interface UploadCallbacks {
        fun onProgressUpdate(percentage: Int)

        fun onError()

        fun onFinish()
    }


    override fun contentType(): MediaType {
        // i want to upload only images
        return MediaType.parse("image/*")!!
    }

    @Throws(IOException::class)
    override fun contentLength(): Long {
        return file.length()
    }





    @Throws(IOException::class)
    override fun writeTo(sink: BufferedSink) {
        val fileLength = file.length()
        val buffer = ByteArray(DEFAULT_BUFFER_SIZE)
        val input = FileInputStream(file)
        var uploaded: Long = 0

        try {
            var read: Int
            val handler = Handler(Looper.getMainLooper())


            while (input.read(buffer).let { read = it; it != -1 }) {



                // update progress on UI thread
                handler.post(ProgressUpdater(uploaded, fileLength))

                uploaded += read.toLong()
                sink.write(buffer, 0, read)


            }
        } finally {
            input.close()
        }
    }

    private inner class ProgressUpdater(private val mUploaded: Long, private val mTotal: Long) : Runnable {

        override fun run() {
            listener.onProgressUpdate((100 * mUploaded / mTotal).toInt())

            if (mTotal - mUploaded < DEFAULT_BUFFER_SIZE) {
                listener.onFinish()
            }
        }
    }

}