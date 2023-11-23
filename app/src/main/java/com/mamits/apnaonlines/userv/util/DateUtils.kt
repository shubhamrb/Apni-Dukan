package com.mamits.apnaonlines.userv.utils

import android.content.Context
import android.text.format.DateFormat
import com.mamits.apnaonlines.userv.model.TimerObj
import com.mamits.apnaonlines.userv.util.CommonUtils
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

/**
 * Created by vijay on 17/05/2019.
 */
class DateUtils {


    companion object {

        val yyyy = "yyyy"
        val MM = "MM"
        val dd_mmm_yyyy_hh_mm_ss = "dd-mmm-yyyy hh:mm:ss"
        val dd_mmm_yyyy_hh_mm_a = "dd-mmm-yyyy hh:mm a"
        val yyyy_MM_dd = "yyyy-MM-dd"
        val yyyy_MM_dd_HH_mm_ss_ampm = "yyyy-MM-dd hh:mm:ss a"
        val dd_MM_yyyy_HH_mm_ampm = "dd-MM-yyyy hh:mm a"
//        val dd_MM_yyyy_HH_mm_ss_ampm = "dd-MM-yyyy hh:mm:ss a"
        val dd_MM_yyyy_HH_mm = "dd-MM-yyyy HH:mm"
        val dd_MM_yyyy_comma_HH_mm_ampm = "dd-MM-yyyy, hh:mm a"
        val dd_slash_MM_yyyy_HH_mm_ss_ampm = "dd/MM/yyyy hh:mm:ss a"
        val dd_slash_MMM_yyyy_HH_mm_ss_ampm = "dd MMM yyyy hh:mm:ss a"
        val dd_slash_MMM_yyyy_HH_mm_ampm = "dd MMM yyyy"
        val dd_slash_MMM_yyyy_HH_mm = "dd MMM yyyy hh:mm"
        val dd_MMMM_yyyy = "dd MMMM yyyy"
        val dd_MMM_yyyy = "dd MMM yyyy"
        val dd_MMM_yyyy_hh_mm_a = "dd MMM yyyy hh:mm a"
        val MMM_yyyy = "MMM yyyy"
        val MM_yyyy = "MMyyyy"
        val dd_MMM_yy = "dd MMM yy"
        val dd_MMM = "dd MMM"
        val dd_MM_yyyy = "dd-MM-yyyy"
        val yyyy_MM_dd_hh_mm_ss = "yyyy-MM-dd HH:mm:ss"
        val hh_mm_ss = "hh:mm:ss a"
        val hh_a = "hh a"
        val HH = "HH"
        val hh = "hh"
        val HH_mm_ss = "HH:mm:ss"
        val mm = "mm"
        val HH_mm_ss_a = "HH:mm:ss a"
        val dd__MM__yyyy = "dd/MM/yyyy"
        val EEE_d_MMM = "EEE, d MMM"

        val getFormattedDateEEE_MMM_d_h_mm_a = "EEE, MMM d 'at' h:mm a"
        val EEE_MMM_d = "EEE, MMM d"
        val dd_MM_yyyy_HH_mm_ss = "dd-MM-yyyy HH:mm:ss"
        val yyyy_MM_dd_HH_mm_ss = "yyyy-MM-dd HH:mm:ss"
        val yyyy_MM_dd_slash = "yyyy-MM-dd"
        val MMM = "MMM"
        val MMM_yy = "MMM"
        val dd = "dd"
        val eee_h_mm_a = "EEE h:mm a"
        val hh_mm_a = "hh:mm a"
        val hh_mm_aaa = "hh:mm aaa"
        val HH_mm_a = "HH:mm a"
        val hh_mm = "hh:mm"
        val HH_mm = "HH:mm"
        val hh_mm_ss_a = "hh:mm:ss a"

        val yyyy_MM_dd_T_HH_mm_ssZ = "yyyy-MM-dd'T'HH:mm:ss.000+0000"
        val yyyy_MM_dd_T_HH_mm_sss = "yyyy-MM-dd'T'HH:mm:ss.SSS'+'SSSS"

        val d_M_yyyy_h_mm_ampm = "d/M/yyyy h:mm a"
        val mmm_d_yyyy = "MMM d, yyyy"

        fun getLocalTimeString(strUTCTime: String, oldFormat: String, newFormat: String): String? {
            var strLocalTime: String? = null
            val UTCTimeFormatter = SimpleDateFormat(oldFormat, Locale.getDefault())
            val LocalTimeFormatter = SimpleDateFormat(newFormat, Locale.getDefault())

            try {
                UTCTimeFormatter.timeZone = TimeZone.getTimeZone("UTC")
                val date = UTCTimeFormatter.parse(strUTCTime)
                LocalTimeFormatter.timeZone = TimeZone.getDefault()
                strLocalTime = LocalTimeFormatter.format(date)
            } catch (e: Exception) {
                e.printStackTrace()
            }

            return strLocalTime
        }

        fun getUTCTimeString(strLocalTime: String, oldFormat: String, newFormat: String): String {
            var s = ""
            val LocalTimeFormatter = SimpleDateFormat(oldFormat, Locale.getDefault())
            val UTCTimeFormatter = SimpleDateFormat(newFormat, Locale.getDefault())

            try {
                LocalTimeFormatter.timeZone = TimeZone.getDefault()
                // millisecond = LocalTimeFormatter.parse(strLocalTime).getTime();
                val d = LocalTimeFormatter.parse(strLocalTime)
                UTCTimeFormatter.timeZone = TimeZone.getTimeZone("UTC")
                s = UTCTimeFormatter.format(d)
            } catch (e: Exception) {
                e.printStackTrace()
            }

            return s
        }

        fun changeDateFormat(selectedDate: String?, oldFormat: String, newFormat: String): String {
            var simpleDateFormat = SimpleDateFormat(oldFormat, Locale.getDefault())
            simpleDateFormat = convertDateToUTC(simpleDateFormat)
            val sdfTarget = SimpleDateFormat(newFormat, Locale.getDefault())
            var StrDateNew = ""

            try {
                val datenw = simpleDateFormat.parse(selectedDate)
                StrDateNew = sdfTarget.format(datenw)
            } catch (e: Exception) {
                StrDateNew = ""
            }

            return StrDateNew
        }

        fun changeDateToUTCFormat(selectedDate: String?, oldFormat: String, newFormat: String): String {
            var simpleDateFormat = SimpleDateFormat(oldFormat, Locale.getDefault())
            val sdfTarget = SimpleDateFormat(newFormat)
            sdfTarget.timeZone = TimeZone.getTimeZone("UTC")
            var StrDateNew = ""

            try {
                val datenw = simpleDateFormat.parse(selectedDate)
                StrDateNew = sdfTarget.format(datenw)
            } catch (e: Exception) {
                StrDateNew = ""
            }

            return StrDateNew
        }

        fun changeUTCDateFormat(selectedDate: String?, oldFormat: String, newFormat: String): String {
            var simpleDateFormat = SimpleDateFormat(oldFormat, Locale.getDefault())
            simpleDateFormat.timeZone = TimeZone.getTimeZone("UTC")
            val sdfTarget = SimpleDateFormat(newFormat)
            var StrDateNew = ""

            try {
                val datenw = simpleDateFormat.parse(selectedDate)
                StrDateNew = sdfTarget.format(datenw)
            } catch (e: Exception) {
                StrDateNew = ""
            }

            return StrDateNew
        }

        fun getDate(requestedFormat: String, time: Long): String {
            val cal = Calendar.getInstance(Locale.US)
            cal.timeInMillis = time
            return DateFormat.format(requestedFormat, cal).toString()
        }

        fun getCurrentDate(format: String): String {
            var formattedDate = ""
            try {
                val c = Calendar.getInstance()
                val simpleDateFormat = SimpleDateFormat(format, Locale.getDefault())
                formattedDate = simpleDateFormat.format(c.time)
            } catch (e: Exception) {
                formattedDate = ""
            }

            return formattedDate
        }

        fun getTomorrowDate(format: String): String {
            var formattedDate = ""
            try {
                val c = Calendar.getInstance()
                c.add(Calendar.DAY_OF_YEAR, 1)
                val simpleDateFormat = SimpleDateFormat(format, Locale.getDefault())
                formattedDate = simpleDateFormat.format(c.time)
            } catch (e: Exception) {
                formattedDate = ""
            }

            return formattedDate
        }


        fun getCurrentTime(pattern: String): String {
            var formattedDate = ""
            try {
                val c = Calendar.getInstance()
                val simpleDateFormat = SimpleDateFormat(pattern, Locale.getDefault())
                formattedDate = simpleDateFormat.format(c.time)
            } catch (e: Exception) {
                formattedDate = ""
            }

            return formattedDate
        }

        val currentMinutes: Int
            get() {
                var formattedTime: Int
                try {
                    val c = Calendar.getInstance()
                    val simpleDateFormat = SimpleDateFormat(mm, Locale.getDefault())
                    formattedTime = Integer.parseInt(simpleDateFormat.format(c.time))
                } catch (e: Exception) {
                    formattedTime = 0
                }

                return formattedTime
            }

        val currentHour: Int
            get() {
                var formattedTime: Int
                try {
                    val time = getCurrentTime(HH_mm_ss)
                    val simpleDateFormat = SimpleDateFormat(HH_mm_ss, Locale.getDefault())
                    val simpleDateForm = SimpleDateFormat(HH, Locale.getDefault())
                    val date = simpleDateFormat.parse(time)
                    formattedTime = Integer.parseInt(simpleDateForm.format(date))
                } catch (e: Exception) {
                    formattedTime = 0
                }

                return formattedTime
            }

        fun getSelectedDay(olddate: String?): Int {
            var formattedTime: Int
            try {
                val simpleDateFormat = SimpleDateFormat(yyyy_MM_dd, Locale.getDefault())
                val simpleDateFormatNew = SimpleDateFormat(dd, Locale.getDefault())
                val date = simpleDateFormat.parse(olddate)
                formattedTime = Integer.parseInt(simpleDateFormatNew.format(date))
            } catch (e: Exception) {
                formattedTime = 0
            }

            return formattedTime
        }

        fun parseDateFormat(selectedDate: String?, format: String): Date {
            var formattedDate = Date()
            try {
                var simpleDateFormat = SimpleDateFormat(format, Locale.getDefault())
//                simpleDateFormat = convertDateToUTC(simpleDateFormat)
                formattedDate = simpleDateFormat.parse(selectedDate)
            } catch (e: Exception) {
                formattedDate = Date()
            }

            return formattedDate
        }

        fun parseUTCDateFormat(selectedDate: String?, format: String): Date {
            var simpleDateFormat = SimpleDateFormat(format, Locale.getDefault())
            simpleDateFormat.timeZone = TimeZone.getTimeZone("UTC")
            var datenw = Date()
            try {
                datenw = simpleDateFormat.parse(selectedDate)
            } catch (e: Exception) {
            }

            return datenw
        }

        fun convertDateToUTC(simpleDateFormat: SimpleDateFormat): SimpleDateFormat {
//            simpleDateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
            return simpleDateFormat
        }

        fun getTimeFromVideoDuration(duration: Long): String {
            return String.format(Locale.US, "%02d:%02d", TimeUnit.MILLISECONDS.toMinutes(duration),
                    TimeUnit.MILLISECONDS.toSeconds(duration) - TimeUnit.MINUTES.toSeconds(
                            TimeUnit.MILLISECONDS.toMinutes(duration)))
        }

        fun convertDateFormat(strLocalTime: String?, oldFormat: String, newFormat: String): String {
            var s = ""
            val LocalTimeFormatter = SimpleDateFormat(oldFormat, Locale.getDefault())
            val TimeFormatter = SimpleDateFormat(newFormat, Locale.getDefault())

            try {
                LocalTimeFormatter.timeZone = TimeZone.getDefault()
                // millisecond = LocalTimeFormatter.parse(strLocalTime).getTime();
                val d = LocalTimeFormatter.parse(strLocalTime)
                TimeFormatter.timeZone = TimeZone.getDefault()
                s = TimeFormatter.format(d)
            } catch (e: Exception) {
                e.printStackTrace()
            }

            return s
        }

        fun getTimeDifference(date1: Date, date2: Date): String {
            val millis = date1.getTime() - date2.getTime()
            val hours = millis / (1000 * 60 * 60)
            val mins = millis % (1000 * 60 * 60)

            return "$hours : $mins"
        }

        fun getHourDifference(date1: Date, date2: Date): Long {
            val difference = date1.time - date2.time
//            val hours = difference / (1000 * 60 * 60)
            val hours = (difference / (1000 * 60 * 60)).toInt()
            return hours.toLong()
        }

        fun getMinutesDifference(date1: Date, date2: Date): Long {
            val difference = date1.time - date2.time
//            val mins = difference % (1000 * 60 * 60)
            val mins = (difference / (1000 * 60)).toInt() % 60
            return mins.toLong()
        }

        fun getFormatedDate(context: Context, data: String?, oldFormat: String, newFormat: String): String {
            var milliseconds: Long = 0
            val f = SimpleDateFormat(oldFormat, Locale.getDefault())

            try {
                val d = f.parse(data)
                milliseconds = d.time
            } catch (e: Exception) {
                e.printStackTrace()
            }

            /*String date = android.text.format.DateUtils.getRelativeDateTimeString(context, milliseconds,
                android.text.format.DateUtils.SECOND_IN_MILLIS, android.text.format.DateUtils.FORMAT_ABBREV_RELATIVE, 0).toString();*/

            //        String date = DateUtils.convertDateFormat(data, DateUtils.yyyy_MM_dd_HH_mm_ss_ampm, DateUtils.dd_MM_yyyy_comma_HH_mm_ampm);

            val date = DateUtils.convertDateFormat(data, oldFormat, newFormat)

            val trydate = android.text.format.DateUtils.getRelativeDateTimeString(context, milliseconds,
                    android.text.format.DateUtils.DAY_IN_MILLIS, android.text.format.DateUtils.WEEK_IN_MILLIS,
                    android.text.format.DateUtils.FORMAT_SHOW_YEAR).toString()
            CommonUtils.printLog("date", trydate)

            return if (trydate.contains("Today") || trydate.contains("Yesterday") || trydate.contains("Tomorrow")) {
                trydate
            } else {
                date
            }
        }

        fun getChatFormattedDate(context: Context, data: String): String {
            var milliseconds: Long = 0
            val f = SimpleDateFormat(yyyy_MM_dd_HH_mm_ss_ampm, Locale.getDefault())
            f.timeZone = TimeZone.getDefault()
            try {
                val d = f.parse(data)
                milliseconds = d.time
            } catch (e: Exception) {
                e.printStackTrace()
            }

            //        String date = android.text.format.DateUtils.getRelativeDateTimeString(context, milliseconds,
            //                android.text.format.DateUtils.SECOND_IN_MILLIS, android.text.format.DateUtils.FORMAT_ABBREV_RELATIVE, 0).toString();
            val date = DateUtils.convertDateFormat(data, DateUtils.yyyy_MM_dd_HH_mm_ss_ampm, DateUtils.EEE_d_MMM)
            val trydate = android.text.format.DateUtils.getRelativeDateTimeString(context, milliseconds,
                    android.text.format.DateUtils.DAY_IN_MILLIS, android.text.format.DateUtils.WEEK_IN_MILLIS,
                    android.text.format.DateUtils.FORMAT_SHOW_YEAR).toString()
            CommonUtils.printLog("date", trydate)

            return if (trydate.contains("Today") || trydate.contains("Yesterday") || trydate.contains("Tomorrow")) {
                trydate.split(",".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()[0]
            } else {
                date
            }
        }

        fun checkAfterTimings(time: String, endtime: String): Boolean {

            try {
                val date1 = SimpleDateFormat(HH_mm, Locale.getDefault()).parse(time)
                val date2 = SimpleDateFormat(HH_mm_ss, Locale.getDefault()).parse(endtime)

                return date1.after(date2)
            } catch (e: Exception) {
                e.printStackTrace()
            }

            return false
        }

        fun checkLastHourTimings(time: String, endtime: String): Boolean {
            val pattern = "HH:mm"
            val sdf = SimpleDateFormat(pattern, Locale.getDefault())
            try {
                val date1 = sdf.parse(time)
                val date2 = sdf.parse(endtime)
                val difference = date1.time - date2.time

                val hours = (difference / (1000 * 60 * 60)).toInt()
                val Mins = (difference / (1000 * 60)).toInt() % 60
                val Secs = ((difference / 1000).toInt() % 60).toLong()

                if (Mins <= 30) {
                    return true
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }

            return false
        }

        fun checkTimeDifference(endtime: String, time: String): TimerObj {
            val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
            val sdf1 = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
            var mTimerObj: TimerObj? = TimerObj("0","0","0",0)
                val date1 = sdf.parse(time)
                val date2 = sdf1.parse(endtime)
                val difference = date1.time - date2.time

                if(difference>0){
                    val hours = (difference / (1000 * 60 * 60)).toInt()
                    val Mins = (difference / (1000 * 60)).toInt() % 60
                    val Secs = ((difference / 1000).toInt() % 60).toLong()
                    mTimerObj = TimerObj(hours?.toString(), Mins.toString(), Secs?.toString(), totalMillis = difference)
                }
                return mTimerObj!!
        }

        fun addHrMinuteToDateStr(dateStr:String, ishr:Boolean, unit:Int):String{
            val dateString = dateStr
            val millisToAdd: Long = if(ishr) ((1000 * 60 * 60) * unit).toLong() else ((1000 * 60) * unit).toLong()
            val format = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
            val d: Date = format.parse(dateString)
            d.time = d.time + millisToAdd
            var newDateStr = format.format(d)
            println("New value: $newDateStr")
            return newDateStr
        }


        fun getTimeObjFromMillis(difference:Long): TimerObj {
            if (difference > 0) {
                val hours = (difference / (1000 * 60 * 60)).toInt()
                val Mins = (difference / (1000 * 60)).toInt() % 60
                val Secs = ((difference / 1000).toInt() % 60).toLong()
                return TimerObj(
                    hours?.toString(),
                    Mins.toString(),
                    Secs?.toString(),
                    totalMillis = difference
                )
            }else{
                return TimerObj(
                    "0",
                    "0",
                    "0",
                    totalMillis = difference
                )
            }
        }

        fun checkBeforeTimings(time: String, endtime: String): Boolean {
            try {
                val date1 = SimpleDateFormat(HH_mm, Locale.getDefault()).parse(time)
                val date2 = SimpleDateFormat(HH_mm_ss, Locale.getDefault()).parse(endtime)

                return date1.before(date2)
            } catch (e: Exception) {
                e.printStackTrace()
            }

            return false
        }

        fun checkIsValidDateTime(time: String, endtime: String): Boolean {
            try {
                val date1 = SimpleDateFormat(dd_slash_MMM_yyyy_HH_mm_ampm, Locale.getDefault()).parse(time)
                val date2 = SimpleDateFormat(yyyy_MM_dd_HH_mm_ss_ampm, Locale.getDefault()).parse(endtime)

                return !date1.before(date2)
            } catch (e: Exception) {
                e.printStackTrace()
            }

            return false
        }

        fun checkEqualDateTime(time: String, endtime: String): Boolean {
            try {
                val date1 = SimpleDateFormat(HH_mm_ss, Locale.getDefault()).parse(time)
                val date2 = SimpleDateFormat(HH_mm, Locale.getDefault()).parse(endtime)

                return date1 != date2
            } catch (e: Exception) {
                e.printStackTrace()
            }

            return false
        }

        fun isTomorrowDate(time: String, endtime: String): Boolean {
            try {
                val date1 = SimpleDateFormat(dd_slash_MMM_yyyy_HH_mm_ampm, Locale.getDefault()).parse(time)
                val date2 = SimpleDateFormat(yyyy_MM_dd_HH_mm_ss_ampm, Locale.getDefault()).parse(endtime)

                return date1.date > date2.date
            } catch (e: Exception) {
                e.printStackTrace()
            }

            return false
        }

        fun checkDayDifference(time: String, endtime: String, format1: String,
                               format2: String): Int {
            try {
                val date1 = SimpleDateFormat(format1).parse(time)
                val date2 = SimpleDateFormat(format1).parse(endtime)
                val difference = date2.time - date1.time
//                val days = (difference / (1000 * 60 * 60 * 24)).toInt()
                CommonUtils.printLog("DateDiff", "${date1?.compareTo(date2)!!}")
                return date1?.compareTo(date2)!!
            } catch (e: Exception) {
                e.printStackTrace()
            }

            return 0
        }

        fun getMinOfHour(time: Int): Int {
            return if (time in 1..15) {
                15
            } else if (time in 16..30) {
                30
            } else if (time in 31..45) {
                45
            } else if (time in 46..60) {
                60
            } /*else if (time > 60) {
            int leftTime = time - 60;
            return getMinOfHour(leftTime);
        }*/
            else {
                time
            }
        }

        fun getMinOfHourOfMinute(time: Int): Int {
            return if (time in 1..15) {
                15
            } else if (time in 16..30) {
                30
            } else if (time in 31..45) {
                45
            } else if (time in 46..60) {
                0
            } /*else if (time > 60) {
            int leftTime = time - 60;
            return getMinOfHour(leftTime);
        }*/
            else {
                0
            }
        }

        fun getHourIn12Format(time: Int): Int {
            return if (time <= 12) 12 else time - 12
        }

        fun getDayOfWeek(): Int {
            val c = Calendar.getInstance()
            val dayOfWeek = c.get(Calendar.DAY_OF_WEEK)

            if (Calendar.MONDAY == dayOfWeek) {
                return 1
            } else if (Calendar.TUESDAY == dayOfWeek) {
                return 2
            } else if (Calendar.WEDNESDAY == dayOfWeek) {
                return 3
            } else if (Calendar.THURSDAY == dayOfWeek) {
                return 4
            } else if (Calendar.FRIDAY == dayOfWeek) {
                return 5
            } else if (Calendar.SATURDAY == dayOfWeek) {
                return 6
            } else if (Calendar.SUNDAY == dayOfWeek) {
                return 7
            }
            return -1
        }


        fun getNameOfDay(dayOfWeek: Int): String {
            val calendar = Calendar.getInstance()
            calendar.set(Calendar.DAY_OF_WEEK, dayOfWeek)
            val days = arrayOf("Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat")
            val dayIndex = calendar.get(Calendar.DAY_OF_WEEK)
            return days[dayIndex]
        }

        fun getDurationString(seconds: Int): String {
            var seconds = seconds

            val hours = seconds / 3600
            val minutes = seconds % 3600 / 60
            seconds = seconds % 60
            val time = twoDigitString(hours) + ":" + twoDigitString(minutes) + ":" + twoDigitString(seconds)

            return if (time.startsWith("00:")) {
                time.substring(3)
            } else {
                time
            }
        }

        private fun twoDigitString(number: Int): String {

            if (number == 0) {
                return "00"
            }

            return if (number / 10 == 0) {
                "0$number"
            } else number.toString()

        }
    }
}
