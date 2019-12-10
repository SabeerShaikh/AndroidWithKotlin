package com.example.kotlinassignment.Presenter

import android.content.Context
import android.content.SharedPreferences
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.util.Log
import com.android.volley.DefaultRetryPolicy
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.kotlinassignment.DataModel.AssignmentDataManager
import com.example.kotlinassignment.DataModel.AssignmentModel
import com.example.kotlinassignment.Interface.GetDataListener
import com.example.kotlinassignment.Interface.MainInteractor
import com.example.kotlinassignment.R
import com.example.kotlinassignment.Util.API
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.util.*


/**
 * Created by Sabeer Shaikh on 12/10/2019.
 *
 */
@Suppress("DEPRECATION")
class MainInteractorImpl(mGetDatalistener: GetDataListener) : MainInteractor {
    private val REQUEST_TAG = "Demo-Network-Call"
    private var mGetDatalistener: GetDataListener? = null
    // private lateinit var model: AssignmentViewModel
    private var mRequestQueue: RequestQueue? = null
    private var sharedpreferences: SharedPreferences? = null
    private var mContext: Context? = null

    init {
        this.mGetDatalistener = mGetDatalistener

    }

    //Response from the Server
    private val onEQLoaded = Response.Listener<String> { response ->
        val assignmentModelsList = ArrayList<AssignmentModel>()
        val jsonObject: JSONObject
        var jsonArray: JSONArray? = null
        val mainTitle: String
        Log.d("Response--", response)
        try {
            jsonObject = JSONObject(response)
            mainTitle = jsonObject.getString("title")
            jsonArray = jsonObject.getJSONArray("rows")
            val editor = sharedpreferences!!.edit()
            editor.putString("ActivityTitle", mainTitle)
            editor.apply()

        } catch (e: JSONException) {
            e.printStackTrace()
        }

        try {
            var jsonObject1: JSONObject
            //Fetching data and adding data into model arraylist and storing in room database
            assert(jsonArray != null)
            for (i in 0 until jsonArray!!.length()) {
                jsonObject1 = jsonArray.getJSONObject(i)
                val title = jsonObject1.getString("title")
                val description = jsonObject1.getString("description")
                val imageHref = jsonObject1.getString("imageHref")
                if (!title.contains("null") && !description.contains("null") && !imageHref.contains(
                        "null"
                    )
                ) {
                    val assignmentModel = AssignmentModel(title, description, imageHref)
                    //adding to room database
                    assignmentModelsList.add(assignmentModel)
                }

            }

            mGetDatalistener.onSuccess(
                mContext!!.getString(R.string.success),
                assignmentModelsList
            )

        } catch (ex: JSONException) {
            mGetDatalistener.onFailure(ex.toString())

        }
    }


    override fun provideData(context: Context, isRestoring: Boolean) {
        //providing data on screen orientation
        val shouldLoadFromNetwork: Boolean
        if (isRestoring) {

            val existingData = AssignmentDataManager().getLatestData()

            if (existingData != null && !existingData.isEmpty()) {
                // we have cached copy of data for restoring purpose
                shouldLoadFromNetwork = false

                mGetDatalistener!!.onSuccess(mContext!!.getString(R.string.restore), existingData)
            } else {
                shouldLoadFromNetwork = true
            }
        } else {
            shouldLoadFromNetwork = true
        }

        if (shouldLoadFromNetwork) {

            if (isInternetOn(context)) {
                this.initNetworkCall(context)
            } else {

                mGetDatalistener!!.onFailure("No internet connection.")
            }
        }
    }

    private val onEQError =
        Response.ErrorListener { error -> mGetDatalistener.onFailure(error.toString()) }

    private fun initNetworkCall(context: Context) {

        cancelAllRequests()
        mContext = context
        sharedpreferences = context.getSharedPreferences(API().MyPREFERENCES, Context.MODE_PRIVATE)
        // Get the view model
        //model = ViewModelProviders.of(MainActivity()).get(AssignmentViewModel::class.java)
        //mDatabase = Room.databaseBuilder(context, AppDatabase::class.java, "production")
        //  .build()
        mRequestQueue = Volley.newRequestQueue(context)

        val request = StringRequest(Request.Method.GET, API().ASS_URL, onEQLoaded, onEQError)
        request.retryPolicy = DefaultRetryPolicy(
            10000, /* 10 sec timeout policy */
            0, /*no retry*/
            DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
        )

        request.tag = REQUEST_TAG
        mRequestQueue!!.add(request)

    }

    private fun cancelAllRequests() {
        if (mRequestQueue != null) {
            mRequestQueue!!.cancelAll(REQUEST_TAG)
        }
    }

    override fun onDestroy() {
        cancelAllRequests()
    }


    private fun isInternetOn(context: Context): Boolean {

        // get Connectivity Manager object to check connection
        val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        if (Build.VERSION.SDK_INT < 23) {
            val ni = cm.activeNetworkInfo

            if (ni != null) return ni.isConnected && (ni.type == ConnectivityManager.TYPE_WIFI || ni.type == ConnectivityManager.TYPE_MOBILE)
        } else {
            val network = cm.activeNetwork

            if (network != null) {
                val nc = cm.getNetworkCapabilities(network)!!

                return nc.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) || nc.hasTransport(
                    NetworkCapabilities.TRANSPORT_WIFI
                )
            }
        }

        return false
    }

}