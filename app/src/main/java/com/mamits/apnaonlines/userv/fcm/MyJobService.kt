package  com.mamits.apnaonlines.userv.fcm

import android.util.Log
import com.firebase.jobdispatcher.JobParameters
import com.firebase.jobdispatcher.JobService

/**
 * Created by vijay on 17/05/2019.
 */
class MyJobService : JobService() {

    override fun onStartJob(jobParameters: JobParameters): Boolean {
        Log.d(TAG, "Performing long running task in scheduled job")
        // TODO(developer): add long running task here.
        return false
    }

    override fun onStopJob(jobParameters: JobParameters): Boolean {
        return false
    }

    companion object {

        private const val TAG = "MyJobService"
    }
}
