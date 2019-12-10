package com.example.kotlinassignment

import android.app.Activity
import android.content.Context
import android.content.SharedPreferences
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.AsyncTask
import android.os.Build
import android.os.Bundle
import android.os.Parcelable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.kotlinassignment.Adapter.AssignmentKotlinAdapter
import com.example.kotlinassignment.DataModel.AssignmentModel
import com.example.kotlinassignment.Interface.MainView
import com.example.kotlinassignment.Presenter.MainPresenterImpl
import com.example.kotlinassignment.Util.API
import com.example.kotlinassignment.ViewModel.AssignmentViewModel
import kotlinx.android.synthetic.main.assignment_fragment_layout.*
import java.util.*


/**
 * Created by Sabeer Shaikh on 12/09/2019.
 *
 */
@Suppress("DEPRECATION", "DEPRECATED_IDENTITY_EQUALS")
class AssignmentFragment : Fragment(), MainView, SwipeRefreshLayout.OnRefreshListener {
    private lateinit var model: AssignmentViewModel
    private val LOADING_TAG = "MainActivity_LOADING"
    private val CONTENT_TAG = "MainActivity_CONTENT"
    private val STATE_TAG = "MainActivity_KeyForLayoutManagerState"
    private var mRecyclerView: RecyclerView? = null
    private var linearLayoutManager: LinearLayoutManager? = null
    private var mSwipeRefreshLayout: SwipeRefreshLayout? = null
    private var mMainPresenter: MainPresenterImpl? = null
    private var wasLoadingState = false
    private var wasRestoringState = false
    private var savedRecyclerLayoutState: Parcelable? = null
    private var sharedpreferences: SharedPreferences? = null
    private var listener: MainActivity? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (savedInstanceState != null) {
            wasLoadingState = savedInstanceState.getBoolean(LOADING_TAG, false)
            wasRestoringState = savedInstanceState.getBoolean(CONTENT_TAG, false)
            savedRecyclerLayoutState = savedInstanceState.getParcelable(STATE_TAG)

        }

    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Get the custom view for this fragment layout
        val view = inflater.inflate(R.layout.assignment_fragment_layout, container, false)
        init(view)
        //if network is not available
        if (!isInternetOn(listener!!)) {
            OfflineLoadData()
            listener!!.title = sharedpreferences!!.getString("ActivityTitle", "")
        }

