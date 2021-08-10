import android.content.Context
import android.os.Environment
import java.io.BufferedWriter
import java.io.FileWriter
import java.io.PrintWriter
import java.time.OffsetDateTime
import java.time.temporal.ChronoUnit

class OtherFileStorage(context: Context) {

    val fileAppend : Boolean = true //true=追記, false=上書き
    val context:Context = context
    var fileName : String = "SensorLog"
    val extension : String = ".csv"
    val filePath: String = context.getApplicationContext().getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS).toString().plus("/").plus(fileName).plus(extension) //内部ストレージのDocumentのURL
    val baceTime: OffsetDateTime = OffsetDateTime.now()
    val dimention: Int = 3

    init {
        writeText(firstLog(dimention),filePath)
    }

    fun doLog(text: String) {
        writeText(ChronoUnit.MILLIS.between(baceTime,OffsetDateTime.now()).toString().plus(",").plus(text), filePath)
    }

    //CSV一行目の出力をする。
    private fun firstLog(dimension: Int):String {
        return when (dimension) {
            1 -> baceTime.toString().plus(",x")
            2 -> baceTime.toString().plus(",x,y")
            3 -> baceTime.toString().plus(",x,y,z")
            else -> {
                var result: String = baceTime.toString()
                for (i in 0 until dimension) result = result.plus(",").plus(i)
                result
            }
        }
    }

    //外部ストレージにファイル出力をする関数
    private fun writeText(text:String, path:String){
        val fil = FileWriter(path,fileAppend)
        val pw = PrintWriter(BufferedWriter(fil))
        pw.println(text)
        pw.close()
    }
}