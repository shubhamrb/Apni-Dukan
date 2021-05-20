package com.mponline.userApp.ui.fragment

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager
import com.mponline.userApp.R
import com.mponline.userApp.listener.OnItemClickListener
import com.mponline.userApp.listener.OnSwichFragmentListener
import com.mponline.userApp.model.response.CategorylistItem
import com.mponline.userApp.model.response.GetCategoriesResponse
import com.mponline.userApp.ui.adapter.ServicesAdapter
import com.mponline.userApp.ui.base.BaseFragment
import com.mponline.userApp.util.CommonUtils
import com.mponline.userApp.utils.Constants
import com.mponline.userApp.utils.ImageGlideUtils
import com.mponline.userApp.viewmodel.UserListViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_service.view.*
import kotlinx.android.synthetic.main.fragment_service.view.rv_services
import kotlinx.android.synthetic.main.layout_empty.*
import kotlinx.android.synthetic.main.layout_progress.*

@AndroidEntryPoint
class ServiceFragment : BaseFragment(), OnItemClickListener {
    override fun onNetworkChange(isConnected: Boolean) {

    }

    var mView: View? = null
    var mSwichFragmentListener: OnSwichFragmentListener? = null
    val viewModel: UserListViewModel by viewModels()
    var mCategoryObj: CategorylistItem? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mView = inflater!!.inflate(R.layout.fragment_service, container, false)

        return mView
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context != null) {
            mSwichFragmentListener = context as OnSwichFragmentListener
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        view?.relative_frag?.setOnClickListener {

        }
        arguments?.let {
            if (it?.containsKey("obj")) {
                val data: CategorylistItem? = arguments?.getParcelable<CategorylistItem>("obj")
                data?.let {
                    mCategoryObj = it
                    callSubCategory(it)
                }
            }
        }
        //Service


    }

    private fun callSubCategory(category: CategorylistItem) {
        if (CommonUtils.isOnline(activity!!)) {
            switchView(3, "")
            var commonRequestObj = getCommonRequestObj(
                apiKey = getApiKey(),
                category_id = category?.id!!
            )
            viewModel?.getSubCategories(commonRequestObj)?.observe(activity!!, Observer {
                it?.run {
                    if (status) {
                        switchView(1, "")
                        setDataToUI(this)
                    } else {
                        switchView(0, "")
                        CommonUtils.createSnackBar(
                            activity?.findViewById(android.R.id.content)!!,
                            resources?.getString(R.string.no_net)!!
                        )
                    }
                }
            })
        } else {
            CommonUtils.createSnackBar(
                activity?.findViewById(android.R.id.content)!!,
                resources?.getString(R.string.no_net)!!
            )
        }
    }

    fun setDataToUI(mGetCategoriesResponse: GetCategoriesResponse) {
        mGetCategoriesResponse?.let {
            view?.rv_services?.layoutManager =
                GridLayoutManager(
                    activity, 3
                )
            view?.rv_services?.adapter = ServicesAdapter(
                activity,
                this@ServiceFragment,
                it?.data!!
            )
        }
    }


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

    }

    override fun onStart() {
        super.onStart()
        mSwichFragmentListener?.onSwichToolbar(Constants.SHOW_NAV_DRAWER_TOOLBAR, "", null)
    }

    override fun onClick(pos: Int, view: View, obj: Any?) {
        when (view?.id) {
            R.id.cv_service -> {
                mSwichFragmentListener?.onSwitchFragment(
                    Constants.SUB_SERVICE_PAGE,
                    Constants.WITH_NAV_DRAWER,
                    obj,
                    mCategoryObj
                )
            }

        }
    }

    fun switchView(i: Int, msg: String) {
        mView?.run {
            when (i) {
                0 -> {
                    relative_progress?.visibility = View.GONE
                    relative_empty?.visibility = View.VISIBLE
                    ll_container?.visibility = View.GONE
                }
                1 -> {
                    relative_progress?.visibility = View.GONE
                    relative_empty?.visibility = View.GONE
                    ll_container?.visibility = View.VISIBLE
                }
                2 -> {
                    relative_progress?.visibility = View.GONE
                    relative_empty?.visibility = View.GONE
                }
                3 -> {
                    relative_progress?.visibility = View.VISIBLE
                    relative_empty?.visibility = View.GONE
                    ll_container?.visibility = View.GONE
                }
            }
        }
    }

    companion object {
        fun newInstance(
            context: Activity,
            category: CategorylistItem
        ): Fragment {
            val fragment = ServiceFragment()
            val bundle = Bundle()
            bundle.putParcelable("obj", category)
            fragment.arguments = bundle
            return fragment
        }
    }
}