        // Return the fragment view/layout
        return view
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is Activity) {
            this.listener = context as MainActivity
        }

    }

    private fun init(view: View) {

        //Initialisation of shared preference
        sharedpreferences =
            listener!!.getSharedPreferences(API().MyPREFERENCES, Context.MODE_PRIVATE)
        mRecyclerView = view.findViewById(R.id.recycler_view) as RecyclerView
        // Specify layout for recycler view
        linearLayoutManager = LinearLayoutManager(
            listener, RecyclerView.VERTICAL, false
        )
        mRecyclerView!!.layoutManager = linearLayoutManager
        // Creates the databases and initializes it.
        // Get the view model
        model = ViewModelProviders.of(listener!!).get(AssignmentViewModel::class.java)
        mSwipeRefreshLayout = view.findViewById(R.id.swipe_layout)
        // Setup refresh listener which triggers new data loading
        mSwipeRefreshLayout!!.setOnRefreshListener(this)
        // Configure the refreshing colors
        mSwipeRefreshLayout!!.setColorSchemeResources(
            android.R.color.holo_blue_bright,
            android.R.color.holo_green_light,
            android.R.color.holo_orange_light,
            android.R.color.holo_red_light
        )

    }

    private fun OfflineLoadData() {

        //create thread for fetching recodes from database
        //Fetching all recodes from database.
         model.allAssignmentData.observe(listener!!, Observer { assignmentModel ->
            // Data bind the recycler view
            mRecyclerView!!.adapter = AssignmentKotlinAdapter(assignmentModel)
        })

        if (savedRecyclerLayoutState != null) {
            Objects.requireNonNull<RecyclerView.LayoutManager>(recycler_view!!.layoutManager)
                .onRestoreInstanceState(savedRecyclerLayoutState)

        }
        savedRecyclerLayoutState = null

    }

    override fun onStart() {
        super.onStart()
        mMainPresenter = MainPresenterImpl(this)

        if (wasLoadingState) {
            // it was loading already so restart fetching anyway
            mMainPresenter!!.getDataForList(listener!!, false)
        } else {
            // it was not loading now it wither restores cached data or fetch from network
            mMainPresenter!!.getDataForList(listener!!, wasRestoringState)
        }
    }

    override fun onRefresh() {
        //force refresh
        if (isInternetOn(activity!!))
            mMainPresenter!!.getDataForList(listener!!, false)
        else
            hideProgress()

    }

    //set the recycler view cache for fast loading images
    private fun setRecyclerViewCache() {
        mRecyclerView!!.setHasFixedSize(true)
        mRecyclerView!!.setItemViewCacheSize(20)
        mRecyclerView!!.isDrawingCacheEnabled = true
        mRecyclerView!!.drawingCacheQuality = View.DRAWING_CACHE_QUALITY_HIGH
    }

    override fun onGetDataSuccess(list: List<AssignmentModel>) {
        addToDB(list)
        mRecyclerView!!.adapter = AssignmentKotlinAdapter(list)
        setRecyclerViewCache()
        if (savedRecyclerLayoutState != null) {
            Objects.requireNonNull<RecyclerView.LayoutManager>(recycler_view!!.layoutManager)
                .onRestoreInstanceState(savedRecyclerLayoutState)
        }

        savedRecyclerLayoutState = null
    }

    private fun addToDB(assignmentModelList: List<AssignmentModel>) {
        //Inserting data in room data
        model = ViewModelProviders.of(listener!!).get(AssignmentViewModel::class.java)
        AsyncTask.execute {
            for (element in assignmentModelList) {
                model.insert(element)
            }

        }
    }

    override fun onGetDataFailure(message: String) {
        Toast.makeText(listener!!.applicationContext, message, Toast.LENGTH_LONG).show()

    }

    override fun showProgress() {
        hideProgress()
        mSwipeRefreshLayout!!.isRefreshing = true
    }

    override fun hideProgress() {
        if (mSwipeRefreshLayout != null && mSwipeRefreshLayout!!.isRefreshing) {
            mSwipeRefreshLayout!!.isRefreshing = false
        }
    }


    override fun setMainTitle() {
        listener!!.title = sharedpreferences!!.getString("ActivityTitle", "")
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {

    }

    override fun onSaveInstanceState(outState: Bundle) {

        if (recycler_view != null) {
            // for data restoring purpose
            outState.putBoolean(CONTENT_TAG, true)
        } else {
            outState.putBoolean(CONTENT_TAG, false)
        }

        if (mSwipeRefreshLayout != null && mSwipeRefreshLayout!!.isRefreshing) {
            // saving the loading state
            outState.putBoolean(LOADING_TAG, true)
        } else {
            outState.putBoolean(LOADING_TAG, false)
        }

        outState.putParcelable(STATE_TAG, linearLayoutManager!!.onSaveInstanceState())

        super.onSaveInstanceState(outState)
    }

    override fun onViewStateRestored(savedInstanceState: Bundle?) {
        super.onViewStateRestored(savedInstanceState)
        if (savedInstanceState != null) {
            // Restore last state for checked position.
            val isRestoringVal: Boolean
            val isLoadingState: Boolean

            isRestoringVal = savedInstanceState.getBoolean(CONTENT_TAG, false)
            isLoadingState = savedInstanceState.getBoolean(LOADING_TAG, false)
            if (isLoadingState) {
                // it was loading already so restart fetching anyway
                mMainPresenter!!.getDataForList(listener!!.applicationContext, false)
            } else {
                // it was not loading then, now it whether restores cached data or fetch from network
                mMainPresenter!!.getDataForList(listener!!.applicationContext, isRestoringVal)
            }
            savedRecyclerLayoutState = savedInstanceState.getParcelable(STATE_TAG)
        }
    }

    override fun onStop() {
        mMainPresenter!!.onDestroy()
        super.onStop()
    }

    // This method is called when the fragment is no longer connected to the Activity
    // Any references saved in onAttach should be nulled out here to prevent memory leaks.
    override fun onDetach() {
        super.onDetach()
        this.listener = null
    }

    private fun isInternetOn(context: Context): Boolean {

        // get Connectivity Manager object to check connection
        val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        if (Build.VERSION.SDK_INT < 23) {
            val ni = cm.activeNetworkInfo

            if (ni != null) {
                return ni.isConnected && (ni.type == ConnectivityManager.TYPE_WIFI || ni.type == ConnectivityManager.TYPE_MOBILE)
            }
        } else {
            val n = cm.activeNetwork

            if (n != null) {
                val nc = cm.getNetworkCapabilities(n)

                return nc!!.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) || nc.hasTransport(
                    NetworkCapabilities.TRANSPORT_WIFI
                )
            }
        }

        return false
    }
}